package dispatcher.sim.runner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import dispatcher.dispatch.eval.PlanEvaluation;
import dispatcher.sim.traffic.Passenger;

public class RunnerTest {

    @Test
    void overweightPartyStepsOutReRequestsAndIsStillServed() {
        // three 250 kg parties, all 3 -> 9 (at 60, 60.6 and 61.2 s)
        // first trip: party 1 boards at 3; parties 2 and 3 join at a second stop at 3 -> 750 kg
        // party 3 (boarded last) steps out, re-requests, and rides on a later trip
        Runner runner = new Runner(40);
        runner.setFixedSearchSeconds(0.0);
        PlanEvaluation truth = runner.run(List.of(
            new Passenger(3, 3, 9, 1.00, 250.0),
            new Passenger(3, 3, 9, 1.01, 250.0),
            new Passenger(3, 3, 9, 1.02, 250.0)));

        assertEquals(1, runner.getOverweightChecks());         // one stop was over 630 kg
        assertEquals(1, runner.getStepOuts());                 // one party stepped out
        assertEquals(0, runner.getOverweightDepartures());     // nobody left over the rating
        assertEquals(500.0, runner.getMaxLoadKg(), 0.001);     // two parties is the most that rode

        assertEquals(3, truth.getDeliveryWaits().size());      // every party is delivered exactly once
        assertEquals(4, runner.getBrainResult().getDeliveryWaits().size());   // brain also counts the stale request

        // a random one of parties 2 and 3 stepped out; its true wait runs from its FIRST press,
        // so it is longer than the brain's view of its re-request (r-00004)
        double steppedOutTrueWait = Math.max(
                truth.getPickupWaits().get("r-00002"), truth.getPickupWaits().get("r-00003"));
        double brainWaitRerequest = runner.getBrainResult().getPickupWaits().get("r-00004");
        assertTrue(steppedOutTrueWait > brainWaitRerequest + 25.0);
    }

    @Test
    void withTheSwitchOffTheLastNewcomerStepsOut() {
        Runner runner = new Runner(40);
        runner.setFixedSearchSeconds(0.0);
        runner.setRandomStepOut(false);
        PlanEvaluation truth = runner.run(List.of(
            new Passenger(3, 3, 9, 1.00, 250.0),
            new Passenger(3, 3, 9, 1.01, 250.0),
            new Passenger(3, 3, 9, 1.02, 250.0)));

        // party 3 is last in the boarding list, so it steps out and waits far longer than party 2
        assertEquals(1, runner.getStepOuts());
        assertTrue(truth.getPickupWaits().get("r-00003") > truth.getPickupWaits().get("r-00002") + 25.0);
    }

    @Test
    void severalPartiesStepOutOneCheckAtATime() {
        // three 400 kg parties, all 3 -> 9. Trip 1: party 1 alone, then 2 and 3 board together (1200 kg):
        // check 1 over -> one out (800), check 2 over -> another out (400), check 3 green.
        // Trip 2: both re-requesters board together (800) -> one out. Trip 3: the last one rides alone.
        Runner runner = new Runner(40);
        runner.setFixedSearchSeconds(0.0);
        PlanEvaluation truth = runner.run(List.of(
            new Passenger(3, 3, 9, 1.00, 400.0),
            new Passenger(3, 3, 9, 1.01, 400.0),
            new Passenger(3, 3, 9, 1.02, 400.0)));

        assertEquals(3, runner.getStepOuts());
        assertEquals(0, runner.getOverweightDepartures());
        assertEquals(400.0, runner.getMaxLoadKg(), 0.001);     // never more than one party at a time
        assertEquals(3, truth.getDeliveryWaits().size());      // and everyone still gets there
    }

    @Test
    void lightPartiesNeverCountAsOverweight() {
        Runner runner = new Runner(40);
        runner.setFixedSearchSeconds(0.0);
        runner.run(List.of(
            new Passenger(1, 3, 9, 1.00, 80.0),
            new Passenger(1, 3, 9, 1.01, 80.0)));

        assertEquals(160.0, runner.getMaxLoadKg(), 0.001);
        assertEquals(0, runner.getOverweightDepartures());
    }

    @Test
    public void threeHandBuiltRequestsPrintGrowingPlans() {
        // Passenger(count, fromFloor, toFloor, time in MINUTES, weight)
        List<Passenger> passengers = List.of(
            new Passenger(1, 3, 9, 0.5, 80.0),
            new Passenger(1, 5, 7, 1.0, 80.0),
            new Passenger(1, 2, 4, 1.5, 80.0)
        );

        new Runner(40).run(passengers);
    }

