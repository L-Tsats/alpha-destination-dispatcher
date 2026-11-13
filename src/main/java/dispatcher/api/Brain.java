
package dispatcher.api;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;

import dispatcher.api.dto.AssignmentResponse;
import dispatcher.api.dto.NextDestinationResponse;
import dispatcher.api.dto.PassengerRequest;

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

        //Elevator requests
        server.createContext("/api/elevators/", exchange -> {
            
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length != 5 ||
                !parts[1].equals("api") ||
                !parts[2].equals("elevators") ||
                parts[3].isBlank() ||
                !parts[4].equals("next-destination")) {

                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }



            String savedElevatorId = parts[3]; //Testing purposes


            Gson gson = new Gson();

            System.out.println(savedElevatorId);

            if (savedElevatorId.equals("B")) { //Testing purposes
                NextDestinationResponse x = new NextDestinationResponse(5); //Testing purposes

                String responseJson = gson.toJson(x);
                
                byte[] bytes = responseJson.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set(
                    "Content-Type", "application/json"
                );

                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes); //Sends the bytes of the json response
                exchange.close();


            } else {
                System.out.println("lauos");
            }



        });
        server.start();
        
    }

}
