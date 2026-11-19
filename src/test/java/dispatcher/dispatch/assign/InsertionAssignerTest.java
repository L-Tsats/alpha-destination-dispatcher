package dispatcher.dispatch.assign;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import dispatcher.dispatch.eval.Evaluator;
import dispatcher.dispatch.eval.PlanScorer;
import dispatcher.dispatch.eval.Request;
import dispatcher.dispatch.eval.Stop;
import dispatcher.dispatch.search.Search;

class InsertionAssignerTest {

    private static List<Integer> floors(List<Stop> route) {
        return route.stream().map(Stop::getFloor).toList();
    }

    @Test
    void emptyRouteJustAddsPickupAndDropoff() {
        InsertionAssigner assigner = new InsertionAssigner(40);
        Request a = new Request("A", 3, 9, 0.0);

        Insertion result = assigner.best(List.of(), a, 1, 0.0);

        assertEquals(List.of(3, 9), floors(result.getRoute()));
        // wait 5.8 + dwell 8 + 6 floors 11.4 = 25.2
        assertEquals(25.2, result.getCost(), 0.001);
    }

    @Test
    void picksTheCheapestPositionsAndKeepsTheOldOrder() {
        InsertionAssigner assigner = new InsertionAssigner(40);
        Request a = new Request("A", 3, 9, 0.0);
        Request b = new Request("B", 5, 7, 0.0);
        List<Stop> route = List.of(
                new Stop(3, List.of(a), List.of()),
                new Stop(9, List.of(), List.of(a)));

        Insertion result = assigner.best(route, b, 1, 0.0);

        // B rides inside A's trip: A journey 47.2 + B journey 33.4 = 80.6, versus 25.2 alone
        // (next best: [5,7,3,9] and [3,9,5,7], both 80.8)
        assertEquals(List.of(3, 5, 7, 9), floors(result.getRoute()));
        assertEquals(55.4, result.getCost(), 0.001);
        assertEquals(2, route.size());   // the input route is untouched
    }

    @Test
    void neverPicksUpWhileTheCarIsFull() {
        InsertionAssigner assigner = new InsertionAssigner(40);
        // three requests already in the car (dropoffs only), capacity is 3
        Request x = new Request("X", 2, 9, 0.0);
        Request y = new Request("Y", 2, 10, 0.0);
        Request z = new Request("Z", 2, 11, 0.0);
        Request b = new Request("B", 5, 7, 0.0);
        List<Stop> route = List.of(
                new Stop(9, List.of(), List.of(x)),
                new Stop(10, List.of(), List.of(y)),
                new Stop(11, List.of(), List.of(z)));

        Insertion result = assigner.best(route, b, 3, 0.0);

        List<Integer> f = floors(result.getRoute());
        assertEquals(5, f.size());
        // the pickup (floor 5) can't be first: that would make 4 people in the car
        assertTrue(f.indexOf(5) >= 1);
    }

    @Test
    void newPartyJoinsAnExistingStopAtItsFloor() {
        Request a = new Request("A", 3, 9, 0.0);
        Request b = new Request("B", 3, 7, 0.0);
        List<Stop> route = List.of(
                new Stop(3, List.of(a), List.of()),
                new Stop(9, List.of(), List.of(a)));

        Insertion result = new InsertionAssigner(40).best(route, b, 1, 0.0);

        // both board at 3 together; [3, 7, 9] totals 58.6 s against 64.2 s for [3, 9, 7]
        assertEquals(List.of(3, 7, 9), floors(result.getRoute()));
        assertEquals(2, result.getRoute().get(0).getBoarding().size());
    }

    @Test
    void aFullFloorMakesTheElevatorCostlyButStillGivesARoute() {
        // three parties already wait at floor 5 for this elevator (capacity 3)
        Request x = new Request("X", 5, 9, 0.0);
        Request y = new Request("Y", 5, 10, 0.0);
        Request z = new Request("Z", 5, 11, 0.0);
        Request d = new Request("D", 5, 12, 0.0);
        List<Stop> route = List.of(
                new Stop(5, List.of(x, y, z), List.of()),
                new Stop(9, List.of(), List.of(x)),
                new Stop(10, List.of(), List.of(y)),
                new Stop(11, List.of(), List.of(z)));

        Insertion result = new InsertionAssigner(40).best(route, d, 1, 0.0);

        // a fourth party at 5 boards with the others wherever it is inserted: over capacity
        assertTrue(result.getCost() >= 1_000_000.0);
        assertEquals(5, result.getRoute().size());           // still a complete route, no crash
    }

    @Test
    void insertionNeverBeatsTheFullSearch() {
        Request a = new Request("A", 3, 9, 0.0);
        Request b = new Request("B", 15, 4, 0.0);
        Request c = new Request("C", 6, 12, 0.0);

        // best route for A and B alone, then insert C into it
        List<Stop> routeAB = new Search(40).searchBestroute(List.of(a, b), 1, 0.0);
        Insertion ins = new InsertionAssigner(40).best(routeAB, c, 1, 0.0);

        Evaluator ev = new Evaluator(40);
        double insertionTotal = PlanScorer.deliveryTotal(ev.evaluate(ins.getRoute(), 1, 0.0));

        // full search over all three
        List<Stop> full = new Search(40).searchBestroute(List.of(a, b, c), 1, 0.0);
        double fullTotal = PlanScorer.deliveryTotal(ev.evaluate(full, 1, 0.0));

        assertTrue(fullTotal <= insertionTotal + 1e-9);
    }

}
