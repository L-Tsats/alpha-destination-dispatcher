
package dispatcher.sim.elevator;

//import dispatcher.sim.http.NextDestinationResponse;
//import dispatcher.sim.http.PassengerRequest;
//import dispatcher.sim.http.AssignmentResponse;
import dispatcher.sim.http.BrainApiClient;
import dispatcher.sim.http.ElevatorHttpServer;

import java.io.IOException;

public class Elevator {
    
    public enum ElevatorState {
        IDLE,
        MOVING,
        DOORS_OPENING,
        DOORS_OPEN,
        DOORS_CLOSING,
    }

    private static final int T_START_MS = 500;
    private static final int T_DOOR_MS = 4000;//οσο κανει να ανοιξει και να κλεισει η πορτα

    private int currentFloor;
    private String id;
    private ElevatorState currentState;
    
    public Elevator(String id, int startingFloor) {
        this.id = id;
        this.currentFloor = startingFloor;
        this.currentState = ElevatorState.IDLE;
    }

    public String getId() {
        return id;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public ElevatorState getCurrentState() {
        return currentState;
    }

    public void setCurrentFloor(int floor) {
        this.currentFloor = floor;
    }

    public void movetoFloor( int targetFloor) throws InterruptedException {
        if (targetFloor == currentFloor) {
            System.out.println("Elevator " + id + " is already on floor " + targetFloor);
            return;
        }
        System.out.println("Motor is starting");
        Thread.sleep(T_START_MS); // περιμενει μισο δευτερολεπτο πριν ξεκινησει
        
        currentState = ElevatorState.MOVING;
        System.out.println(">>> Next stop is floor" + targetFloor + " <<<");

        
        while (currentFloor != targetFloor) {
            Thread.sleep(1000); // περιμενει ενα δευτερολεπτο
            if (targetFloor > currentFloor) {
                currentFloor++;
            } else {
                currentFloor--;
            }
            System.out.println("Elevator " + id + " is passing floor " + currentFloor);
        }

        System.out.println("Elevator " + id + " has arrived at floor " + currentFloor);

        currentState = ElevatorState.DOORS_OPENING;
        System.out.println("Doors are opening now");
        Thread.sleep(T_DOOR_MS/3);// ενα μερος των 4 δευτερολεπτων 
        currentState = ElevatorState.DOORS_OPEN;
        System.out.println("Doors are open now/passengers please board or exit");
        Thread.sleep(T_DOOR_MS/3);// ενα μερος των 4 δευτερολεπτων
        currentState = ElevatorState.DOORS_CLOSING;
        System.out.println("Doors are closing now");
        Thread.sleep(T_DOOR_MS/3);// ενα μερος των 4 δευτερολεπτων
        currentState = ElevatorState.IDLE;
        System.out.println("Elevator " + id + " is now idle");
    }
    @Override 
    public String toString() {
        return "Elevator " + id + " is currently on floor " + currentFloor + " and is " + currentState;
    }

    public static void main(String args[]) throws IOException, InterruptedException{
        if (args.length < 2) {
            System.out.println("please provide the elevator id and starting floor ");
            return;
        }
        String elevatorId = args[0];
        int startingFloor = Integer.parseInt(args[1]);
        BrainApiClient client = new BrainApiClient("http://localhost:8080");
        Elevator e1 = new Elevator(elevatorId, startingFloor);
        ElevatorHttpServer server = new ElevatorHttpServer(e1);
        server.start();
        
        int nextFloor = client.getNextDest(e1.getId()).nextFloor();
        System.out.println("Brain gave command to move to floor: " + nextFloor);
        e1.movetoFloor(nextFloor);

    }
}
