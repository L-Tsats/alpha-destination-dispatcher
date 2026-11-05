
package dispatcher.sim.http;

import dispatcher.sim.elevator.Elevator;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class ElevatorHttpServer {

    private final HttpServer server;
    private final Elevator elevator;

    public ElevatorHttpServer(Elevator elevator) throws IOException {
        this.elevator = elevator;

        server = HttpServer.create(
            new InetSocketAddress("127.0.0.1", 8081), 0
        );

        server.createContext("/elevators/" + elevator.getId() + "/floor", this::getFloor);
    }

    private void getFloor(HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod().equals("GET")) {
            sendResponse(exchange, 405, "Method not allowed");
            return;
        }

        if (!exchange.getRequestURI().getPath().equals("/elevators/" +elevator.getId() + "/floor")) {
            sendResponse(exchange, 404, "Not found");
            return;
        }

        int floor = elevator.getCurrentFloor();

        String response = "{\"id\":\"" + elevator.getId()
                        + "\",\"floor\":" + floor + "}";

        sendResponse(exchange, 200, response);
    }

    private void sendResponse(
            HttpExchange exchange,
            int status,
            String response) throws IOException {

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
            "Content-Type", "application/json; charset=UTF-8"
        );

        exchange.sendResponseHeaders(status, bytes.length);

        try (var output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    public void start() {
        server.start();
        System.out.println("HTTP Server running on port 8081");
    }
}
