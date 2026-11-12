package dispatcher.sim.runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import dispatcher.dispatch.assign.Insertion;
import dispatcher.dispatch.assign.InsertionAssigner;
import dispatcher.dispatch.eval.PlanEvaluation;
import dispatcher.dispatch.eval.Request;
import static dispatcher.dispatch.eval.Status.BOARDED;
import static dispatcher.dispatch.eval.Status.DELIVERED;
import dispatcher.dispatch.eval.Stop;
import dispatcher.dispatch.search.Search;
import dispatcher.sim.traffic.Passenger;
import dispatcher.timing.TravelModel;

public class Runner {
    private final int nFloors;
    private final int totalCars;
    private final InsertionAssigner assigner;

    private int overdueSearches = 0;
    private double totalSearchMs = 0.0;
    private double maxSearchMs = 0.0;
    private int maxPending = 0;
    private Double fixedSearchSeconds = null;

    private Double budgetOverride = null;   // null = use the real budget
    private boolean reSearchEnabled = true;
    private Double reSearchBudgetOverride = null;
    private boolean useInsertionAssignment = true;
    private double totalAssignMs = 0.0;
    private double maxAssignMs = 0.0;


    public Runner(int nFloors) {
        this(nFloors, 1);
    }

    public Runner(int nFloors, int totalCars) {
        this.nFloors = nFloors;
        this.totalCars = totalCars;
        this.assigner = new InsertionAssigner(nFloors);
    }
    
