package dispatcher.devices.elevator;

import java.io.IOException;

import dispatcher.api.BrainApiClient;
import dispatcher.api.dto.NextDestinationResponse;
import dispatcher.timing.TravelModel;

// One elevator. It runs on its own thread: ask the brain where to go, go there, ask again.
public class ElevatorController implements Runnable {
    int T_DOOR_MS = 8000; // Time for doors to open/close in milliseconds
    private final BrainApiClient client;
    private final double[] travelTable;
    private Elevator elevator;
    
    public ElevatorController(String id, int nFloors, String brainUrl) {
        this.elevator = new Elevator(id, 1);
        this.client = new BrainApiClient(brainUrl);
        TravelModel model = new TravelModel();
        this.travelTable = model.buildTravelTable(nFloors);
    }

    // The elevator's life: ask the brain where to go, go there, ask again. Forever.
    // Start it with: new Thread(new ElevatorController("A", 40, "http://localhost:8080")).start();
    @Override
    public void run() {
        while (true) {
            try {
                // ask the brain where to go next (waits up to ~25 s if there is nothing to do)
                NextDestinationResponse answer = client.getNextDest(elevator.getId());

                // TODO 1: if answer is null (HTTP 204: nothing to do yet), "continue" to ask again
                // TODO 2: otherwise go there: moveToFloor(answer.nextFloor());

            } catch (IOException e) {
                // brain unreachable: wait a bit, then try again
                System.out.println("[" + elevator.getId() + "] brain unavailable, retrying in 2 s");
                try { Thread.sleep(2000); } catch (InterruptedException stop) { return; }
            } catch (InterruptedException e) {
                return;   // someone stopped this elevator
            }
        }
    }

    public void moveToFloor(int targetFloor) throws InterruptedException {
        // TODO 3: a pickup at the floor we are already on must still open the doors.
        //         Delete this early "return": put the moving part below (MOVING ... setCurrentFloor)
        //         inside  if (targetFloor != elevator.getCurrentFloor()) { ... }  instead.
        if (targetFloor == elevator.getCurrentFloor()) {
            System.out.println("Elevator " + elevator.getId() + " is already on floor " + targetFloor);
            return;
        }
        elevator.setCurrentState(ElevatorState.MOVING);
        displayTargetFloor(targetFloor);
        
        double travelTime = travelTable[Math.abs(elevator.getCurrentFloor() - targetFloor)];
        Thread.sleep((long) (travelTime * 1000));

        elevator.setCurrentFloor(targetFloor);
        System.out.println("Elevator " + elevator.getId()+ " has arrived at floor "+ elevator.getCurrentFloor());

        // TODO 4: start every print below (and above) with "[" + elevator.getId() + "] "
        //         so several elevators in one console stay readable
        elevator.setCurrentState(ElevatorState.DOORS_OPENING);
        
        //I'll get this type of code from the rest of the code, but for now we use this
        Thread.sleep(T_DOOR_MS);// ενα μερος των 8 δευτερολεπτων 

        elevator.setCurrentState(ElevatorState.IDLE);
    }

    // Where the elevator is right now (used by the tests and, later, a GUI)
    public int getCurrentFloor() {
        return elevator.getCurrentFloor();
    }
    
    private void displayTargetFloor(int targetFloor) {
        System.out.println("----------------------------");
        System.out.println("Next floor: " + targetFloor);
        System.out.println("----------------------------");
     }
}
