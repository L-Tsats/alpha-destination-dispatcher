package dispatcher.sim.traffic;

public class Passenger {
    private final int boardingCount;
    private final int fromFloor;
    private final int toFloor;
    private final double time;

    public Passenger(int count, int fromFloor, int toFloor, double time) {
        this.boardingCount = count;
        this.fromFloor = fromFloor;
        this.toFloor = toFloor;
        this.time = time;
    }

    public int getBoardingCount() {
        return boardingCount;
    }

    public int getToFloor() {
        return toFloor;
    }

    public int getFromFloor() {
        return fromFloor;
    }

    public double getTime() {
        return time;
    }

}
