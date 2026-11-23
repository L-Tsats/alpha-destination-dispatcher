package dispatcher.integration;

import java.util.List;

import dispatcher.dispatch.eval.PlanEvaluation;
import dispatcher.sim.runner.Runner;
import dispatcher.sim.traffic.Passenger;

/**
 * Demonstration of comparing Search vs DirectionalSearch algorithms
 * using the same Runner instance.
 */
public class ComparisonDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("Smart Elevator Destination Dispatcher - Algorithm Comparison");
        System.out.println("=".repeat(80));
        System.out.println();

        // Define a passenger scenario
        List<Passenger> passengers = List.of(
            new Passenger(1, 3, 9, 1.0, 80.0),
            new Passenger(1, 5, 7, 1.05, 80.0),
            new Passenger(1, 2, 4, 1.10, 80.0),
            new Passenger(1, 10, 15, 1.15, 80.0),
            new Passenger(1, 12, 20, 1.20, 80.0),
            new Passenger(1, 8, 25, 1.25, 80.0)
        );

        System.out.println("Test Scenario: 6 passengers, 3 elevators, 40 floors");
        System.out.println("Passengers:");
        for (int i = 0; i < passengers.size(); i++) {
            Passenger p = passengers.get(i);
            System.out.printf("  %d. Floor %d → %d at %.2f min%n", 
                i + 1, p.getFromFloor(), p.getToFloor(), p.getTime());
        }
        System.out.println();

        // Create a single Runner instance
        Runner runner = new Runner(40, 3);
        runner.setFixedSearchSeconds(0.0);

        System.out.println("-".repeat(80));
        System.out.println("Running with ORIGINAL SEARCH algorithm...");
        System.out.println("-".repeat(80));
        
        long start1 = System.currentTimeMillis();
        PlanEvaluation normalResult = runner.run(passengers, false);
        long duration1 = System.currentTimeMillis() - start1;

        System.out.println("Original Search Results:");
        printResults(normalResult, runner, duration1);

        System.out.println();
        System.out.println("-".repeat(80));
        System.out.println("Running with DIRECTIONAL SEARCH algorithm...");
        System.out.println("-".repeat(80));

        long start2 = System.currentTimeMillis();
        PlanEvaluation directionalResult = runner.run(passengers, true);
        long duration2 = System.currentTimeMillis() - start2;

        System.out.println("Directional Search Results:");
        printResults(directionalResult, runner, duration2);

        System.out.println();
        System.out.println("=".repeat(80));
        System.out.println("COMPARISON SUMMARY");
        System.out.println("=".repeat(80));

        double normalAvgWait = normalResult.getPickupWaits().values().stream()
            .mapToDouble(Double::doubleValue).average().orElse(0.0);
        double directionalAvgWait = directionalResult.getPickupWaits().values().stream()
            .mapToDouble(Double::doubleValue).average().orElse(0.0);

        double normalAvgJourney = normalResult.getDeliveryWaits().values().stream()
            .mapToDouble(Double::doubleValue).average().orElse(0.0);
        double directionalAvgJourney = directionalResult.getDeliveryWaits().values().stream()
            .mapToDouble(Double::doubleValue).average().orElse(0.0);

        System.out.printf("%-30s %15s %15s%n", "Metric", "Original", "Directional");
        System.out.println("-".repeat(80));
        System.out.printf("%-30s %15.2f %15.2f%n", "Avg Wait Time (s)", normalAvgWait, directionalAvgWait);
        System.out.printf("%-30s %15.2f %15.2f%n", "Avg Journey Time (s)", normalAvgJourney, directionalAvgJourney);
        System.out.printf("%-30s %15d %15d%n", "Passengers Served", 
            normalResult.getDeliveryWaits().size(), directionalResult.getDeliveryWaits().size());
        System.out.printf("%-30s %15d ms %12d ms%n", "Execution Time", duration1, duration2);
        System.out.println("=".repeat(80));
    }

    private static void printResults(PlanEvaluation result, Runner runner, long durationMs) {
        System.out.printf("  Passengers served: %d%n", result.getDeliveryWaits().size());
        
        double avgWait = result.getPickupWaits().values().stream()
            .mapToDouble(Double::doubleValue).average().orElse(0.0);
        double maxWait = result.getPickupWaits().values().stream()
            .mapToDouble(Double::doubleValue).max().orElse(0.0);
        
        double avgJourney = result.getDeliveryWaits().values().stream()
            .mapToDouble(Double::doubleValue).average().orElse(0.0);
        double maxJourney = result.getDeliveryWaits().values().stream()
            .mapToDouble(Double::doubleValue).max().orElse(0.0);

        System.out.printf("  Average wait time: %.2f seconds%n", avgWait);
        System.out.printf("  Maximum wait time: %.2f seconds%n", maxWait);
        System.out.printf("  Average journey time: %.2f seconds%n", avgJourney);
        System.out.printf("  Maximum journey time: %.2f seconds%n", maxJourney);
        System.out.printf("  Overdue searches: %d%n", runner.getOverdueSearches());
        System.out.printf("  Total search time: %.2f ms%n", runner.getTotalSearchMs());
        System.out.printf("  Max search time: %.2f ms%n", runner.getMaxSearchMs());
        System.out.printf("  Execution time: %d ms%n", durationMs);
    }
}
