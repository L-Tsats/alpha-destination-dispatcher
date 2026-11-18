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
    private static final double OVER_CAPACITY_PENALTY = 1_000_000.0; // Large enough that any route that fits always wins over one that doesn't


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
                candidate = mustBoard(candidate);

                double cost = PlanScorer.deliveryTotal(evaluator.evaluate(candidate, startFloor, startTime)) - oldTotal;
                if (!fits(candidate, startLoad)) cost += OVER_CAPACITY_PENALTY;

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

    // Rebuilds a route the way it really plays out: at each stop's floor, everyone waiting there
    // boards and everyone riding there gets off. Stops left with nobody are dropped.
    private List<Stop> mustBoard(List<Stop> stops) {
        List<Request> waiting = new ArrayList<>();   // will board somewhere in this route
        List<Request> onboard = new ArrayList<>();   // already in the car before the route starts
        for (Stop s : stops) waiting.addAll(s.getBoarding());
        for (Stop s : stops) {
            for (Request r : s.getAlighting()) {
                if (!waiting.contains(r) && !onboard.contains(r)) onboard.add(r);
            }
        }

        List<Stop> result = new ArrayList<>();
        for (Stop s : stops) {
            int f = s.getFloor();
            List<Request> boarding = new ArrayList<>();
            List<Request> alighting = new ArrayList<>();

            for (Request r : onboard) {
                if (r.getToFloor() == f) alighting.add(r);
            }
            for (Request r : waiting) {
                if (r.getFromFloor() == f) boarding.add(r);
            }

            if (boarding.isEmpty() && alighting.isEmpty()) continue;   // nothing happens here any more

            onboard.removeAll(alighting);
            waiting.removeAll(boarding);
            onboard.addAll(boarding);
            result.add(new Stop(f, boarding, alighting));
        }
        return result;
    }


}
