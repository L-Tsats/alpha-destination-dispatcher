package dispatcher.dispatch.brain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dispatcher.dispatch.assign.Insertion;
import dispatcher.dispatch.assign.InsertionAssigner;
import dispatcher.dispatch.eval.PlanEvaluation;
import dispatcher.dispatch.eval.Request;
import static dispatcher.dispatch.eval.Status.BOARDED;
import static dispatcher.dispatch.eval.Status.DELIVERED;
import dispatcher.dispatch.eval.Stop;
import dispatcher.dispatch.search.DirectionalSearch;
import dispatcher.dispatch.search.Search;
import dispatcher.timing.Dwell;
import dispatcher.timing.TravelModel;

public class BrainCore {
    private final int nFloors;
    private final int nCars;
    private final LinkedHashMap<String, CarPlan> carPlans = new LinkedHashMap<>();
    private final Search search;
    private final DirectionalSearch directionalSearch;
    private final InsertionAssigner assigner;

    private int requestCounter = 0;
    private final Map<String, Double> waits = new HashMap<>();
    private final Map<String, Double> journeys = new HashMap<>();


    private int overdueSearches = 0;
    private double totalSearchMs = 0.0;
    private double maxSearchMs = 0.0;
    private int maxPending = 0;
    private double totalAssignMs = 0.0;
    private double maxAssignMs = 0.0;

    private Double fixedSearchSeconds = null;
    private Double budgetOverride = null;   // null = use the real budget
    private boolean reSearchEnabled = true;
    private Double reSearchBudgetOverride = null;
    private boolean useInsertionAssignment = true;
    private final Map<String, Stop> lastStops = new HashMap<>();
    private final Map<String, Double> lastArrivals = new HashMap<>();

    private boolean useDirectionalSearch = false;

    public record Ack(String requestId, String elevatorId, Double wakeAt) {}
    private record Assignment(CarPlan plan, Insertion interim) {}

    private final double[] travelTable;

    public BrainCore(int nFloors, int nCars) {
        this.nFloors = nFloors;
        this.nCars = nCars;
        this.search = new Search(nFloors);
        this.directionalSearch = new DirectionalSearch();
        this.assigner = new InsertionAssigner(nFloors);
        travelTable = new TravelModel().buildTravelTable(nFloors);

        for(int i = 0; i < nCars; i++) {
            Car car = new Car(String.valueOf((char)('A' + i)), 1);
            carPlans.put(car.getId(), new CarPlan(car, false));
        }
    }

    // Statistics Getters
    public int getOverdueSearches() {
        return overdueSearches;
    }

    public double getTotalSearchMs() {
        return totalSearchMs;
    }

    public double getMaxSearchMs() {
        return maxSearchMs;
    }

    public int getMaxPending() {
        return maxPending;
    }

    public double getTotalAssignMs() {
        return totalAssignMs;
    }

    public double getMaxAssignMs() {
        return maxAssignMs;
    }

    // Configuration setters
    public void setDirectionalSearch(boolean useDirectional) {
        this.useDirectionalSearch = useDirectional;
        if (useDirectional) {
            // Reset directions when switching to DirectionalSearch mode
            directionalSearch.resetAllDirections();
        }
    }
    public void setFixedSearchSeconds(Double fixedSearchSeconds) {
        this.fixedSearchSeconds = fixedSearchSeconds;
    }

    public void setBudgetOverride(Double budgetOverride) {
        this.budgetOverride = budgetOverride;
    }

    public void setReSearchBudgetOverride(Double reSearchBudgetOverride) {
        this.reSearchBudgetOverride = reSearchBudgetOverride;
    }

    public void setUseInsertionAssignment(boolean useInsertionAssignment) {
        this.useInsertionAssignment = useInsertionAssignment;
    }

    public void setReSearchEnabled(boolean reSearchEnabled) {
        this.reSearchEnabled = reSearchEnabled;
    }

    // Helpers

    public Stop nextStop(String carId) {
        CarPlan plan = carPlans.get(carId);
        Car car = plan.getCar();
        
        if (plan.getRoute().isEmpty()) {
            car.setIdle(true);
            return null;
        }
        return plan.getRoute().remove(0);
    }

    public Car getCar(String carId) {
        return carPlans.get(carId).getCar();
    }

    public PlanEvaluation result() {
        return new PlanEvaluation(waits, journeys);
    }

    public double arrived(String carId, Stop stop, double arrival) {
        CarPlan plan = carPlans.get(carId);
        Car car = plan.getCar();
        car.setDestFloor(stop.getFloor());

        for(Request r : stop.getBoarding()) {
            waits.put(r.getRequestId(), arrival - r.getArrivalTime());
            r.setStatus(BOARDED);
        }
        for(Request r : stop.getAlighting()) {
            journeys.put(r.getRequestId(), arrival - r.getArrivalTime());
            r.setStatus(DELIVERED);
        }

        car.setNextDecisionTime(arrival + Dwell.SECONDS);

        if(reSearchEnabled && plan.isSearchIncomplete()) {
            long t1 = System.nanoTime();
            double reBudget = reSearchBudgetOverride != null ? reSearchBudgetOverride : Dwell.SECONDS;

            
            List<Stop> result = computeRoute(plan.getRequests(), car.getDestFloor(), arrival, reBudget, carId);
            
            double reMs = (System.nanoTime() - t1) / 1_000_000.0;
            totalSearchMs += reMs;
            maxSearchMs = Math.max(maxSearchMs, reMs);

            if(result != null) {
                plan.setRoute(new ArrayList<>(result));
                // DirectionalSearch never aborts, only Search does
                plan.setSearchIncomplete(!useDirectionalSearch && search.wasAborted());
            }
        }

        return car.getNextDecisionTime();
    }

