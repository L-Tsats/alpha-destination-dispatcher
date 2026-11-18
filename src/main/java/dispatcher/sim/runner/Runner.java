package dispatcher.sim.runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import dispatcher.dispatch.brain.BrainCore;
import dispatcher.dispatch.eval.PlanEvaluation;
import dispatcher.dispatch.eval.Request;
import dispatcher.dispatch.eval.Stop;
import dispatcher.sim.traffic.Passenger;

/**
 * Fast, in-process driver of the brain: it owns the clock and the event queue,
 * plays the passengers and the elevators in virtual time, and asks BrainCore for every decision.
 *
 * It also plays REALITY: true weights, who is really inside each elevator, parties stepping out
 * when the elevator is overweight. run() returns the TRUE waits and journeys (from each party's
 * first keypad press); the brain's own view is available from getBrainResult().
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

    // Reality settings (placeholders)
    private double ratingKg = 630.0;           // unverified rating
    private double recheckSeconds = 3.0;       // extra door time per party stepping out
    private double reRequestDelaySeconds = 10.0; // a party that stepped out presses the keypad again after this
    private boolean randomStepOut = true;      // true: a random newcomer steps out; false: the last one in the list
    private long stepOutSeed = 1;              // same seed, same run
    private Random stepOutRandom;

    // Ground-truth stats, sim side only: the brain never sees these
    private int overweightDepartures = 0;      // departures still over the rating (only a single heavy party can cause this)
    private int overweightChecks = 0;          // stops where the load was over the rating before anyone stepped out
    private int stepOuts = 0;                  // parties that stepped out
    private double maxLoadKg = 0.0;            // highest load at departure

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

        // The sim's view of reality
        Map<String, Passenger> passengerById = new HashMap<>();   // request id -> the party behind it
        Map<Passenger, String> originalId = new HashMap<>();      // party -> id of its FIRST request
        Map<String, Set<String>> inside = new HashMap<>();        // elevator -> request ids truly inside
        Map<String, Double> loadKg = new HashMap<>();             // elevator -> true load
        Map<String, Double> trueWaits = new HashMap<>();          // keyed by the party's first request id
        Map<String, Double> trueJourneys = new HashMap<>();
        overweightDepartures = 0;
        overweightChecks = 0;
        stepOuts = 0;
        maxLoadKg = 0.0;
        stepOutRandom = new Random(stepOutSeed);

        for (Passenger passenger : passengers) {
            eventLine.add(new Event(passenger.getTime() * 60, passenger, EventType.KEYPAD_REQUEST, null));
        }

        while (!eventLine.isEmpty()) {
            Event nextEvent = eventLine.poll();
            clock = nextEvent.getTime();

            switch (nextEvent.getType()) {
                case KEYPAD_REQUEST -> {
                    Passenger p = nextEvent.getPassenger();

                    // peek BEFORE the idle elevator's event is scheduled (sim-only: used for the late check)
                    Event next = eventLine.peek();
                    Double nextTime = next == null ? null : next.getTime();

                    BrainCore.Ack ack = brain.submit(p.getFromFloor(), p.getToFloor(), clock, nextTime);
                    passengerById.put(ack.requestId(), p);
                    originalId.putIfAbsent(p, ack.requestId());   // a re-request keeps the first id

                    if (ack.wakeAt() != null) {
                        eventLine.add(new Event(ack.wakeAt(), null, EventType.ELEVATOR_REQUEST, ack.elevatorId()));
                    }
                }
                case ELEVATOR_REQUEST -> {
                    String carId = nextEvent.getCarId();
                    if (brain.nextDestination(carId, clock) == null) {
                        break; // No more stops, the elevator is now idle
                    }

                    // --- what really happens at this stop ---
                    Stop s = brain.lastStop(carId);
                    double arrival = brain.lastArrival(carId);
                    Set<String> in = inside.computeIfAbsent(carId, k -> new LinkedHashSet<>());
                    double load = loadKg.getOrDefault(carId, 0.0);

                    // 1. People get off. A stale request (its party stepped out earlier) is not inside: skip it.
                    for (Request r : s.getAlighting()) {
                        if (in.remove(r.getRequestId())) {
                            Passenger p = passengerById.get(r.getRequestId());
                            load -= p.getWeight();
                            trueJourneys.put(originalId.get(p), arrival - p.getTime() * 60);
                        }
                    }

                    // 2. Everyone waiting here boards
                    List<Request> boarded = new ArrayList<>(s.getBoarding());
                    for (Request r : boarded) {
                        in.add(r.getRequestId());
                        load += passengerById.get(r.getRequestId()).getWeight();
                    }

                    // 3. Overweight: one weight check per pass. If over, ONE newcomer from this floor steps out
                    //    (parties already inside were accepted earlier), wait recheckSeconds, check again.
                    //    Never empty the elevator, or a single heavy party could never ride.
                    if (load > ratingKg) overweightChecks++;
                    int outHere = 0;
                    while (load > ratingKg && !boarded.isEmpty() && in.size() > 1) {
                        int pick = randomStepOut ? stepOutRandom.nextInt(boarded.size()) : boarded.size() - 1;
                        Request out = boarded.remove(pick);
                        in.remove(out.getRequestId());
                        Passenger p = passengerById.get(out.getRequestId());
                        load -= p.getWeight();
                        outHere++;
                    }
                    double departure = brain.getCar(carId).getNextDecisionTime() + outHere * recheckSeconds;

                    // 4. Those who stepped out press the keypad again a little later
                    for (Request r : s.getBoarding()) {
                        if (!boarded.contains(r)) {
                            Passenger p = passengerById.get(r.getRequestId());
                            eventLine.add(new Event(departure + reRequestDelaySeconds, p, EventType.KEYPAD_REQUEST, null));
                        }
                    }
                    stepOuts += outHere;

                    // 5. True waits for those who really got in
                    for (Request r : boarded) {
                        Passenger p = passengerById.get(r.getRequestId());
                        trueWaits.put(originalId.get(p), arrival - p.getTime() * 60);
                    }

                    // 6. What the weight sensor reads when the doors close
                    loadKg.put(carId, load);
                    maxLoadKg = Math.max(maxLoadKg, load);
                    if (load > ratingKg) overweightDepartures++;

                    eventLine.add(new Event(departure, null, EventType.ELEVATOR_REQUEST, carId));
                }

            }
        }

        return new PlanEvaluation(trueWaits, trueJourneys);
    }

    // The brain's own view (includes stale requests of parties that stepped out)
    public PlanEvaluation getBrainResult() {
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

    public int getOverweightChecks() {
        return overweightChecks;
    }

    public int getStepOuts() {
        return stepOuts;
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

    // true: a random newcomer steps out (default); false: the last one in the stop's boarding list
    void setRandomStepOut(boolean random) {
        this.randomStepOut = random;
    }

    void setStepOutSeed(long seed) {
        this.stepOutSeed = seed;
    }

    // for tests only: forces every search to use this budget
    void setBudgetOverride(Double budget) {
        this.budgetOverride = budget;
    }
}