    public PlanEvaluation run(List<Passenger> passengers) {
        overdueSearches = 0;
        totalSearchMs = 0.0;
        maxSearchMs = 0.0;
        maxPending = 0;
        totalAssignMs = 0.0;
        maxAssignMs = 0.0;


        PriorityQueue<Event> eventLine = new PriorityQueue<>();
        double clock = 0.0;
        int requestCounter = 0;
        double[] travelTable = new TravelModel().buildTravelTable(nFloors);

        for(Passenger passenger : passengers) {
            eventLine.add(new Event(passenger.getTime() * 60, passenger, EventType.KEYPAD_REQUEST, null));
        }

        Map<String, Passenger> passengerById = new HashMap<>();
        Search search = new Search(nFloors);

        LinkedHashMap<String, CarPlan> carPlans = new LinkedHashMap<>();

        for(int i = 0; i < totalCars; i++) {
            Car car = new Car(String.valueOf((char)('A' + i)), 1);
            carPlans.put(car.getId(), new CarPlan(car, false));
        }

        Map<String, Double> waits = new HashMap<>();
        Map<String, Double> journeys = new HashMap<>();

        while (!eventLine.isEmpty()) {
            Event nextEvent = eventLine.poll();
            clock = nextEvent.getTime();

            switch (nextEvent.getType()) {
                case KEYPAD_REQUEST -> {
                    List<Stop> result = null;

                    String id = String.format("r-%05d", ++requestCounter);
                    Request newRequest = toRequest(nextEvent.getPassenger(), id, nextEvent.getTime());

                    long ta = System.nanoTime();
                    Assignment chosen = assign(newRequest, carPlans, clock);
                    double assignMs = (System.nanoTime() - ta) / 1_000_000.0;
                    totalAssignMs += assignMs;
                    maxAssignMs = Math.max(maxAssignMs, assignMs);

                    CarPlan pickedCarPlan = chosen.plan();
                    Car pickedCar = pickedCarPlan.getCar();
                    pickedCarPlan.getRequests().add(newRequest);
                    pickedCarPlan.setRoute(new ArrayList<>(chosen.interim().getRoute()));
                    maxPending = Math.max(maxPending, pickedCarPlan.unfinishedCount());
                    passengerById.put(id, nextEvent.getPassenger());

                    double budget = budgetOverride != null
                        ? budgetOverride
                        : (pickedCar.isIdle() ? 1.0 : pickedCar.getNextDecisionTime() - clock);

                    long t0 = System.nanoTime();
                    result = search.searchBestroute(pickedCarPlan.getRequests(), pickedCar.getDestFloor(), clock, budget);
                    double elapsedMs = (System.nanoTime() - t0) / 1_000_000.0;

                    totalSearchMs += elapsedMs;
                    maxSearchMs = Math.max(maxSearchMs, elapsedMs);

                    double d = fixedSearchSeconds != null ? fixedSearchSeconds : elapsedMs / 1000.0;

                    Event next = eventLine.peek();   // peek BEFORE scheduling the idle car's event
                    boolean late = next != null && clock + d > next.getTime();

                    if (late || search.wasAborted()) {
                        overdueSearches++;
                    }

                    if (late || result == null) {
                        // plan not ready in time or no plan found: keep the interim insertion route
                        pickedCarPlan.setSearchIncomplete(true);
                    } else {
                        pickedCarPlan.setRoute(new ArrayList<>(result));
                        pickedCarPlan.setSearchIncomplete(search.wasAborted());
                    }

                    if (pickedCar.isIdle()) {
                        pickedCar.setIdle(false);
                        pickedCar.setNextDecisionTime(clock + d);
                        eventLine.add(new Event(clock + d, null, EventType.ELEVATOR_REQUEST, pickedCar.getId()));
                    }
                }

                case ELEVATOR_REQUEST -> {
                    List<Stop> result = null;

                    CarPlan plan = carPlans.get(nextEvent.getCarId());
                    Car car = plan.getCar();


                    if(plan.getRoute().isEmpty()) {
                        car.setIdle(true);
                        break; // No more stops to make, car is idle
                    }
                    Stop stop = plan.getRoute().remove(0);
                    double arrival = clock + travelTable[Math.abs(stop.getFloor() - car.getDestFloor())];

                    car.setDestFloor(stop.getFloor());
                    for(Request r : stop.getBoarding()) {
                        waits.put(r.getRequestId(), arrival - r.getArrivalTime());
                        r.setStatus(BOARDED);
                    }
                    for(Request r : stop.getAlighting()) {
                        journeys.put(r.getRequestId(), arrival - r.getArrivalTime());
                        r.setStatus(DELIVERED);
                    }

                    car.setNextDecisionTime(arrival + dwell(stop));
                    eventLine.add(new Event(car.getNextDecisionTime(), null, EventType.ELEVATOR_REQUEST, car.getId()));

                    if(reSearchEnabled && plan.isSearchIncomplete()) {
                        long t1 = System.nanoTime();
                        double reBudget = reSearchBudgetOverride != null ? reSearchBudgetOverride : dwell(stop);
                        result = search.searchBestroute(plan.getRequests(), car.getDestFloor(), arrival, reBudget);

                        double reMs = (System.nanoTime() - t1) / 1_000_000.0;
                        totalSearchMs += reMs;
                        maxSearchMs = Math.max(maxSearchMs, reMs);

                        if(result != null) {
                            plan.setRoute(new ArrayList<>(result));
                            plan.setSearchIncomplete(search.wasAborted());
                        }
                    }
                }
            }

        }

        return new PlanEvaluation(waits, journeys);
    }

    // Getters / Setters
    public int getNFloors(){
        return nFloors;
    }

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

    public void setFixedSearchSeconds(Double fixedSearchSeconds) {
        this.fixedSearchSeconds = fixedSearchSeconds;
    }

    void setReSearchBudgetOverride(Double budget) {
        this.reSearchBudgetOverride = budget;
    }

    void setReSearchEnabled(boolean enabled) {
        this.reSearchEnabled = enabled;
    }

    void setInsertionAssignment(boolean on) {
        this.useInsertionAssignment = on;
    }

    public double getTotalAssignMs() {
        return totalAssignMs;
    }

    public double getMaxAssignMs() {
        return maxAssignMs;
    }

    // for tests only: forces every search to use this budget
    void setBudgetOverride(Double budget) {
        this.budgetOverride = budget;
    }

    // Helpers
    private Request toRequest(Passenger passenger, String id, double time) {
        return new Request(id, passenger.getFromFloor(), passenger.getToFloor(), time);
    }
    
    private double dwell(Stop s) {
        return 4.0 + 1.1 * Math.max(s.getBoarding().size(), s.getAlighting().size());
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

    private record Assignment(CarPlan plan, Insertion interim) {}

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

}
