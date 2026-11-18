package dispatcher.dispatch.assign;

import java.util.List;

import dispatcher.dispatch.eval.Stop;

public class Insertion {
    private final List<Stop> route;
    private final double cost;

    public Insertion(List<Stop> route, double cost) {
        this.route = route;
        this.cost = cost;
    }

    public List<Stop> getRoute() {
        return route;
    }

    public double getCost() {
        return cost;
    }

}
