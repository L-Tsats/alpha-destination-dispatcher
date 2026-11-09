package dispatcher.sim.elevator;

//import dispatcher.sim.http.NextDestinationResponse;
//import dispatcher.sim.http.PassengerRequest;
//import dispatcher.sim.http.AssignmentResponse;
import dispatcher.sim.http.BrainApiClient;
import dispatcher.sim.http.ElevatorHttpServer;

import java.io.IOException;

public class ElevatorRun {

    private static final int T_START_MS = 500;
    private static final int T_DOOR_MS = 4000;

    private Elevator elevator;

    public ElevatorRun(Elevator elevator) {
        this.elevator = elevator;
    }

    public void movetoFloor(int targetFloor) throws InterruptedException {
        if (targetFloor == elevator.getCurrentFloor()) {
            System.out.println("Elevator " + elevator.getId() + " is already on floor " + targetFloor);
            return;
        }
        System.out.println("Motor is starting");
        Thread.sleep(T_START_MS);// περιμενει μισο δευτερολεπτο πριν ξεκινησει

        elevator.setCurrentState(ElevatorState.MOVING);
        System.out.println(">>> Next stop is floor " + targetFloor + " <<<");

        
        while (elevator.getCurrentFloor() != targetFloor) {
            Thread.sleep(1000);
            if (targetFloor > elevator.getCurrentFloor()) {
                elevator.setCurrentFloor(elevator.getCurrentFloor() + 1);
            } else {
                elevator.setCurrentFloor(elevator.getCurrentFloor() - 1);
            }

            System.out.println("Elevator " + elevator.getId()+ " is passing floor "+ elevator.getCurrentFloor());
        }

        System.out.println("Elevator " + elevator.getId()+ " has arrived at floor "+ elevator.getCurrentFloor());

        elevator.setCurrentState(ElevatorState.DOORS_OPENING);
        System.out.println("Doors are opening now");
        Thread.sleep(T_DOOR_MS / 3);// ενα μερος των 4 δευτερολεπτων 
        
        elevator.setCurrentState(ElevatorState.DOORS_OPEN);
        System.out.println("Doors are open now/passengers please board or exit");
        Thread.sleep(T_DOOR_MS / 3);
        
        elevator.setCurrentState(ElevatorState.DOORS_CLOSING);
        System.out.println("Doors are closing now");
        Thread.sleep(T_DOOR_MS / 3);

        elevator.setCurrentState(ElevatorState.IDLE);
        System.out.println("Elevator " + elevator.getId() + " is now idle");
    }

    public static void main(String args[]) throws IOException, InterruptedException {
        if (args.length < 2) {
            System.out.println("please provide the elevator id and starting floor ");
            return;
        }

        String elevatorId = args[0];
        int startingFloor = Integer.parseInt(args[1]);
        BrainApiClient client =new BrainApiClient("http://localhost:8080");
        Elevator e1 = new Elevator(elevatorId, startingFloor);
        ElevatorHttpServer server = new ElevatorHttpServer(e1);
        server.start();

        ElevatorRun runner = new ElevatorRun(e1);
        int nextFloor = client.getNextDest(e1.getId()).nextFloor();

        System.out.println("Brain gave command to move to floor: " + nextFloor);
        runner.movetoFloor(nextFloor);
    }
}
