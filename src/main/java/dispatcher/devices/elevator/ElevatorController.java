package dispatcher.devices.elevator;

import java.io.IOException;

import dispatcher.api.BrainApiClient;
import dispatcher.api.dto.NextDestinationResponse;
import dispatcher.timing.Dwell;
import dispatcher.timing.RunClock;
import dispatcher.timing.TravelModel;

// One elevator. It runs on its own thread: ask the brain where to go, go there, ask again.
// Start it with: new Thread(new ElevatorController("A", 40, "http://localhost:8080")).start();
public class ElevatorController implements Runnable {
    private final BrainApiClient client;
    private final double[] travelTable;
    private final Elevator elevator;

    public ElevatorController(String id, int nFloors, String brainUrl) {
        this.elevator = new Elevator(id, 1);   // always starts at floor 1 (the brain assumes it)
        this.client = new BrainApiClient(brainUrl);
        this.travelTable = new TravelModel().buildTravelTable(nFloors);
    }

    // The elevator's life: ask the brain where to go, go there, ask again. Forever.
    @Override
    public void run() {
        log("ready at floor " + elevator.getCurrentFloor());
        while (true) {
            try {
                // ask the brain where to go next (the brain holds the call open up to ~25 s if there is nothing to do)
                NextDestinationResponse answer = client.getNextDest(elevator.getId());
                if (answer == null) {
                    continue;   // HTTP 204: nothing to do yet, ask again
                }
                moveToFloor(answer.nextFloor());

            } catch (IOException e) {
                // brain unreachable: wait a bit, then try again
                log("brain unavailable, retrying in 2 s");
                try { Thread.sleep(2000); } catch (InterruptedException stop) { return; }
            } catch (InterruptedException e) {
                return;   // someone stopped this elevator
            }
        }
    }

    // Go to a floor and serve it: travel (if needed), open the doors, wait, close them.
    public void moveToFloor(int targetFloor) throws InterruptedException {
        // A stop at the floor we are already on still opens the doors (someone may be boarding here)
        if (targetFloor != elevator.getCurrentFloor()) {
            elevator.setCurrentState(ElevatorState.MOVING);
            displayTargetFloor(targetFloor);

            double travelTime = travelTable[Math.abs(elevator.getCurrentFloor() - targetFloor)];
            Thread.sleep((long) (travelTime * 1000));

            elevator.setCurrentFloor(targetFloor);
        }
        log("at floor " + targetFloor + ", doors open");

        // Fixed dwell: the same value the brain uses for its predictions
        elevator.setCurrentState(ElevatorState.DOORS_OPEN);
        Thread.sleep((long) (Dwell.SECONDS * 1000));

        log("doors closed");
        elevator.setCurrentState(ElevatorState.IDLE);
    }

    // Where the elevator is right now (used by the tests and, later, a GUI)
    public int getCurrentFloor() {
        return elevator.getCurrentFloor();
    }

    public String getId() {
        return elevator.getId();
    }

    private void displayTargetFloor(int targetFloor) {
        log("next floor: " + targetFloor);
    }

    // Every line starts with the elevator's id, so several elevators in one console stay readable
    private void log(String message) {
        System.out.println(RunClock.stamp() + " [" + elevator.getId() + "] " + message);
    }
}
