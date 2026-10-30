package dispatcher.dispatch.eval;

import java.util.List;

public class Stop {
    private final int floor;
    private final List<Request> boarding;
    private final List<Request> alighting;

    public Stop(int floor, List<Request> boarding, List<Request> alighting) {
        this.floor = floor;
        this.boarding = List.copyOf(boarding);
        this.alighting = List.copyOf(alighting);
    }

    public int getFloor() {
        return floor;
    }

    public List<Request> getAlighting() {
        return alighting;
    }

    public List<Request> getBoarding() {
        return boarding;
    }
    
}
