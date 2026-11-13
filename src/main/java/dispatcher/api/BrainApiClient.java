package dispatcher.api;

import dispatcher.api.dto.AssignmentResponse;
import dispatcher.api.dto.NextDestinationResponse;
import dispatcher.api.dto.PassengerRequest;

import java.net.URI;
import com.sun.net.httpserver.HttpServer;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;

import java.io.IOException;


public class BrainApiClient {

    private final URI baseURI;
    //private final NextDestinationResponse x = new NextDestinationResponse(5); //Testing purposes only

    //Correct URL syntax check
    public BrainApiClient(String brainURL){

        String url = brainURL.endsWith("/")
            ? brainURL
            : brainURL + "/";
        this.baseURI = URI.create(url);
    }


    //Communication with Keypad

    public AssignmentResponse submitRequest(PassengerRequest req) throws IOException, InterruptedException {

        Gson g = new Gson();

        //req from PassengerRequest type to json
        String json = g.toJson(req);

        //Creation of request
        HttpRequest request = HttpRequest.newBuilder()
            .uri(baseURI.resolve("api/requests"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpClient client = HttpClient.newHttpClient();

        //Response of the brain + sending the request
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        //Failsafe
        if (response.statusCode() != 200) {
            throw new IOException(
                "Brain returned HTTP " + response.statusCode()
            );
        }


        //Transformation of response from json to AssignmentResponse and return
        AssignmentResponse assignment = g.fromJson(
            response.body(),
            AssignmentResponse.class
        );

        return assignment;

    }

    //Communication with Elevator

    public NextDestinationResponse getNextDest(String elevatorID) throws IOException, InterruptedException{
        
        HttpRequest request = HttpRequest.newBuilder().uri(baseURI.resolve("api/elevators/" + elevatorID + "/next-destination")).GET().build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException(
                "Brain returned HTTP " + response.statusCode()
            );
        }

        Gson g = new Gson();
        NextDestinationResponse nextDest = g.fromJson(
            response.body(),
            NextDestinationResponse.class
        );

        return nextDest;
        
    }
}