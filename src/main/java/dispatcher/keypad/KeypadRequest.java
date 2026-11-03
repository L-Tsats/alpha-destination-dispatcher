package dispatcher.keypad;

public class KeypadRequest {
    private final int currentFloor;
    private final int destinationFloor;

    public KeypadRequest(int currentFloor, int destinationFloor) {
        this.currentFloor = currentFloor;
        this.destinationFloor = destinationFloor;

    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public int getDestinationFloor() {
        return destinationFloor;
    }

}