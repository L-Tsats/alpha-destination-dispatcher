
package dispatcher.sim.Brain;

import com.sun.net.httpserver.HttpServer;
import com.google.gson.Gson;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

import dispatcher.sim.http.PassengerRequest;
import dispatcher.sim.http.AssignmentResponse;

public class Brain {
    private static final String elevatorId = "B"; //Testing purposes

    public static void main(String[] args) throws IOException{
        
        //Request:

        HttpServer server = HttpServer.create(
            new InetSocketAddress(8080), 0
        );
        server.createContext("/api/requests", exchange -> {
            Gson gson = new Gson();

            String json = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
            );

            PassengerRequest req = gson.fromJson(
                json,
                PassengerRequest.class
            );

            System.out.println(req.getFromFloor());
            System.out.println(req.getToFloor());

            //Response:


            AssignmentResponse assignment =
                new AssignmentResponse(elevatorId);

            String responseJson = gson.toJson(assignment);
            
            byte[] bytes = responseJson.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set(
                "Content-Type", "application/json"
            );

            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes); //Sends the bytes of the json response
            exchange.close();



        });
        server.start();
    }
}