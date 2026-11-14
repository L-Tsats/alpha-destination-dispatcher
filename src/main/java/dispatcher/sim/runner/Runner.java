package dispatcher.sim.runner;

import java.util.List;
import java.util.PriorityQueue;

import dispatcher.dispatch.brain.BrainCore;
import dispatcher.dispatch.eval.PlanEvaluation;
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

                    if (ack.wakeAt() != null) {
                        eventLine.add(new Event(ack.wakeAt(), null, EventType.ELEVATOR_REQUEST, ack.elevatorId()));
                    }
                }
                case ELEVATOR_REQUEST -> {
                    String carId = nextEvent.getCarId();
                    if (brain.nextDestination(carId, clock) == null) {
                        break; // No more stops, the car is now idle
                    }
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
