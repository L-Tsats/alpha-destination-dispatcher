package dispatcher.sim.traffic;
import java.util.ArrayList;
import java.util.List;

public class Scenario {
    private final int lengthMinutes;
    private final List<Knot> knots;
    private final TrafficType trafficType;
    private final List<Segment> segments = new ArrayList<>();

    private Scenario(int lengthMinutes, List<Knot> knots, TrafficType trafficType) {
        this.trafficType = trafficType;
        this.lengthMinutes = lengthMinutes;
        this.knots = List.copyOf(knots);
        for (int i = 1; i < knots.size(); i++) {
            this.segments.add(new Segment(knots.get(i - 1), knots.get(i)));
        }
        Knot lastKnot = knots.get(knots.size() - 1);
        Knot tailEnd = new Knot(lengthMinutes, lastKnot.getLambda());
        this.segments.add(new Segment(lastKnot, tailEnd));
    }


    public static Scenario of(int lengthMinutes, List<Knot> knots, TrafficType trafficType) {
        // validation here if you want (length > 0, knots sorted, etc.)
        if (knots.isEmpty()) {
            throw new IllegalArgumentException("knots must not be empty");
        }
        if (knots.get(0).getStartMinute() != 0) {
            throw new IllegalArgumentException("initial knot must be 0");
        }
        if (knots.get(knots.size() - 1).getStartMinute() >= lengthMinutes) {
            throw new IllegalArgumentException("last knot must start before scenario's endTime");
        }

        for (int i = 0; i < knots.size(); i++) {
            Knot knot = knots.get(i);
            if (knot.getLambda() <= 0) {
                throw new IllegalArgumentException("All lambdas must be strict-postitive");
            }
            if (i > 0 && knots.get(i - 1).getStartMinute() >= knot.getStartMinute()) {
                throw new IllegalArgumentException("Knot's starting points must be in strict ascending order");
            }
        }

        return new Scenario(lengthMinutes, knots, trafficType);
    }

    public int getLengthMinutes() {
        return lengthMinutes;
    }

    public List<Knot> getKnots() {
        return knots;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public TrafficType getTrafficType() {
        return trafficType;
    }

}
