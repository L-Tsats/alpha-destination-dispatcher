package dispatcher.dispatch.search;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import dispatcher.dispatch.eval.PlanEvaluation;
import dispatcher.dispatch.eval.Request;
import dispatcher.dispatch.eval.Status;
import dispatcher.dispatch.eval.Stop;

public class SearchTest {

    @Test
    void partiesAtTheSameFloorBoardTogether() {
        Request a = new Request("a", 3, 9, 0.0);
        Request b = new Request("b", 3, 7, 0.0);
        List<Stop> route = new Search(40).searchBestroute(List.of(a, b), 1, 0.0);

        assertEquals(3, route.size());                       // [3 (a and b board), 7, 9]
        assertEquals(3, route.get(0).getFloor());
        assertEquals(2, route.get(0).getBoarding().size());
    }

    @Test
    void riderLeavesWhereTheNextPartyBoards() {
        Request a = new Request("a", 2, 5, 0.0);
        a.setStatus(Status.BOARDED);
        Request b = new Request("b", 5, 9, 0.0);
        List<Stop> route = new Search(40).searchBestroute(List.of(a, b), 2, 0.0);

        assertEquals(2, route.size());                       // [5 (a off, b on), 9]
        assertEquals(1, route.get(0).getAlighting().size());
        assertEquals(1, route.get(0).getBoarding().size());
    }

    @Test
    public void buildsFullPlanPickupNearestFirst() {
        Request r1 = new Request("r1", 3, 9, 0.0);
        Request r2 = new Request("r2", 5, 7, 0.0);

        Search search = new Search(40);

        long startNanos = System.nanoTime();
        List<Stop> best = search.searchBestroute(List.of(r1, r2), 1, 0.0);
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
    
    @Test
    void zeroBudgetAbortsBeforeAnyPlan() {
        Search s = new Search(10);
        List<Request> reqs = List.of(
                new Request("a", 3, 8, 0.0),
                new Request("b", 5, 2, 0.0));

        List<Stop> result = s.searchBestroute(reqs, 1, 0.0, -1.0);

        assertTrue(s.wasAborted());
        assertNull(result);
    }

    @Test
    void generousBudgetFinishesWithoutAbort() {
        Search s = new Search(10);
        List<Request> reqs = List.of(
                new Request("a", 3, 8, 0.0),
                new Request("b", 5, 2, 0.0));

        List<Stop> result = s.searchBestroute(reqs, 1, 0.0, 10.0);

        assertFalse(s.wasAborted());
        assertNotNull(result);
        assertEquals(4, result.size()); // 2 pickups + 2 dropoffs
    }

    @Test
    void cancelCheckThatSaysTrueAbortsAtOnce() {
        Search s = new Search(10);
        s.setCancelCheck(() -> true);
        List<Request> reqs = List.of(
                new Request("a", 3, 8, 0.0),
                new Request("b", 5, 2, 0.0));

        List<Stop> result = s.searchBestroute(reqs, 1, 0.0);

        assertTrue(s.wasAborted());
        assertNull(result);
    }

    @Test
    void cancelCheckThatFlipsMidSearchKeepsBestSoFar() {
        Search s = new Search(10);
        AtomicInteger calls = new AtomicInteger();
        s.setCancelCheck(() -> calls.incrementAndGet() > 50);   // cancel after 50 checks
        List<Request> reqs = List.of(
                new Request("a", 3, 8, 0.0),
                new Request("b", 5, 2, 0.0),
                new Request("c", 7, 9, 0.0),
                new Request("d", 2, 6, 0.0),
                new Request("e", 9, 1, 0.0));

        List<Stop> result = s.searchBestroute(reqs, 1, 0.0);

        assertTrue(s.wasAborted());
        assertNotNull(result);   // the first complete plan is found well before check 50
    }

    @Test
    void searchIsReusableAfterACancel() {
        Search s = new Search(10);
        List<Request> reqs = List.of(
                new Request("a", 3, 8, 0.0),
                new Request("b", 5, 2, 0.0));

        s.setCancelCheck(() -> true);
        s.searchBestroute(reqs, 1, 0.0);
        assertTrue(s.wasAborted());

        s.setCancelCheck(() -> false);
        List<Stop> result = s.searchBestroute(reqs, 1, 0.0);

        assertFalse(s.wasAborted());
        assertEquals(4, result.size());
    }

}
