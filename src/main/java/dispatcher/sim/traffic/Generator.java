package dispatcher.sim.traffic;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Generator {
    private final Random random;
    
    public Generator(int seed) {
        this.random = new Random(seed);
    }

    public Generator() {
        this(42);
    }

    public List<Passenger> simulate(Scenario scenario, int n) {
        List<Double> times = simulateTimes(scenario);
        List<Passenger> passengers = new ArrayList<>();
        for (double t : times) {
            int[] floors = scenario.getTrafficType().sampleFloors(n, random);
            passengers.add(new Passenger(samplePartySize(), floors[0], floors[1], t));
        }

        return passengers;
        
    }

    private List<Double> simulateTimes(Scenario scenario) {
        List<Double> times = new ArrayList<>();
        double s = 0;
        int i = 0;
        Segment segment = scenario.getSegments().getFirst();
        double E;
        double A;

        while (true) { 
            E = -Math.log(1- random.nextDouble());

            A = getA(segment, s);

            while (E > A) { 
                E -= A;
                s = segment.getEndMinute(); 
                i ++;
                if (i < scenario.getSegments().size()) {
                    segment = scenario.getSegments().get(i);
                    A = getA(segment, s);
                } else {
                    return times;
                }
            }

            double x = getX(E, segment, s);
            s += x;
            times.add(s);

        }
        
    }

    private double lambdaAt(Segment segment, double t) {
        return segment.getStartLambda() + segment.getSlope() * (t - segment.getStartMinute());
    }

    private double getA(Segment segment, double s) {
        return (lambdaAt(segment, s) + lambdaAt(segment, segment.getEndMinute()))*(segment.getEndMinute()-s)/2;
    }

    private double getX(double E, Segment segment, double s) {
        double x;
        if (segment.getSlope() == 0) {
            x = E/lambdaAt(segment, s);
        } else {
            double a = segment.getSlope()/2;
            double b = lambdaAt(segment, s);
            double c = -E;
            x = solveQuad(a, b, c);
        }

        return x;
    }

    private double solveQuad(double a, double b, double c) {
        double disc = b * b - 4 * a * c;
        double sqrtDisc = Math.sqrt(disc);
        double root1 = (-b + sqrtDisc) / (2 * a);
        double root2 = (-b - sqrtDisc) / (2 * a);

        // smallest strictly-positive root
        double smallestPositive = Double.POSITIVE_INFINITY;
        if (root1 > 0 && root1 < smallestPositive) {
            smallestPositive = root1;
        }
        if (root2 > 0 && root2 < smallestPositive) {
            smallestPositive = root2;
        }
            return smallestPositive;
    }

    private int samplePartySize() {
        int count = 1;
        while (count < 3 && random.nextDouble() < 0.3) {
            count++;
        }

        return count;
    }
}
