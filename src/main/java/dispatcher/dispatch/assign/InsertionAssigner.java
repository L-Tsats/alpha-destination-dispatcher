package dispatcher.dispatch.assign;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dispatcher.dispatch.eval.Evaluator;
import dispatcher.dispatch.eval.PlanScorer;
import dispatcher.dispatch.eval.Request;
import dispatcher.dispatch.eval.Stop;

public class InsertionAssigner {
    private final Evaluator evaluator; // nFloors will be set in evaluateRoute

    public InsertionAssigner(int nFloors) {
        this.evaluator = new Evaluator(nFloors);
    }

    public Insertion best(List<Stop> route, Request req, int startFloor, double startTime) {
        int m = route.size();
        int startLoad = onboardLoad(route);
        double oldTotal = PlanScorer.deliveryTotal(evaluator.evaluate(route, startFloor, startTime));

        Stop pickup = new Stop(req.getFromFloor(), new ArrayList<>(List.of(req)), new ArrayList<>());
        Stop dropoff = new Stop(req.getToFloor(), new ArrayList<>(), new ArrayList<>(List.of(req)));

        List<Stop> bestRoute = null;
        double bestCost = Double.POSITIVE_INFINITY;

        for (int i = 0; i <= m; i++) {          // pickup goes before original stop i (m = at the end)
            for (int j = i; j <= m; j++) {      // dropoff goes before original stop j, after the pickup
                List<Stop> candidate = new ArrayList<>();
                for (int k = 0; k <= m; k++) {
                    if (k == i) candidate.add(pickup);
                    if (k == j) candidate.add(dropoff);
                    if (k < m) candidate.add(route.get(k));
                }
                if (!fits(candidate, startLoad)) continue;

                double cost = PlanScorer.deliveryTotal(evaluator.evaluate(candidate, startFloor, startTime)) - oldTotal;
                if (cost < bestCost) {          // strict <, so ties keep the earliest position
                    bestCost = cost;
                    bestRoute = candidate;
                }
            }
        }
        return new Insertion(bestRoute, bestCost);
    }

    // requests that are dropped off in the route but never picked up in it are already in the car
    private int onboardLoad(List<Stop> route) {
        Set<String> boarding = new HashSet<>();
        Set<String> alighting = new HashSet<>();
        for (Stop s : route) {
            for (Request r : s.getBoarding()) boarding.add(r.getRequestId());
            for (Request r : s.getAlighting()) alighting.add(r.getRequestId());
        }
        alighting.removeAll(boarding);
        return alighting.size();
    }

    // capacity never exceeded anywhere along the candidate
    private boolean fits(List<Stop> stops, int startLoad) {
        int load = startLoad;
        for (Stop s : stops) {
            load -= s.getAlighting().size();
            load += s.getBoarding().size();
            if (load > 3) return false;
        }
        return true;
    }

}
