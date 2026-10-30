package dispatcher.dispatch.search;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import dispatcher.dispatch.eval.PlanEvaluation;
import dispatcher.dispatch.eval.Request;
import dispatcher.dispatch.eval.Stop;

public class SearchTest {

    @Test
    public void buildsFullPlanPickupNearestFirst() {
        Request r1 = new Request("r1", 3, 9, 0.0);
        Request r2 = new Request("r2", 5, 7, 0.0);

        Search search = new Search(40);

        long startNanos = System.nanoTime();
        List<Stop> best = search.run(List.of(r1, r2));
        long elapsedNanos = System.nanoTime() - startNanos;

        // --- correctness ---
        // every request both picked up and delivered -> 4 stops
        assertEquals(4, best.size());

        // best pickup order is nearest-first from floor 1: floor 3 (r1) then floor 5 (r2)
        List<Integer> pickupFloors = new ArrayList<>();
        for (Stop s : best) {
            if (!s.getBoarding().isEmpty()) pickupFloors.add(s.getFloor());
        }
        assertEquals(List.of(3, 5), pickupFloors);

        // --- diagnostics (printed, not asserted) ---
        System.out.println("=== Search diagnostics ===");
        System.out.printf("elapsed: %.3f ms%n", elapsedNanos / 1_000_000.0);
        System.out.println("bestScore (avg pickup wait): " + search.getBestScore());
        PlanEvaluation eval = search.getBestPlanEvaluation();
        System.out.println("pickupWaits:   " + eval.getPickupWaits());
        System.out.println("deliveryWaits: " + eval.getDeliveryWaits());
        System.out.println("bestRoute floors: " + pickupFloors + " + dropoffs");
    }
}
