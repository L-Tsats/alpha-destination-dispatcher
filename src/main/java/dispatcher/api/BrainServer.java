package dispatcher.api;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

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

    public BrainServer(int port, int nFloors, int nCars) throws IOException {
        brain = new BrainCore(nFloors, nCars);
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/requests", this::handleRequest);
        server.createContext("/api/elevator", this::handleElevator);
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

        Integer nextFloor;
        synchronized (brain) {
            nextFloor = brain.nextDestination(elevatorId, now());
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
}
