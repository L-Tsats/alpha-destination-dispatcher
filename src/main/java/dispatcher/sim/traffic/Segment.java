package dispatcher.sim.traffic;

public class Segment {
    private final double startMinute;
    private final double startLambda;
    private final double endMinute;
    private final double endLambda;

    public Segment(Knot start, Knot end) {
        this.startMinute = start.getStartMinute();
        this.startLambda = start.getLambda();
        this.endMinute = end.getStartMinute();
        this.endLambda = end.getLambda();
    }

    public double getStartMinute() {
        return startMinute;
    }

    public double getStartLambda() {
        return startLambda;
    }

    public double getEndMinute() {
        return endMinute;
    }

    public double getEndLambda() {
        return endLambda;
    }

    public double getSlope() {
        return (endLambda - startLambda) / (endMinute - startMinute);
    }
}
