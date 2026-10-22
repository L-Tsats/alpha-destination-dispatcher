package dispatcher.sim.traffic;

public class Knot {
    private final double startMinute;
    private final double lambda;

    public Knot(double startMinute, double lambda) {
        this.startMinute = startMinute;
        this.lambda = lambda;
    }

    public double getLambda() {
        return lambda;
    }

    public double getStartMinute() {
        return startMinute;
    }

}
