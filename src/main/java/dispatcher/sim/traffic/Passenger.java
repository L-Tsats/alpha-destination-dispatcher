package dispatcher.sim.traffic;

import org.apache.commons.math3.distribution.NormalDistribution;


public class Passenger {
    private final int boardingCount;
    private final int fromFloor;
    private final int toFloor;
    private final double time;
    private final double weight;

    public Passenger(int count, int fromFloor, int toFloor, double time, double weight) {
        this.boardingCount = count;
        this.fromFloor = fromFloor;
        this.toFloor = toFloor;
        this.time = time;
        this.weight = weight;
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

    public double getWeight() {
        return weight;
    }

}
