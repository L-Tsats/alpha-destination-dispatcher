package dispatcher.api;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import dispatcher.api.dto.AssignmentResponse;
import dispatcher.api.dto.NextDestinationResponse;
import dispatcher.api.dto.PassengerRequest;
import dispatcher.dispatch.brain.BrainCore;

public class BrainServer {
    private final HttpServer server;
    private final BrainCore brain;
    private final Gson gson = new Gson();
    private final long startNanos = System.nanoTime();
    private long pollTimeoutMs = 25_000;

    public BrainServer(int port, int nFloors, int nCars) throws IOException {
        brain = new BrainCore(nFloors, nCars);
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/requests", this::handleRequest);
        server.createContext("/api/elevators/", this::handleElevator);

        // One thread per open request, so a waiting elevator never blocks a keypad request.
        // Plain threads, not virtual ones: on JDK 21 a virtual thread waiting inside
        // synchronized stays pinned to its carrier thread.
        server.setExecutor(Executors.newCachedThreadPool());
    }

    public void start() { server.start(); }
    public void stop()  { server.stop(0); }
    public int port()   { return server.getAddress().getPort(); }

    // Seconds since the server started: the brain's clock in real-time mode
    private double now() {
        return (System.nanoTime() - startNanos) / 1e9;
    }

    private void handleRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("POST")) {
            sendJson(exchange, 405, "{\"error\":\"POST only\"}");
            return;
        }
        PassengerRequest req = gson.fromJson(readBody(exchange), PassengerRequest.class);

        BrainCore.Ack ack;
        synchronized (brain) {
            ack = brain.submit(req.getFromFloor(), req.getToFloor(), now(), null);
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
        }

        if (nextFloor == null) {
            try (exchange) {
                exchange.sendResponseHeaders(204, -1);
            }
        } else {
            sendJson(exchange, 200, gson.toJson(new NextDestinationResponse(nextFloor)));
        }

    }

    // --- plumbing ---
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

    public void setPollTimeoutMs(long ms) { this.pollTimeoutMs = ms; }

}
