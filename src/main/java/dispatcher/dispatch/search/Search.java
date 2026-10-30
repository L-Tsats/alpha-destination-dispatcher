package dispatcher.dispatch.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dispatcher.dispatch.eval.Evaluator;
import dispatcher.dispatch.eval.PlanEvaluation;
import dispatcher.dispatch.eval.Request;
import dispatcher.dispatch.eval.Status;
import dispatcher.dispatch.eval.Stop;

public class Search {
    private final Evaluator evaluator;
    private List<Stop> bestRoute; // Used to save the best and recursively check it
    private double bestScore = Double.POSITIVE_INFINITY;
    private PlanEvaluation bestplPlanEvaluation;

    public Search(int nFloors) {
        evaluator  = new Evaluator(nFloors);
    }

    public List<Stop> run(List<Request> requests) {
        bestScore = Double.POSITIVE_INFINITY;
        bestRoute = null;

        List<Request> toPickup = new ArrayList<>();
        List<Request> onboard = new ArrayList<>();
        for (Request r : requests) {
            if (r.getStatus() == Status.WAITING) {
                toPickup.add(r);
            } else if (r.getStatus() == Status.BOARDED) {
                onboard.add(r);
            }
        }

        search(new ArrayList<>(), toPickup, onboard);
        return bestRoute;
    }


    public List<Stop> getBestRoute() {
        return bestRoute;
    }

    public double getBestScore() {
        return bestScore;
    }

    public PlanEvaluation getBestPlanEvaluation() {
        return bestplPlanEvaluation;
    }


    private void search(List<Stop> stopsSoFar, List<Request> toPickup, List<Request> onboard) {
        if(toPickup.isEmpty() && onboard.isEmpty()) {
            PlanEvaluation planEval = evaluator.evaluate(stopsSoFar, 1, 0.0);
            double score = score(planEval);
            if (score < bestScore) {
                bestScore = score;
                bestRoute = List.copyOf(stopsSoFar);
                bestplPlanEvaluation = planEval;
            }

            return;
        }
        
        for(Request req : toPickup) {
            List<Stop> nextStops = new ArrayList<>(stopsSoFar);
            List<Request> boarding = new ArrayList<>();
            boarding.add(req);
            nextStops.add(new Stop(req.getFromFloor(), boarding, new ArrayList<>()));

            List<Request> nextToPickup = new ArrayList<>(toPickup);
            nextToPickup.remove(req);  
            List<Request> nextOnboard = new ArrayList<>(onboard);
            nextOnboard.add(req);

            search(nextStops, nextToPickup, nextOnboard);
        }

        for (Request req : onboard) {
            List<Stop> nextStops = new ArrayList<>(stopsSoFar);
            nextStops.add(new Stop(req.getToFloor(), new ArrayList<>(), List.of(req)));

            List<Request> nextOnboard = new ArrayList<>(onboard);
            nextOnboard.remove(req);

            search(nextStops, new ArrayList<>(toPickup), nextOnboard);
        }
    }

    private double score(PlanEvaluation plan) {
        double sum = 0;
        int count = 0;
        for (Map.Entry<String, Double> wait : plan.getPickupWaits().entrySet()) {
            sum += wait.getValue();
            count ++;
        }
        
        return sum / count;
    }

}
