package dispatcher.sim.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import dispatcher.dispatch.assign.InsertionAssigner;
import dispatcher.dispatch.eval.Evaluator;
import dispatcher.dispatch.eval.PlanEvaluation;
import dispatcher.dispatch.eval.PlanScorer;
import dispatcher.dispatch.eval.Request;
import dispatcher.dispatch.eval.Status;
import dispatcher.dispatch.eval.Stop;
import dispatcher.dispatch.search.Search;
import dispatcher.sim.traffic.Generator;
import dispatcher.sim.traffic.Knot;
import dispatcher.sim.traffic.Passenger;
import dispatcher.sim.traffic.Scenario;
import dispatcher.sim.traffic.TrafficType;

class RunnerScenarioTest {

    @Test
    void generatedScenarioIsFullyServed() {
        // 20 minutes at a constant 0.4 requests/min, about 8 requests
        Scenario scenario = Scenario.of(20, List.of(new Knot(0, 0.4)), TrafficType.MIXED);
        List<Passenger> passengers = new Generator(42).simulate(scenario, 40);

        Runner runner = new Runner(40, 3);
        PlanEvaluation out = runner.run(passengers);

        System.out.println("passengers: " + passengers.size());
        System.out.println("served (pickup/journey): "
                + out.getPickupWaits().size() + "/" + out.getDeliveryWaits().size());
        System.out.println("mean wait: " + mean(out.getPickupWaits()));
        System.out.println("mean journey: " + mean(out.getDeliveryWaits()));
        System.out.println("overdue searches: " + runner.getOverdueSearches());
        System.out.println("max search ms: " + runner.getMaxSearchMs());
        System.out.println("total search ms: " + runner.getTotalSearchMs());

        assertTrue(passengers.size() > 0);
        assertEquals(passengers.size(), out.getPickupWaits().size());
        assertEquals(passengers.size(), out.getDeliveryWaits().size());
    }

    private static double mean(Map<String, Double> values) {
        double sum = 0;
        for (double v : values.values()) {
            sum += v;
        }
        return sum / values.size();
    }

    @Test
    void sweepCarsAndLoad() {
        double[] lambdas = {0.4, 1.0, 1.5};
        for (double lambda : lambdas) {
            for (int cars = 1; cars <= 4; cars++) {
                Scenario scenario = Scenario.of(10, List.of(new Knot(0, lambda)), TrafficType.MIXED);
                List<Passenger> passengers = new Generator(42).simulate(scenario, 40);

                Runner runner = new Runner(40, cars);
                PlanEvaluation out = runner.run(passengers);

                System.out.println(String.format(java.util.Locale.ROOT,
                        "lambda=%.1f cars=%d n=%d wait=%.1f journey=%.1f overdue=%d maxMs=%.1f totalMs=%.1f",
                        lambda, cars, passengers.size(),
                        mean(out.getPickupWaits()), mean(out.getDeliveryWaits()),
                        runner.getOverdueSearches(), runner.getMaxSearchMs(), runner.getTotalSearchMs()));

                assertEquals(passengers.size(), out.getDeliveryWaits().size());
            }
        }
    }

    @Test
    void sweepLoadCarsAndSeeds() {
        double[] lambdas = {2.0, 4.0, 6.0};
        int[] carCounts = {1, 2, 4};
        int seeds = 5;

        for (double lambda : lambdas) {
            for (int cars : carCounts) {
                double wait = 0, journey = 0, worstJourney = 0, overdue = 0, maxMs = 0;
                int n = 0, maxPending = 0;

                for (int seed = 1; seed <= seeds; seed++) {
                    Scenario scenario = Scenario.of(5, List.of(new Knot(0, lambda)), TrafficType.MIXED);
                    List<Passenger> ps = new Generator(seed).simulate(scenario, 40);

                    Runner runner = new Runner(40, cars);
                    runner.setBudgetOverride(0.1);   // cap every search at 0.1 s
                    runner.setBudgetOverride(0.1);
                    runner.setReSearchBudgetOverride(0.1);

                    PlanEvaluation out = runner.run(ps);

                    assertEquals(ps.size(), out.getDeliveryWaits().size());

                    n += ps.size();
                    wait += mean(out.getPickupWaits());
                    journey += mean(out.getDeliveryWaits());
                    worstJourney = Math.max(worstJourney, max(out.getDeliveryWaits()));
                    overdue += runner.getOverdueSearches();
                    maxMs = Math.max(maxMs, runner.getMaxSearchMs());
                    maxPending = Math.max(maxPending, runner.getMaxPending());
                }

                System.out.println(String.format(java.util.Locale.ROOT,
                        "lambda=%.1f cars=%d requests=%d wait=%.1f journey=%.1f worstJourney=%.1f overdue/run=%.1f maxMs=%.1f maxPending=%d",
                        lambda, cars, n, wait / seeds, journey / seeds, worstJourney, overdue / seeds, maxMs, maxPending));
            }
        }
    }