    @Test
    public void singlePassengerWaitAndJourney() {
        List<Passenger> passengers = List.of(new Passenger(1, 3, 9, 1.0, 80.0));

        Runner runner = new Runner(40);
        runner.setFixedSearchSeconds(0.0);
        PlanEvaluation result = runner.run(passengers);

        assertEquals(5.8, result.getPickupWaits().get("r-00001"), 0.001);
        assertEquals(22.3, result.getDeliveryWaits().get("r-00001"), 0.001);
    }

    @Test
    public void carRestartsAfterGoingIdle() {
        // first passenger is finished long before the second arrives, so the car idles in between
        List<Passenger> passengers = List.of(
            new Passenger(1, 3, 9, 1.0, 80.0),
            new Passenger(1, 9, 3, 5.0, 80.0)   // 300 s, car is waiting at floor 9
        );

        Runner runner = new Runner(40);
        runner.setFixedSearchSeconds(0.0);
        PlanEvaluation result = runner.run(passengers);

        // second passenger: same floor as the car, gap 0 -> wait 0
        // dwell 5.1, then gap 6 = 11.4 s -> arrives at 300 + 5.1 + 11.4 = 316.5 -> journey 16.5
        assertEquals(0.0, result.getPickupWaits().get("r-00002"), 0.001);
        assertEquals(16.5, result.getDeliveryWaits().get("r-00002"), 0.001);
    }

    @Test
    public void requestArrivingMidTripReordersThePlan() {
        List<Passenger> passengers = List.of(
            new Passenger(1, 3, 9, 1.0, 80.0),    // r-00001 at 60 s
            new Passenger(1, 5, 7, 1.05, 80.0)    // r-00002 at 63 s, car is mid-trip
        );

        Runner runner = new Runner(40);
        runner.setFixedSearchSeconds(0.0);
        PlanEvaluation result = runner.run(passengers);

        // A: car reaches 3 at 65.8 -> wait 5.8 (committed before B arrives)
        assertEquals(5.8, result.getPickupWaits().get("r-00001"), 0.001);
        // B is picked up at 5 (arrival 76.7) and dropped at 7 (87.6) BEFORE A's dropoff at 9
        assertEquals(13.7, result.getPickupWaits().get("r-00002"), 0.001);
        assertEquals(24.6, result.getDeliveryWaits().get("r-00002"), 0.001);
        // A's dropoff at 9 is delayed to 98.5 -> journey 38.5 (it would be 22.3 without B)
        assertEquals(38.5, result.getDeliveryWaits().get("r-00001"), 0.001);
    }

    @Test
    void normalRunHasNoOverdueSearches() {
        Runner runner = new Runner(10);
        PlanEvaluation out = runner.run(List.of(
                new Passenger(1, 3, 9, 1.0, 80.0),
                new Passenger(1, 5, 7, 1.05, 80.0)));

        assertEquals(0, runner.getOverdueSearches());
        assertEquals(2, out.getDeliveryWaits().size());
        assertTrue(runner.getMaxSearchMs() >= 0);
    }

    @Test
    void everyoneIsServedEvenWhenEverySearchAborts() {
        Runner runner = new Runner(10);
        runner.setBudgetOverride(-1.0);   // every search aborts immediately

        PlanEvaluation out = runner.run(List.of(
                new Passenger(1, 3, 9, 1.0, 80.0),
                new Passenger(1, 5, 7, 1.05, 80.0)));

        assertEquals(2, out.getDeliveryWaits().size());
        assertEquals(2, runner.getOverdueSearches());
    }

    @Test
    void reSearchAtCarEventFixesAppendedOrder() {
        Runner runner = new Runner(40);
        runner.setBudgetOverride(-1.0);       // every keypad search aborts, so requests are appended
        runner.setFixedSearchSeconds(0.0);

        PlanEvaluation result = runner.run(List.of(
                new Passenger(1, 3, 9, 1.0, 80.0),          // r-00001 at 60 s
                new Passenger(1, 20, 25, 1.05, 80.0),       // r-00002 at 63 s
                new Passenger(1, 10, 12, 64.0 / 60.0, 80.0) // r-00003 at 64 s
        ));

        assertEquals(3, runner.getOverdueSearches());
        assertEquals(22.3, result.getDeliveryWaits().get("r-00001"), 0.01);

        // after the re-search the car serves C (10 -> 12) before B (20 -> 25)
        assertEquals(27.6417, result.getPickupWaits().get("r-00003"), 0.01);
        assertEquals(38.5417, result.getDeliveryWaits().get("r-00003"), 0.01);
        assertEquals(58.8417, result.getPickupWaits().get("r-00002"), 0.01);
        assertEquals(73.9417, result.getDeliveryWaits().get("r-00002"), 0.01);
    }

