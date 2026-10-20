/*
 * EXERCISE 44: HTTP client  (talking to a web server from Java)
 * =============================================================
 * BRIDGE RUNG: this is where "learning Java" meets the group project. The web
 * runs on the client-server model: a client sends an HTTP request to a server,
 * the server sends back a response. This rung is the CLIENT side — making a
 * request and reading the reply. (Rung 45 is the server side.)
 *
 * HTTP in one breath
 * ------------------
 * A client opens a connection to a URL, picks a METHOD (GET to fetch, POST to
 * send data, etc.), and reads the response: a status code (200 OK, 404 Not
 * Found, ...) plus a body (HTML, JSON, text...).
 *
 * THE RECIPE (classic java.net approach, from the Spinellis UrlRetriever)
 * ----------------------------------------------------------------------
 *   URL url = new URL("https://example.com");
 *   HttpURLConnection conn = (HttpURLConnection) url.openConnection();
 *   conn.setRequestMethod("GET");
 *   int status = conn.getResponseCode();            // e.g. 200
 *   BufferedReader in = new BufferedReader(
 *       new InputStreamReader(conn.getInputStream()));
 *   // read lines from 'in' until null, append to a StringBuilder
 *
 * EXCEPTIONS: new URL(...) throws MalformedURLException; the connection/read
 * throw IOException. Wrap in try/catch (catch Exception is fine for the exercise).
 *
 * Imports:
 *   import java.io.BufferedReader;
 *   import java.io.InputStreamReader;
 *   import java.net.HttpURLConnection;
 *   import java.net.URL;
 *
 * NOTE: needs an internet connection to actually fetch. If offline, you can
 * point it at the local server from rung 45 (http://localhost:8000/).
 *
 * ------------------------------------------------------------------
 * TASK 1 — fetch a URL and print its status + body
 * ------------------------------------------------------------------
 * - Build a URL (hardcode one, e.g. "https://example.com", or read args[0]).
 * - Open an HttpURLConnection, setRequestMethod("GET").
 * - Print the response code (conn.getResponseCode()).
 * - Read the body line-by-line via a BufferedReader and print it.
 * - Wrap it all in try/catch.
 *
 * ------------------------------------------------------------------
 * TASK 2 — read a header
 * ------------------------------------------------------------------
 * After connecting, also print one response header, e.g.
 *   conn.getHeaderField("Content-Type")
 * so you see that a response is status + headers + body.
 *
 */
