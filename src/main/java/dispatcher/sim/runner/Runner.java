package dispatcher.sim.runner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import dispatcher.dispatch.brain.BrainCore;
import dispatcher.dispatch.eval.PlanEvaluation;
import dispatcher.dispatch.eval.Request;
import dispatcher.dispatch.eval.Stop;
import dispatcher.sim.traffic.Passenger;

/**
 * Fast, in-process driver of the brain: it owns the clock and the event queue,
 * plays the passengers and the cars in virtual time, and asks BrainCore for every decision.
 */
public class Runner {
    private final int nFloors;
    private final int totalCars;
    private BrainCore brain;

    // Settings, forwarded to a fresh BrainCore at the start of every run
    private Double fixedSearchSeconds = null;
    private Double budgetOverride = null;   // null = use the real budget
    private boolean reSearchEnabled = true;
    private Double reSearchBudgetOverride = null;
    private boolean useInsertionAssignment = true;

    // Ground truth, sim side only: the brain never sees these
    private double ratingKg = 630.0;          // unverified rating
    private int overweightDepartures = 0;
    private double maxLoadKg = 0.0;

    public Runner(int nFloors) {
        this(nFloors, 1);
    }

    public Runner(int nFloors, int totalCars) {
        this.nFloors = nFloors;
        this.totalCars = totalCars;
    }

    public PlanEvaluation run(List<Passenger> passengers) {
        brain = new BrainCore(nFloors, totalCars);
        brain.setFixedSearchSeconds(fixedSearchSeconds);
        brain.setBudgetOverride(budgetOverride);
        brain.setReSearchBudgetOverride(reSearchBudgetOverride);
        brain.setReSearchEnabled(reSearchEnabled);
        brain.setUseInsertionAssignment(useInsertionAssignment);

        PriorityQueue<Event> eventLine = new PriorityQueue<>();
        double clock = 0.0;

        // The sim's view of reality: which party is behind each request, and what each elevator weighs
        Map<String, Passenger> passengerById = new HashMap<>();
        Map<String, Double> loadKg = new HashMap<>();
        overweightDepartures = 0;
        maxLoadKg = 0.0;

        for (Passenger passenger : passengers) {
            eventLine.add(new Event(passenger.getTime() * 60, passenger, EventType.KEYPAD_REQUEST, null));
        }

        while (!eventLine.isEmpty()) {
            Event nextEvent = eventLine.poll();
            clock = nextEvent.getTime();

            switch (nextEvent.getType()) {
                case KEYPAD_REQUEST -> {
                    Passenger p = nextEvent.getPassenger();

                    // peek BEFORE the idle car's event is scheduled (sim-only: used for the late check)
                    Event next = eventLine.peek();
                    Double nextTime = next == null ? null : next.getTime();

                    BrainCore.Ack ack = brain.submit(p.getFromFloor(), p.getToFloor(), clock, nextTime);
                    passengerById.put(ack.requestId(), p);

                    if (ack.wakeAt() != null) {
                        eventLine.add(new Event(ack.wakeAt(), null, EventType.ELEVATOR_REQUEST, ack.elevatorId()));
                    }
                }
                case ELEVATOR_REQUEST -> {
                    String carId = nextEvent.getCarId();
                    if (brain.nextDestination(carId, clock) == null) {
                        break; // No more stops, the car is now idle
                    }

                    // What the weight sensor would read when the doors close at this stop
                    Stop s = brain.lastStop(carId);
                    double load = loadKg.getOrDefault(carId, 0.0);
                    for (Request r : s.getAlighting()) load -= passengerById.get(r.getRequestId()).getWeight();
                    for (Request r : s.getBoarding())  load += passengerById.get(r.getRequestId()).getWeight();
                    loadKg.put(carId, load);
                    maxLoadKg = Math.max(maxLoadKg, load);
                    if (load > ratingKg) overweightDepartures++;
                    eventLine.add(new Event(brain.getCar(carId).getNextDecisionTime(), null, EventType.ELEVATOR_REQUEST, carId));
                }

            }
        }

        return brain.result();
    }

    // Getters / Setters
    public int getNFloors() {
        return nFloors;
    }

    public int getOverdueSearches() {
        return brain.getOverdueSearches();
    }

    public double getTotalSearchMs() {
        return brain.getTotalSearchMs();
    }

    public double getMaxSearchMs() {
        return brain.getMaxSearchMs();
    }

    public int getMaxPending() {
        return brain.getMaxPending();
    }

    public double getTotalAssignMs() {
        return brain.getTotalAssignMs();
    }

    public double getMaxAssignMs() {
        return brain.getMaxAssignMs();
    }

    public int getOverweightDepartures() {
        return overweightDepartures;
    }

    public double getMaxLoadKg() {
        return maxLoadKg;
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

    // for tests only: forces every search to use this budget
    void setBudgetOverride(Double budget) {
        this.budgetOverride = budget;
    }
}