    @Test
    void lateSearchStillServesEveryone() {
        Runner runner = new Runner(40);
        runner.setFixedSearchSeconds(1000.0);   // every search "takes" 1000 s of sim time

        PlanEvaluation out = runner.run(List.of(
                new Passenger(1, 3, 9, 1.0, 80.0),     // r-00001 at 60 s
                new Passenger(1, 5, 7, 1.05, 80.0)));  // r-00002 at 63 s

        assertEquals(2, out.getDeliveryWaits().size());   // nobody is dropped
        assertEquals(2, runner.getOverdueSearches());     // both plans came too late

        // the idle car waits for the plan: it starts at 60 + 1000, reaches floor 3 at 1065.8
        assertEquals(1005.8, out.getPickupWaits().get("r-00001"), 0.01);
    }

    @Test
    void twoCarsSplitTwoRequests() {
        Runner runner = new Runner(40, 2);
        runner.setFixedSearchSeconds(0.0);

        PlanEvaluation result = runner.run(List.of(
                new Passenger(1, 3, 9, 1.0, 80.0),     // r-00001 at 60 s -> car A
                new Passenger(1, 5, 7, 1.05, 80.0)));  // r-00002 at 63 s -> car B

        assertEquals(5.8, result.getPickupWaits().get("r-00001"), 0.001);
        assertEquals(22.3, result.getDeliveryWaits().get("r-00001"), 0.001);

        // B starts idle at floor 1: pickup at 5 is gap 4 = 8.6 s
        assertEquals(8.6, result.getPickupWaits().get("r-00002"), 0.001);
        // 63 + 8.6 = 71.6, dwell 5.1 -> 76.7, then gap 2 = 5.8 -> 82.5, so journey 19.5
        assertEquals(19.5, result.getDeliveryWaits().get("r-00002"), 0.001);
    }

    @Test
    void threeCarsServeEveryoneInABurst() {
        Runner runner = new Runner(40, 3);
        runner.setFixedSearchSeconds(0.0);

        PlanEvaluation out = runner.run(List.of(
                new Passenger(1, 3, 9, 1.00, 80.0),
                new Passenger(1, 5, 7, 1.01, 80.0),
                new Passenger(1, 12, 2, 1.02, 80.0),
                new Passenger(1, 8, 20, 1.03, 80.0),
                new Passenger(1, 15, 1, 1.04, 80.0),
                new Passenger(1, 6, 30, 1.05, 80.0)));

        assertEquals(6, out.getPickupWaits().size());
        assertEquals(6, out.getDeliveryWaits().size());
        for (String id : out.getPickupWaits().keySet()) {
            assertTrue(out.getDeliveryWaits().get(id) > out.getPickupWaits().get(id));
        }
        assertEquals(0, runner.getOverdueSearches());
    }

    private double totalJourney(boolean reSearch) {
        Runner runner = new Runner(40);
        runner.setBudgetOverride(-1.0);       // every keypad search aborts, so insertion plans
        runner.setFixedSearchSeconds(0.0);
        runner.setReSearchEnabled(reSearch);

        PlanEvaluation out = runner.run(List.of(
                new Passenger(1, 26, 29, 1.0, 80.0),
                new Passenger(1, 8, 34, 61.0 / 60.0, 80.0),
                new Passenger(1, 15, 25, 62.0 / 60.0, 80.0),
                new Passenger(1, 35, 7, 63.0 / 60.0, 80.0)));

        assertEquals(4, out.getDeliveryWaits().size());
        return out.getDeliveryWaits().values().stream().mapToDouble(Double::doubleValue).sum();
    }

    @Test
    void reSearchNeverMakesTheInsertionPlanWorse() {
        double with = totalJourney(true);
        double without = totalJourney(false);
        assertTrue(with <= without + 1e-9);
    }

}
