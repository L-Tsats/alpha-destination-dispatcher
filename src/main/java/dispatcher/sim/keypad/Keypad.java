package dispatcher.sim.keypad;

import dispatcher.sim.http.BrainApiClient;
import dispatcher.sim.http.PassengerRequest;
import dispatcher.sim.http.AssignmentResponse;

import java.io.IOException;

public class Keypad {

    private final BrainApiClient client = new BrainApiClient("http://localhost:8080");
    private String elevatorID;

    private void sendRequest() throws IOException, InterruptedException{
        PassengerRequest req = new PassengerRequest(1, 5); //Request to go from floor 1 to floor 5 testing purposes

        AssignmentResponse answer = client.submitRequest(req);
        elevatorID = answer.elevatorID();

    }
    public static void main(String[] args)
        throws IOException, InterruptedException {

        Keypad keypad = new Keypad();
        keypad.sendRequest();
    }
}
