package dispatcher.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import dispatcher.api.dto.AssignmentResponse;
import dispatcher.api.dto.NextDestinationResponse;
import dispatcher.api.dto.PassengerRequest;
import dispatcher.dispatch.brain.BrainCore;
import dispatcher.dispatch.eval.PlanEvaluation;

public class BrainServer {
    private final HttpServer server;
    private final ExecutorService executor;
    private final BrainCore brain;
    private final Gson gson = new Gson();
    private final long startNanos = System.nanoTime();
    private long pollTimeoutMs = 25_000;
    private final int nFloors;
    private final int nCars;
    private PrintWriter eventLog = null;   // null = no log

    public BrainServer(int port, int nFloors, int nCars) throws IOException {
        this.nFloors = nFloors;
        this.nCars = nCars;
        brain = new BrainCore(nFloors, nCars);
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/requests", this::handleRequest);
        server.createContext("/api/elevators/", this::handleElevator);

        // One thread per open request, so a waiting elevator never blocks a keypad request.
        // Plain threads, not virtual ones: on JDK 21 a virtual thread waiting inside
        // synchronized stays pinned to its carrier thread.
        executor = Executors.newCachedThreadPool();
        server.setExecutor(executor);
    }

    public void start() { server.start(); }

    public void stop() {
        server.stop(0);
        executor.shutdownNow();
        synchronized (brain) {
            if (eventLog != null) eventLog.close();
        }
    }

    public int port()   { return server.getAddress().getPort(); }

    // Seconds since the server started: the brain's clock in real-time mode
    private double now() {
        return (System.nanoTime() - startNanos) / 1e9;
    }

    // ---------- settings and results (used by app/Main) ----------

    // true: plan each elevator's stops with DirectionalSearch (LOOK-like); false: tree search
    public void setDirectionalSearch(boolean directional) {
        synchronized (brain) {
            brain.setDirectionalSearch(directional);
        }
    }

    // Write every request and every handed-out destination as one JSON line to this file
    public void setEventLog(Path file) throws IOException {
        synchronized (brain) {
            eventLog = new PrintWriter(Files.newBufferedWriter(file, StandardCharsets.UTF_8));
        }
    }

    // A copy of the brain's waits and journeys so far (safe to read while elevators run)
    public PlanEvaluation results() {
        synchronized (brain) {
            PlanEvaluation r = brain.result();
            return new PlanEvaluation(new HashMap<>(r.getPickupWaits()), new HashMap<>(r.getDeliveryWaits()));
        }
    }

    // True when every elevator has nothing left to do. An elevator only counts as idle after it
    // finished its last stop (doors closed) and asked again with an empty route.
    // Use it once no more requests can arrive: then "all idle" means everyone has been served.
    public boolean allIdle() {
        synchronized (brain) {
            for (int i = 0; i < nCars; i++) {
                if (!brain.getCar(String.valueOf((char) ('A' + i))).isIdle()) return false;
            }
            return true;
        }
    }

    public void setPollTimeoutMs(long ms) { this.pollTimeoutMs = ms; }

    // ---------- HTTP handlers ----------

    private void handleRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("POST")) {
            sendJson(exchange, 405, "{\"error\":\"POST only\"}");
            return;
        }
        PassengerRequest req;
        try {
            req = gson.fromJson(readBody(exchange), PassengerRequest.class);
        } catch (JsonParseException e) {
            sendJson(exchange, 400, "{\"error\":\"body is not valid JSON\"}");
            return;
        }
        String problem = validate(req);
        if (problem != null) {
            sendJson(exchange, 400, gson.toJson(Map.of("error", problem)));
            return;
        }

        BrainCore.Ack ack;
        synchronized (brain) {
            double t = now();
            ack = brain.submit(req.getFromFloor(), req.getToFloor(), t, null);
            logEvent(t, "request", Map.of(
                    "requestId", ack.requestId(), "from", req.getFromFloor(),
                    "to", req.getToFloor(), "elevator", ack.elevatorId()));
            brain.notifyAll();
        }
        AssignmentResponse assignment = new AssignmentResponse(ack.elevatorId());
        sendJson(exchange, 200, gson.toJson(assignment));
    }

    private void handleElevator(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("GET")) {
            sendJson(exchange, 405, "{\"error\":\"GET only\"}");
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        if (parts.length != 5 || !parts[4].equals("next-destination")) {
            try (exchange) {
                exchange.sendResponseHeaders(404, -1);
            }
            return;
        }
        String elevatorId = parts[3];

        if(!brain.hasElevator(elevatorId)) {
            try (exchange) {
                exchange.sendResponseHeaders(404, -1);
            }
            return;
        }

        // Long polling: if there is no stop yet, hold the request open until a keypad
        // request wakes us (notifyAll in handleRequest) or the timeout passes.
        Integer nextFloor;
        synchronized (brain) {
            long deadline = System.currentTimeMillis() + pollTimeoutMs;
            nextFloor = brain.nextDestination(elevatorId, now());
            while (nextFloor == null) {
                long left = deadline - System.currentTimeMillis();
                if (left <= 0) break;                 // timed out: answer 204, the elevator asks again
                try {
                    brain.wait(left);                 // releases the lock while sleeping
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                nextFloor = brain.nextDestination(elevatorId, now());
            }
            if (nextFloor != null) {
                logEvent(now(), "next-destination", Map.of("elevator", elevatorId, "floor", nextFloor));
            }
        }

        if (nextFloor == null) {
            try (exchange) {
                exchange.sendResponseHeaders(204, -1);
            }
        } else {
            sendJson(exchange, 200, gson.toJson(new NextDestinationResponse(nextFloor)));
        }

    }

    // Returns what is wrong with a request, or null if the brain can accept it
    private String validate(PassengerRequest req) {
        if (req == null) return "empty request";
        int from = req.getFromFloor();
        int to = req.getToFloor();
        if (from < 1 || from > nFloors) return "fromFloor must be between 1 and " + nFloors;
        if (to < 1 || to > nFloors)     return "toFloor must be between 1 and " + nFloors;
        if (from == to)                 return "fromFloor and toFloor must differ";
        return null;
    }

    // --- plumbing ---

    // Call only while holding the brain lock (keeps lines in order)
    private void logEvent(double t, String type, Map<String, Object> fields) {
        if (eventLog == null) return;
        Map<String, Object> line = new LinkedHashMap<>();
        line.put("t", Math.round(t * 1000) / 1000.0);
        line.put("type", type);
        line.putAll(fields);
        eventLog.println(gson.toJson(line));
        eventLog.flush();
    }

    private String readBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (var out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }
}
