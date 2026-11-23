package dispatcher.sim.runner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import dispatcher.dispatch.eval.PlanEvaluation;
import dispatcher.sim.traffic.Passenger;

/**
 * Tests for DirectionalSearch integration with Runner.
 * Verifies that the same Runner instance can execute both algorithms sequentially.
 */
class RunnerDirectionalTest {

    @Test
    void sameRunnerCanExecuteBothAlgorithms() {
        List<Passenger> passengers = List.of(
            new Passenger(1, 3, 9, 1.0, 80.0),
            new Passenger(1, 5, 7, 1.05, 80.0)
        );

        Runner runner = new Runner(40, 1);
        runner.setFixedSearchSeconds(0.0);

        // Run with original Search
        PlanEvaluation normalResult = runner.run(passengers, false);
        
        // Run with DirectionalSearch using the SAME runner instance
        PlanEvaluation directionalResult = runner.run(passengers, true);

        // Both should complete successfully
        assertEquals(2, normalResult.getDeliveryWaits().size());
        assertEquals(2, directionalResult.getDeliveryWaits().size());
        
        // Results may differ but both should be valid
        assertNotNull(normalResult);
        assertNotNull(directionalResult);
    }

    @Test
    void backwardCompatibleRunUsesOriginalSearch() {
        List<Passenger> passengers = List.of(
            new Passenger(1, 3, 9, 1.0, 80.0)
        );

        Runner runner = new Runner(40, 1);
        runner.setFixedSearchSeconds(0.0);

        // Old API without boolean parameter should work
        PlanEvaluation result = runner.run(passengers);

        assertEquals(1, result.getDeliveryWaits().size());
        assertEquals(5.8, result.getPickupWaits().get("r-00001"), 0.001);
    }

    @Test
    void directionalSearchWithSingleElevator() {
        List<Passenger> passengers = List.of(
            new Passenger(1, 3, 9, 1.0, 80.0),
            new Passenger(1, 5, 7, 1.05, 80.0),
            new Passenger(1, 2, 4, 1.10, 80.0)
        );

        Runner runner = new Runner(40, 1);
        runner.setFixedSearchSeconds(0.0);

        PlanEvaluation result = runner.run(passengers, true);

        // All passengers should be served
        assertEquals(3, result.getPickupWaits().size());
        assertEquals(3, result.getDeliveryWaits().size());
        
        // All journeys should be positive
        for (double journey : result.getDeliveryWaits().values()) {
            assertTrue(journey > 0);
        }
    }

    @Test
    void directionalSearchWithTwoElevators() {
        List<Passenger> passengers = List.of(
            new Passenger(1, 3, 9, 1.0, 80.0),
            new Passenger(1, 5, 7, 1.05, 80.0)
        );

        Runner runner = new Runner(40, 2);
        runner.setFixedSearchSeconds(0.0);

        PlanEvaluation result = runner.run(passengers, true);

        // Both passengers should be served, possibly by different elevators
        assertEquals(2, result.getPickupWaits().size());
        assertEquals(2, result.getDeliveryWaits().size());
    }

    @Test
    void directionalSearchWithThreeElevators() {
        List<Passenger> passengers = List.of(
            new Passenger(1, 3, 9, 1.0, 80.0),
            new Passenger(1, 15, 20, 1.01, 80.0),
            new Passenger(1, 25, 30, 1.02, 80.0)
        );

        Runner runner = new Runner(40, 3);
        runner.setFixedSearchSeconds(0.0);

        PlanEvaluation result = runner.run(passengers, true);

        // All three passengers should be served
        assertEquals(3, result.getPickupWaits().size());
        assertEquals(3, result.getDeliveryWaits().size());
    }

    @Test
    void directionalSearchWithFourElevators() {
        List<Passenger> passengers = List.of(
            new Passenger(1, 3, 9, 1.0, 80.0),
            new Passenger(1, 10, 15, 1.01, 80.0),
            new Passenger(1, 20, 25, 1.02, 80.0),
            new Passenger(1, 30, 35, 1.03, 80.0)
        );

        Runner runner = new Runner(40, 4);
        runner.setFixedSearchSeconds(0.0);

        PlanEvaluation result = runner.run(passengers, true);

        // All four passengers should be served
        assertEquals(4, result.getPickupWaits().size());
        assertEquals(4, result.getDeliveryWaits().size());
    }

