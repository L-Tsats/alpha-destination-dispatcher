
package dispatcher.sim.elevator;

import dispatcher.sim.http.NextDestinationResponse;
import dispatcher.sim.http.PassengerRequest;
import dispatcher.sim.http.AssignmentResponse;
import dispatcher.sim.http.BrainApiClient;

import java.io.IOException;
public class Elevator {

    private int currentFloor;
    private final String id;
    
    public Elevator(String id, int startingFloor) {
        this.id = id;
        this.currentFloor = startingFloor;
    }

    public String getId() {
        return id;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(int floor) {
        this.currentFloor = floor;
    }

    public static void main(String args[]) throws IOException, InterruptedException{
        BrainApiClient client = new BrainApiClient("http://localhost:8080");
        Elevator e1 = new Elevator("B", 1);

        System.out.println(client.getNextDest(e1.id).nextFloor());

    }
}
