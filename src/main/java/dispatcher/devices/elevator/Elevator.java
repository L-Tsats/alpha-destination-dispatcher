
package dispatcher.devices.elevator;

import java.io.IOException;

import dispatcher.api.BrainApiClient;


public class Elevator {

    private int currentFloor;
    private String id;
    private ElevatorState currentState;
    private int targetFloor;
    
    public Elevator(String id, int startingFloor) {
        this.id = id;
        this.currentFloor = startingFloor;
        this.currentState = ElevatorState.IDLE;
        this.targetFloor = startingFloor;
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

    public int getTargetFloor() {
    return targetFloor;
    }

    public void setTargetFloor(int floor) {
    this.targetFloor = floor;
   }

    public void setCurrentFloor(int floor) {
        this.currentFloor = floor;
    }

    
    // Χρειάζονται για να ενημερώνει η ElevatorRun την κατάσταση του ανελκυστήρα.
      public void setCurrentState(ElevatorState state) {
        this.currentState = state;
    }

    
    @Override 
    public String toString() {
        return "Elevator " + id + " is currently on floor " + currentFloor + " and is " + currentState;
    }

}
