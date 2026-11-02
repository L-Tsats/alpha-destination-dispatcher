package dispatcher.sim.http;

public class PassengerRequest {

    private int fromFloor, toFloor;
    
    public PassengerRequest(int fromFloor, int toFloor){
        this.fromFloor = fromFloor;
        this.toFloor = toFloor;
    }
    
    public int getFromFloor() {
        return fromFloor;
    }
    
    public int getToFloor() {
        return toFloor;
    }
}