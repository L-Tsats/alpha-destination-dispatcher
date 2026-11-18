package dispatcher.dispatch.brain;

public class Car {
    private final String id;
    private int destFloor;
    private boolean idle = true;
    private double nextDecisionTime = 0.0;

    public Car(String id, int startFloor) {
        this.id = id;
        destFloor = startFloor;
    }

    public String getId() {
        return id;
    }

    public int getDestFloor() {
        return destFloor;
    }

    public void setDestFloor(int destFloor) {
        this.destFloor = destFloor;
    }

    public void setIdle(boolean idle) {
        this.idle = idle;
    }

    public boolean isIdle() {
        return idle;
    }

    public double getNextDecisionTime() {
        return nextDecisionTime;
    }

    public void setNextDecisionTime(double destinationArrivalTime) {
        this.nextDecisionTime = destinationArrivalTime;
    }

}
