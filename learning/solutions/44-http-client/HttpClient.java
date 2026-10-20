// EXERCISE 44 - HTTP client (reference solution)
// Fetches a URL with GET, prints status, a header, and the body.
// Needs internet; or point BASE at the rung-45 local server.

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient {

    public static void main(String[] args) {
        String target = (args.length == 1) ? args[0] : "https://example.com";

        try {
            URL url = new URL(target);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Task 1 — status code
            int status = conn.getResponseCode();
            System.out.println("Status: " + status);

            // Task 2 — one header
            System.out.println("Content-Type: " + conn.getHeaderField("Content-Type"));

            // Task 1 — body, line by line
            BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                body.append(line).append(System.lineSeparator());
            }
            in.close();
            System.out.println("--- body ---");
            System.out.println(body.toString());

        } catch (Exception e) {
            System.err.println("Request failed: " + e.getMessage());
        }
    }
}
