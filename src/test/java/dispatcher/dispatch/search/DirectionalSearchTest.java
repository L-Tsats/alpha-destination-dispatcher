package dispatcher.dispatch.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import dispatcher.dispatch.eval.Request;
import dispatcher.dispatch.eval.Status;
import dispatcher.dispatch.eval.Stop;

class DirectionalSearchTest {

    @Test
    void singleRequestProducesPickupAndDropoff() {
        DirectionalSearch search = new DirectionalSearch();
        List<Request> requests = List.of(new Request("r1", 3, 9, 60.0));

        List<Stop> route = search.getSuboptimalStops(requests, 1);

        assertEquals(2, route.size());
        assertEquals(3, route.get(0).getFloor());
        assertEquals(1, route.get(0).getBoarding().size());
        assertEquals(0, route.get(0).getAlighting().size());

        assertEquals(9, route.get(1).getFloor());
        assertEquals(0, route.get(1).getBoarding().size());
        assertEquals(1, route.get(1).getAlighting().size());
    }

    @Test
    void twoRequestsSameDirectionServedInOrder() {
        DirectionalSearch search = new DirectionalSearch();
        List<Request> requests = List.of(
            new Request("r1", 3, 9, 60.0),
            new Request("r2", 5, 7, 61.0)
        );

        List<Stop> route = search.getSuboptimalStops(requests, 1);

        // Should go UP: 3 (pickup r1), 5 (pickup r2), 7 (dropoff r2), 9 (dropoff r1)
        assertEquals(4, route.size());
        assertEquals(3, route.get(0).getFloor());
        assertEquals(5, route.get(1).getFloor());
        assertEquals(7, route.get(2).getFloor());
        assertEquals(9, route.get(3).getFloor());
    }

    @Test
    void capacityLimitRespected() {
        DirectionalSearch search = new DirectionalSearch();
        // Four requests all wanting to board at floor 5
        List<Request> requests = List.of(
            new Request("r1", 5, 10, 60.0),
            new Request("r2", 5, 11, 61.0),
            new Request("r3", 5, 12, 62.0),
            new Request("r4", 5, 13, 63.0)
        );

        List<Stop> route = search.getSuboptimalStops(requests, 1);

        // First stop at 5: should only board 3 passengers (capacity limit)
        Stop firstStop = route.get(0);
        assertEquals(5, firstStop.getFloor());
        assertEquals(3, firstStop.getBoarding().size());

        // Count total passengers boarded across all stops
        int totalBoarded = route.stream()
            .mapToInt(stop -> stop.getBoarding().size())
            .sum();
        
        // Only 3 can board in a single pass (capacity constraint)
        // The 4th would need to be in a separate request to the brain
        assertEquals(3, totalBoarded, "Only 3 passengers can board due to capacity");
    }

    @Test
    void alreadyBoardedPassengersDroppedOff() {
        DirectionalSearch search = new DirectionalSearch();
        Request r1 = new Request("r1", 3, 9, 60.0);
        r1.setStatus(Status.BOARDED); // Already on board
        Request r2 = new Request("r2", 5, 7, 61.0);

        List<Request> requests = List.of(r1, r2);

        List<Stop> route = search.getSuboptimalStops(requests, 1);

        // Should pick up r2 at 5, drop off r2 at 7, drop off r1 at 9
        assertEquals(3, route.size());
        assertEquals(5, route.get(0).getFloor());
        assertEquals(7, route.get(1).getFloor());
        assertEquals(1, route.get(1).getAlighting().size());
        assertEquals(9, route.get(2).getFloor());
        assertEquals(1, route.get(2).getAlighting().size());
    }

    @Test
    void deliveredRequestsIgnored() {
        DirectionalSearch search = new DirectionalSearch();
        Request r1 = new Request("r1", 3, 9, 60.0);
        r1.setStatus(Status.DELIVERED);
        Request r2 = new Request("r2", 5, 7, 61.0);

        List<Request> requests = List.of(r1, r2);

        List<Stop> route = search.getSuboptimalStops(requests, 1);

        // Should only serve r2
        assertEquals(2, route.size());
        assertEquals(5, route.get(0).getFloor());
        assertEquals(7, route.get(1).getFloor());
    }

    @Test
    void emptyRequestListReturnsEmptyRoute() {
        DirectionalSearch search = new DirectionalSearch();
        List<Request> requests = new ArrayList<>();

        List<Stop> route = search.getSuboptimalStops(requests, 1);

        assertEquals(0, route.size());
    }