    @Test
    void secondSimulationNotAffectedByFirst() {
        List<Passenger> passengers = List.of(
            new Passenger(1, 3, 9, 1.0, 80.0)
        );

        Runner runner = new Runner(40, 1);
        runner.setFixedSearchSeconds(0.0);

        // First run
        PlanEvaluation first = runner.run(passengers, false);
        
        // Second run with same input should produce same result
        PlanEvaluation second = runner.run(passengers, false);

        assertEquals(first.getPickupWaits().get("r-00001"), 
                     second.getPickupWaits().get("r-00001"), 0.001);
        assertEquals(first.getDeliveryWaits().get("r-00001"), 
                     second.getDeliveryWaits().get("r-00001"), 0.001);
    }

    @Test
    void directionalSearchRespectsCapacity() {
        // Three passengers at same floor, capacity is 3
        List<Passenger> passengers = List.of(
            new Passenger(1, 5, 10, 1.0, 80.0),
            new Passenger(1, 5, 11, 1.01, 80.0),
            new Passenger(1, 5, 12, 1.02, 80.0)
        );

        Runner runner = new Runner(40, 1);
        runner.setFixedSearchSeconds(0.0);

        PlanEvaluation result = runner.run(passengers, true);

        // All should eventually be served
        assertEquals(3, result.getDeliveryWaits().size());
    }

    @Test
    void newRequestDuringRouteReplansWithDirectional() {
        List<Passenger> passengers = List.of(
            new Passenger(1, 3, 9, 1.0, 80.0),     // First request
            new Passenger(1, 5, 7, 1.05, 80.0)     // Arrives while elevator is moving
        );

        Runner runner = new Runner(40, 1);
        runner.setFixedSearchSeconds(0.0);

        PlanEvaluation result = runner.run(passengers, true);

        // Both should be served
        assertEquals(2, result.getDeliveryWaits().size());
        
        // Second passenger's pickup wait should be reasonable
        assertTrue(result.getPickupWaits().get("r-00002") < 100.0);
    }

    @Test
    void comparisonBetweenSearchAndDirectionalSearch() {
        List<Passenger> passengers = List.of(
            new Passenger(1, 3, 9, 1.0, 80.0),
            new Passenger(1, 5, 7, 1.05, 80.0),
            new Passenger(1, 2, 4, 1.10, 80.0)
        );

        Runner runner = new Runner(40, 1);
        runner.setFixedSearchSeconds(0.0);

        PlanEvaluation normal = runner.run(passengers, false);
        PlanEvaluation directional = runner.run(passengers, true);

        // Both should serve all passengers
        assertEquals(3, normal.getDeliveryWaits().size());
        assertEquals(3, directional.getDeliveryWaits().size());

        // Calculate average journey times
        double normalAvg = normal.getDeliveryWaits().values().stream()
            .mapToDouble(Double::doubleValue).average().orElse(0.0);
        double directionalAvg = directional.getDeliveryWaits().values().stream()
            .mapToDouble(Double::doubleValue).average().orElse(0.0);

        // Both should produce reasonable results (within order of magnitude)
        assertTrue(normalAvg > 0);
        assertTrue(directionalAvg > 0);
        assertTrue(Math.abs(normalAvg - directionalAvg) < 1000.0); // Sanity check
    }

    @Test
    void threeElevatorsServeMultiplePassengers() {
        List<Passenger> passengers = List.of(
            new Passenger(1, 3, 9, 1.00, 80.0),
            new Passenger(1, 5, 7, 1.01, 80.0),
            new Passenger(1, 12, 2, 1.02, 80.0),
            new Passenger(1, 8, 20, 1.03, 80.0),
            new Passenger(1, 15, 1, 1.04, 80.0),
            new Passenger(1, 6, 30, 1.05, 80.0)
        );

        Runner runner = new Runner(40, 3);
        runner.setFixedSearchSeconds(0.0);

        PlanEvaluation result = runner.run(passengers, true);

        // All six passengers should be served
        assertEquals(6, result.getPickupWaits().size());
        assertEquals(6, result.getDeliveryWaits().size());
        
        // No overdue searches should occur in directional mode
        assertEquals(0, runner.getOverdueSearches());
    }

    @Test
    void emptyPassengerListHandledGracefully() {
        Runner runner = new Runner(40, 2);
        runner.setFixedSearchSeconds(0.0);

        PlanEvaluation result = runner.run(List.of(), true);

        assertEquals(0, result.getPickupWaits().size());
        assertEquals(0, result.getDeliveryWaits().size());
    }
}
