/*
 * EXERCISE 45: HTTP server  (your project's central brain, in miniature)
 * ======================================================================
 * BRIDGE RUNG: this is the single most project-relevant exercise. Your elevator
 * system's CENTRAL BRAIN is an HTTP server that elevators and keypads call. This
 * rung builds a tiny one with Java's built-in com.sun.net.httpserver — the exact
 * pattern (from the Spinellis TimeServer) you'll scale up for the brain.
 *
 * THE RECIPE
 * ----------
 *   HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
 *   server.createContext("/", exchange -> { ... handle a request ... });
 *   server.start();
 * A "context" maps a URL path to a HANDLER. The handler gets an HttpExchange:
 *   - read the request (method, path, body) from it,
 *   - write a response: set status + length, then write bytes to the body stream.
 *
 * MINIMAL HANDLER SHAPE:
 *   HttpHandler h = exchange -> {
 *       byte[] body = "hello".getBytes();
 *       exchange.sendResponseHeaders(200, body.length);  // status + length
 *       try (OutputStream os = exchange.getResponseBody()) {
 *           os.write(body);
 *       }
 *   };
 *
 * HOW TO TEST IT: run this, then in a browser or terminal hit the URL:
 *   browser:  http://localhost:8000/
 *   terminal: curl http://localhost:8000/
 *   or point rung-44's client at http://localhost:8000/
 * The server runs until you stop it (Ctrl+C / the debugger stop button).
 *
 * Imports:
 *   import com.sun.net.httpserver.HttpServer;
 *   import com.sun.net.httpserver.HttpHandler;      (if you name the handler)
 *   import com.sun.net.httpserver.HttpExchange;
 *   import java.net.InetSocketAddress;
 *   import java.io.OutputStream;
 *   import java.io.IOException;
 *
 * ------------------------------------------------------------------
 * TASK 1 — serve the current time at "/"
 * ------------------------------------------------------------------
 * Create a server on port 8000, add a handler at "/" that responds (200) with
 * the current date/time as text. Build the string however you like
 * (e.g. java.time.LocalDateTime.now().toString()), turn it into bytes, send
 * headers with the length, write the bytes, close the stream. Start the server
 * and print a line so you know it's up. Test it in a browser.
 *
 * ------------------------------------------------------------------
 * TASK 2 — a second endpoint, and echo the path
 * ------------------------------------------------------------------
 * Add a second context, e.g. "/hello", that responds with "Hello!". Also, in a
 * handler, read and print the request method and path from the exchange
 * (exchange.getRequestMethod(), exchange.getRequestURI().getPath()) so you see
 * what the client asked for. This previews routing: different paths -> different
 * responses = exactly how the brain will expose /api/requests, /api/elevators/...
 *
 */