    @Test
    void findScenarioWhereInsertionLosesAfterTheFirstStopIsCommitted() {
        InsertionAssigner assigner = new InsertionAssigner(40);
        Evaluator ev = new Evaluator(40);
        Random rnd = new Random(1);
        int losses = 0;
        double gapSum = 0, searchSum = 0;
        boolean printed = false;

        for (int trial = 0; trial < 2000; trial++) {
            int from0 = rnd.nextInt(40) + 1;
            int to0 = otherFloor(rnd, from0);
            Request r0 = new Request("R0", from0, to0, 0.0);
            r0.setStatus(Status.BOARDED);   // the car has already picked R0 up

            List<Request> reqs = new ArrayList<>(List.of(r0));
            List<Stop> route = new ArrayList<>(List.of(
                    new Stop(to0, new ArrayList<>(), new ArrayList<>(List.of(r0)))));
            StringBuilder sb = new StringBuilder("R0 " + from0 + "->" + to0 + " (picked up)  ");

            for (int k = 1; k <= 3; k++) {
                int from = rnd.nextInt(40) + 1;
                int to = otherFloor(rnd, from);
                Request r = new Request("R" + k, from, to, 0.0);
                reqs.add(r);
                sb.append("R" + k + " " + from + "->" + to + "  ");
                route = new ArrayList<>(assigner.best(route, r, from0, 0.0).getRoute());
            }

            double insTotal = PlanScorer.deliveryTotal(ev.evaluate(route, from0, 0.0));
            List<Stop> full = new Search(40).searchBestroute(reqs, from0, 0.0);
            double fullTotal = PlanScorer.deliveryTotal(ev.evaluate(full, from0, 0.0));

            gapSum += insTotal - fullTotal;
            searchSum += fullTotal;
            if (insTotal > fullTotal + 5.0) {
                losses++;
                if (!printed) {
                    printed = true;
                    System.out.println(String.format(java.util.Locale.ROOT,
                            "%sinsertion=%.1f search=%.1f", sb, insTotal, fullTotal));
                }
            }
        }
        System.out.println(String.format(java.util.Locale.ROOT,
                "lost by >5 s in %d of 2000 trials; average gap %.2f%% of the search total",
                losses, 100.0 * gapSum / searchSum));
    }

    private static int otherFloor(Random rnd, int floor) {
        int other;
        do { other = rnd.nextInt(40) + 1; } while (other == floor);
        return other;
    }



    private static double max(Map<String, Double> values) {
        return values.values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
    }

    @Test
    void timeInsertionAtDifferentRouteSizes() {
        InsertionAssigner assigner = new InsertionAssigner(40);
        Random rnd = new Random(1);

        for (int n : new int[] {2, 4, 6, 8, 12, 16}) {
            List<Stop> route = new ArrayList<>();
            for (int k = 0; k < n; k++) {
                int from = rnd.nextInt(40) + 1;
                Request r = new Request("R" + k, from, otherFloor(rnd, from), 0.0);
                route = new ArrayList<>(assigner.best(route, r, 1, 0.0).getRoute());
            }
            int from = rnd.nextInt(40) + 1;
            Request extra = new Request("X", from, otherFloor(rnd, from), 0.0);

            for (int w = 0; w < 200; w++) {
                assigner.best(route, extra, 1, 0.0);          // warm-up, so the first calls don't distort
            }
            int reps = 500;
            long t0 = System.nanoTime();
            for (int w = 0; w < reps; w++) {
                assigner.best(route, extra, 1, 0.0);
            }
            double ms = (System.nanoTime() - t0) / 1e6 / reps;
            System.out.println(String.format(java.util.Locale.ROOT,
                    "%2d requests in route (%2d stops): %.3f ms per insertion", n, route.size(), ms));
        }
    }

    @Test
    void compareGreedyAndInsertionAssignment() {
        int cars = 2;
        int seeds = 5;
        for (double lambda : new double[] {2.0, 4.0, 6.0}) {
            for (boolean insertion : new boolean[] {false, true}) {
                double wait = 0, journey = 0, worst = 0, maxAssignMs = 0;
                for (int seed = 1; seed <= seeds; seed++) {
                    Scenario scenario = Scenario.of(5, List.of(new Knot(0, lambda)), TrafficType.MIXED);
                    List<Passenger> ps = new Generator(seed).simulate(scenario, 40);

                    Runner runner = new Runner(40, cars);
                    runner.setInsertionAssignment(insertion);
                    runner.setBudgetOverride(0.1);
                    runner.setReSearchBudgetOverride(0.1);
                    PlanEvaluation out = runner.run(ps);

                    assertEquals(ps.size(), out.getDeliveryWaits().size());
                    wait += mean(out.getPickupWaits());
                    journey += mean(out.getDeliveryWaits());
                    worst = Math.max(worst, max(out.getDeliveryWaits()));
                    maxAssignMs = Math.max(maxAssignMs, runner.getMaxAssignMs());
                }
                System.out.println(String.format(java.util.Locale.ROOT,
                        "lambda=%.1f cars=%d insertion=%b cap=0.1s wait=%.1f journey=%.1f worstJourney=%.1f maxAssignMs=%.2f",
                        lambda, cars, insertion, wait / seeds, journey / seeds, worst, maxAssignMs));
            }
        }
    }


}