    @Test
    void directionReversesWhenNoMoreStopsInCurrentDirection() {
        DirectionalSearch search = new DirectionalSearch();
        List<Request> requests = List.of(
            new Request("r1", 15, 20, 60.0),  // UP
            new Request("r2", 5, 2, 61.0)     // DOWN
        );

        List<Stop> route = search.getSuboptimalStops(requests, 10);

        // Starting at 10, going UP by default: serve 15->20, then reverse DOWN to serve 5->2
        assertTrue(route.size() >= 4);
        
        // Find the stops
        boolean found15 = false, found20 = false, found5 = false, found2 = false;
        for (Stop stop : route) {
            if (stop.getFloor() == 15) found15 = true;
            if (stop.getFloor() == 20) found20 = true;
            if (stop.getFloor() == 5) found5 = true;
            if (stop.getFloor() == 2) found2 = true;
        }
        
        assertTrue(found15 && found20 && found5 && found2, "All floors should be visited");
    }

    @Test
    void pickupBeforeDropoffForSameRequest() {
        DirectionalSearch search = new DirectionalSearch();
        Request r1 = new Request("r1", 5, 10, 60.0);
        
        List<Stop> route = search.getSuboptimalStops(List.of(r1), 1);
        
        // First stop should be pickup at 5
        assertEquals(5, route.get(0).getFloor());
        assertTrue(route.get(0).getBoarding().contains(r1));
        
        // Second stop should be dropoff at 10
        assertEquals(10, route.get(1).getFloor());
        assertTrue(route.get(1).getAlighting().contains(r1));
    }

    @Test
    void pickupAtCurrentFloorServedImmediately() {
        DirectionalSearch search = new DirectionalSearch();
        List<Request> requests = List.of(
            new Request("r1", 5, 10, 60.0),
            new Request("r2", 8, 15, 61.0)
        );

        // Starting at floor 5 where r1 wants to board
        List<Stop> route = search.getSuboptimalStops(requests, 5);

        // First stop should be at current floor 5
        assertEquals(5, route.get(0).getFloor());
        assertEquals(1, route.get(0).getBoarding().size());
    }

    @Test
    void multipleElevatorsPersistSeparateDirections() {
        DirectionalSearch search = new DirectionalSearch();
        
        List<Request> reqA = List.of(new Request("r1", 10, 15, 60.0));
        List<Request> reqB = List.of(new Request("r2", 20, 25, 60.0));
        
        // Elevator A starts at floor 1, going UP
        search.getSuboptimalStops(reqA, 1, "A");
        
        // Elevator B starts at floor 30, should go UP to 30 then DOWN (or stay UP if above)
        List<Stop> routeB = search.getSuboptimalStops(reqB, 30, "B");
        
        // Both should produce valid routes
        assertEquals(2, routeB.size());
    }

    @Test
    void resetDirectionWorks() {
        DirectionalSearch search = new DirectionalSearch();
        
        // Set some direction for elevator A
        search.getSuboptimalStops(List.of(new Request("r1", 10, 15, 60.0)), 1, "A");
        
        // Reset
        search.resetDirection("A");
        
        // After reset, should use default direction (UP)
        List<Stop> route = search.getSuboptimalStops(
            List.of(new Request("r2", 5, 2, 61.0)), // Request going DOWN
            10, 
            "A"
        );
        
        // Should still produce a valid route
        assertEquals(2, route.size());
    }

    @Test
    void noInfiniteLoopWithComplexRequests() {
        DirectionalSearch search = new DirectionalSearch();
        
        // Complex scenario that could potentially cause issues
        List<Request> requests = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            requests.add(new Request("r" + i, i * 2 + 1, i * 2 + 5, 60.0 + i));
        }
        
        List<Stop> route = search.getSuboptimalStops(requests, 1);
        
        // Should complete without hanging
        assertTrue(route.size() > 0);
        assertTrue(route.size() < 100, "Route should not be excessively long");
    }

    @Test
    void threeRequestsAllServed() {
        DirectionalSearch search = new DirectionalSearch();
        Request r1 = new Request("r1", 3, 9, 60.0);
        Request r2 = new Request("r2", 5, 7, 61.0);
        Request r3 = new Request("r3", 2, 4, 62.0);
        
        List<Stop> route = search.getSuboptimalStops(List.of(r1, r2, r3), 1);
        
        // All three should be picked up
        int totalBoarding = route.stream().mapToInt(s -> s.getBoarding().size()).sum();
        assertEquals(3, totalBoarding);
        
        // All three should be dropped off
        int totalAlighting = route.stream().mapToInt(s -> s.getAlighting().size()).sum();
        assertEquals(3, totalAlighting);
    }
}
