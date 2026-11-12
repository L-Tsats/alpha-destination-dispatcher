package dispatcher.dispatch.search;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

import dispatcher.dispatch.eval.Evaluator; // Used for running it's code to supply values while the search is running!
import dispatcher.dispatch.eval.PlanEvaluation;
import dispatcher.dispatch.eval.PlanScorer;
import dispatcher.dispatch.eval.Request;
import dispatcher.dispatch.eval.Status;
import dispatcher.dispatch.eval.Stop;

public class Search {
    private final Evaluator evaluator;
    private List<Stop> bestRoute;
    private double bestScore = Double.POSITIVE_INFINITY;
    private PlanEvaluation bestplPlanEvaluation;

    private long deadlineNanos = Long.MAX_VALUE;  // "no limit" by default
    private boolean aborted;

    private BooleanSupplier cancelCheck = () -> false;   // default: never cancel

    private int startFloor;
    private double currentTime;

    private final int K = 3; // To-be-replaced with actual car-weight checking

    public Search(int nFloors) {
        evaluator  = new Evaluator(nFloors);
    }

    public List<Stop> searchBestroute(List<Request> requests, int startFloor, double currentTime, double budgetSeconds) {
        bestplPlanEvaluation = null;
        bestScore = Double.POSITIVE_INFINITY;
        bestRoute = null;
        this.startFloor = startFloor;
        this.currentTime = currentTime;
        this.aborted = false;
        this.deadlineNanos = budgetSeconds > 1e6
                ? Long.MAX_VALUE
                : System.nanoTime() + (long) (budgetSeconds * 1e9);


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

    public List<Stop> searchBestroute(List<Request> requests, int startFloor, double currentTime) {
        return searchBestroute(requests, startFloor, currentTime, Double.MAX_VALUE);
    }

    private void search(List<Stop> stopsSoFar, List<Request> toPickup, List<Request> onboard) {
        if (timeIsUp()) return;

        if(toPickup.isEmpty() && onboard.isEmpty()) {
            PlanEvaluation planEval = evaluator.evaluate(stopsSoFar, startFloor, currentTime);
            double score = PlanScorer.deliveryMean(planEval);
            if (score < bestScore) {
                bestScore = score;
                bestRoute = List.copyOf(stopsSoFar);
                bestplPlanEvaluation = planEval;
            }

            return;
        }  
        recurseValidCases(stopsSoFar, toPickup, onboard);

    }

    private void recurseValidCases (List<Stop> stopsSoFar, List<Request> toPickup, List<Request> onboard) {

        if (onboard.size() < K) {
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
        }

        for (Request req : onboard) {
            List<Stop> nextStops = new ArrayList<>(stopsSoFar);
            nextStops.add(new Stop(req.getToFloor(), new ArrayList<>(), List.of(req)));

            List<Request> nextOnboard = new ArrayList<>(onboard);
            nextOnboard.remove(req);

            search(nextStops, new ArrayList<>(toPickup), nextOnboard);
        }
        
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

    public void setCancelCheck(BooleanSupplier cancelCheck) {
        this.cancelCheck = cancelCheck;
    }

    private boolean timeIsUp() {
        if (!aborted && System.nanoTime() > deadlineNanos || cancelCheck.getAsBoolean()) {
            aborted = true;
        }
        return aborted;
    }

    public boolean wasAborted() { return aborted; }
    
}