    public Ack submit(int fromFloor, int toFloor, double clockTime, Double nextEventTime) {

        String id = String.format("r-%05d", ++requestCounter);
        Request newRequest = new Request(id, fromFloor, toFloor, clockTime);

        long ta = System.nanoTime();
        Assignment chosen = assign(newRequest, carPlans, clockTime);
        double assignMs = (System.nanoTime() - ta) / 1_000_000.0;
        totalAssignMs += assignMs;
        maxAssignMs = Math.max(maxAssignMs, assignMs);

        CarPlan pickedCarPlan = chosen.plan();
        Car pickedCar = pickedCarPlan.getCar();
        pickedCarPlan.getRequests().add(newRequest);
        pickedCarPlan.setRoute(new ArrayList<>(chosen.interim().getRoute()));
        maxPending = Math.max(maxPending, pickedCarPlan.unfinishedCount());

        double budget = budgetOverride != null
            ? budgetOverride
            : (pickedCar.isIdle() ? 1.0 : pickedCar.getNextDecisionTime() - clockTime);

        long t0 = System.nanoTime();
        List<Stop> result = computeRoute(pickedCarPlan.getRequests(), pickedCar.getDestFloor(), clockTime, budget, pickedCar.getId());
        double elapsedMs = (System.nanoTime() - t0) / 1_000_000.0;

        totalSearchMs += elapsedMs;
        maxSearchMs = Math.max(maxSearchMs, elapsedMs);

        double d = fixedSearchSeconds != null ? fixedSearchSeconds : elapsedMs / 1000.0;

        boolean late = nextEventTime != null && clockTime + d > nextEventTime;

        if (late || (result == null && !useDirectionalSearch)) {
            overdueSearches++;
        }

        if (late || result == null) {
            // plan not ready in time or no plan found: keep the interim insertion route
            pickedCarPlan.setSearchIncomplete(true);
        } else {
            pickedCarPlan.setRoute(new ArrayList<>(result));
            // DirectionalSearch never aborts, only Search does
            pickedCarPlan.setSearchIncomplete(!useDirectionalSearch && search.wasAborted());
        }

        Double wakeAt = null;
        if (pickedCar.isIdle()) {
            pickedCar.setIdle(false);
            pickedCar.setNextDecisionTime(clockTime + d);
            wakeAt = clockTime + d;
        }

        return new Ack(id, pickedCar.getId(), wakeAt);
    }

    /**
     * Common route computation method that selects between Search and DirectionalSearch.
     */
    private List<Stop> computeRoute(List<Request> requests, int startFloor, double currentTime, double budget, String carId) {
        if (useDirectionalSearch) {
            return directionalSearch.getSuboptimalStops(requests, startFloor, carId);
        } else {
            return search.searchBestroute(requests, startFloor, currentTime, budget);
        }
    }

    private CarPlan pickCar(Map<String, CarPlan> carPlans) {
        int unfinished = Integer.MAX_VALUE;
        CarPlan best = null;
        for(CarPlan plan : carPlans.values()) {
            if(plan.unfinishedCount() < unfinished) {
                unfinished = plan.unfinishedCount();
                best = plan;
            }
        }
        return best;
    }

    private Assignment assign(Request req, Map<String, CarPlan> carPlans, double clock) {
        if (!useInsertionAssignment) {
            CarPlan p = pickCar(carPlans);
            return new Assignment(p, insertInto(p, req, clock));
        }
        Assignment best = null;
        for (CarPlan p : carPlans.values()) {
            Insertion ins = insertInto(p, req, clock);
            if (best == null || ins.getCost() < best.interim().getCost()) {
                best = new Assignment(p, ins);
            }
        }
        return best;
    }

    private Insertion insertInto(CarPlan p, Request req, double clock) {
        Car c = p.getCar();
        return assigner.best(p.getRoute(), req, c.getDestFloor(), Math.max(clock, c.getNextDecisionTime()));
    }

    public Stop lastStop(String carId){
        return lastStops.get(carId);
    }

    // When the elevator reaches the stop it was last given (the brain's predicted arrival)
    public double lastArrival(String carId) {
        return lastArrivals.get(carId);
    }

    public Integer nextDestination(String carId, double now) {
        Stop nextStop = nextStop(carId);
        if (nextStop == null) {
            return null;
        }

        double arrival = now + travelTable[Math.abs(nextStop.getFloor() - getCar(carId).getDestFloor())];
        arrived(carId, nextStop, arrival);
        lastStops.put(carId, nextStop);
        lastArrivals.put(carId, arrival);
        return nextStop.getFloor();
    }

    public boolean hasElevator(String carId) {
        return carPlans.containsKey(carId);
    }

}
