package dispatcher.sim.runner;

public class Car {
    private final String id;
    private int destFloor;
    private boolean idle = true;

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
}
