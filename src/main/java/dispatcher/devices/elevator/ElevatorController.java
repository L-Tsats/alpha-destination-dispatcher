package dispatcher.devices.elevator;
import dispatcher.api.BrainApiClient;
import dispatcher.timing.TravelModel;

import java.io.IOException;

public class ElevatorController {
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
    

    public void movetoFloor(int targetFloor) throws InterruptedException {
        if (targetFloor == elevator.getCurrentFloor()) {
            System.out.println("Elevator " + elevator.getId() + " is already on floor " + targetFloor);
            return;
        }
        elevator.setTargetFloor(targetFloor);
        elevator.setCurrentState(ElevatorState.MOVING);
        displayTargetFloor();
        
        double travelTime = travelTable[Math.abs(elevator.getCurrentFloor() - targetFloor)];
        Thread.sleep((long) (travelTime * 1000));
        
        System.out.println("Elevator " + elevator.getId()+ " has arrived at floor "+ elevator.getCurrentFloor());

        elevator.setCurrentState(ElevatorState.DOORS_OPENING);
        System.out.println("Doors are opening now");
        //i ll get this type of code from the rest of the code, but for now we use this
        Thread.sleep(T_DOOR_MS / 3);// ενα μερος των 8 δευτερολεπτων 
        
        elevator.setCurrentState(ElevatorState.DOORS_OPEN);
        System.out.println("Doors are open now/passengers please board or exit");
        Thread.sleep(T_DOOR_MS / 3);
        
        elevator.setCurrentState(ElevatorState.DOORS_CLOSING);
        System.out.println("Doors are closing now");
        Thread.sleep(T_DOOR_MS / 3);

        //elevator.setCurrentState(ElevatorState.IDLE);
        //System.out.println("Elevator " + elevator.getId() + " is now idle");
        
    }


    
    private void displayTargetFloor() {
        System.out.println("----------------------------");
        System.out.println("Next floor: " + elevator.getTargetFloor());
        System.out.println("----------------------------");
     }
}
