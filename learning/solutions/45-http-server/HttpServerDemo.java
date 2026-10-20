// EXERCISE 45 - HTTP server (reference solution)
// A tiny server: "/" returns the time, "/hello" greets. This is the pattern
// the project's central brain scales up (routing paths -> handlers).
// Test: run it, then open http://localhost:8000/ and http://localhost:8000/hello

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.net.InetSocketAddress;
import java.io.OutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

public class HttpServerDemo {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        // Task 1 — "/" returns the current time
        server.createContext("/", exchange -> {
            logRequest(exchange);
            String text = "Current time: " + LocalDateTime.now() + "\n";
            respond(exchange, 200, text);
        });

        // Task 2 — "/hello" greets
        server.createContext("/hello", exchange -> {
            logRequest(exchange);
            respond(exchange, 200, "Hello!\n");
        });

        server.start();
        System.out.println("Server running on http://localhost:8000/  (Ctrl+C to stop)");
    }

    // Print what the client asked for (method + path)
    private static void logRequest(HttpExchange exchange) {
        System.out.println(exchange.getRequestMethod() + " "
                + exchange.getRequestURI().getPath());
    }

    // Send a text response: status + length, then the bytes.
    private static void respond(HttpExchange exchange, int status, String body)
            throws IOException {
        byte[] bytes = body.getBytes();
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
