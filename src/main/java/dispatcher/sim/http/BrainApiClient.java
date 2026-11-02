package dispatcher.sim.http;

import java.net.URI;
import com.sun.net.httpserver.HttpServer;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import java.io.IOException;


public class BrainApiClient {

    private final URI baseURI;

    public BrainApiClient(String brainURL){

        String url = brainURL.endsWith("/")
            ? brainURL
            : brainURL + "/";
        this.baseURI = URI.create(url);
    }

    public AssignmentResponse submitRequest(PassengerRequest req) throws IOException, InterruptedException {

        Gson g = new Gson();

        String json = g.toJson(req);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(baseURI.resolve("api/requests"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpClient client = HttpClient.newHttpClient();

        //Response of the brain
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException(
                "Brain returned HTTP " + response.statusCode()
            );
        }

        AssignmentResponse assignment = g.fromJson(
            response.body(),
            AssignmentResponse.class
        );

        return assignment;

    }
}