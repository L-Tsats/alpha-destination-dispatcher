package dispatcher.dispatch.search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dispatcher.dispatch.eval.Request;
import dispatcher.dispatch.eval.Status;
import dispatcher.dispatch.eval.Stop;

public class DirectionalSearch {
    
    // Store direction per elevator to persist between replanning operations
    private final Map<String, Integer> elevatorDirections = new HashMap<>();
    private static final int CAPACITY = 3;

    /**
     * Calculates a directional route for the given requests.
     * This is a simpler alternative to exhaustive search that serves requests
     * in directional order (UP then DOWN or vice versa).
     * 
     * @param requests List of requests to serve (mix of WAITING and BOARDED)
     * @param startFloor Current floor of the elevator
     * @param elevatorId Identifier for the elevator (to persist direction between calls)
     * @return List of stops in the order they should be visited
     */
    public List<Stop> getSuboptimalStops(List<Request> requests, int startFloor, String elevatorId) {
        List<Stop> route = new ArrayList<>();

        // Create working copies to avoid modifying original request statuses
        List<Request> toPickup = new ArrayList<>();
        List<Request> onboard = new ArrayList<>();

        // Split requests by status - ignore already DELIVERED
        for (Request r : requests) {
            if (r.getStatus() == Status.WAITING) {
                toPickup.add(r);
            } else if (r.getStatus() == Status.BOARDED) {
                onboard.add(r);
            }
            // DELIVERED requests are ignored
        }

        // If no work to do, return empty route
        if (toPickup.isEmpty() && onboard.isEmpty()) {
            return route;
        }

        int currentFloor = startFloor;
        // Get persisted direction for this elevator, default to UP
        int direction = elevatorDirections.getOrDefault(elevatorId, 1);

        int iterations = 0;
        int maxIterations = (toPickup.size() + onboard.size()) * 50; // Safety limit

        // Continue until all passengers are delivered
        while (!toPickup.isEmpty() || !onboard.isEmpty()) {
            if (++iterations > maxIterations) {
                // Safety: prevent infinite loops
                break;
            }

            ArrayList<Integer> floorsAbove = new ArrayList<>();
            ArrayList<Integer> floorsBelow = new ArrayList<>();

            // Floors where passengers must board (pickup)
            for (Request r : toPickup) {
                int targetFloor = r.getFromFloor();
                if (targetFloor >= currentFloor) {
                    if (!floorsAbove.contains(targetFloor)) {
                        floorsAbove.add(targetFloor);
                    }
                } else {
                    if (!floorsBelow.contains(targetFloor)) {
                        floorsBelow.add(targetFloor);
                    }
                }
            }

            // Floors where passengers must alight (dropoff)
            for (Request r : onboard) {
                int targetFloor = r.getToFloor();
                if (targetFloor >= currentFloor) {
                    if (!floorsAbove.contains(targetFloor)) {
                        floorsAbove.add(targetFloor);
                    }
                } else {
                    if (!floorsBelow.contains(targetFloor)) {
                        floorsBelow.add(targetFloor);
                    }
                }
            }

            floorsAbove.sort(Comparator.naturalOrder());
            floorsBelow.sort(Comparator.reverseOrder());

            int nextFloor;

            // Priority: serve current floor first if there's activity here
            if (!floorsAbove.isEmpty() && floorsAbove.get(0) == currentFloor) {
                nextFloor = floorsAbove.remove(0);
            } else if (!floorsBelow.isEmpty() && floorsBelow.get(0) == currentFloor) {
                nextFloor = floorsBelow.remove(0);
            } else if (direction == 1 && !floorsAbove.isEmpty()) {
                // Going UP and there are floors above
                nextFloor = floorsAbove.remove(0);
            } else if (direction == -1 && !floorsBelow.isEmpty()) {
                // Going DOWN and there are floors below
                nextFloor = floorsBelow.remove(0);
            } else if (!floorsAbove.isEmpty()) {
                // Switch to UP
                direction = 1;
                nextFloor = floorsAbove.remove(0);
            } else if (!floorsBelow.isEmpty()) {
                // Switch to DOWN
                direction = -1;
                nextFloor = floorsBelow.remove(0);
            } else {
                // No more floors to visit
                break;
            }

            List<Request> boarding = new ArrayList<>();
            List<Request> alighting = new ArrayList<>();

            // First: passengers alight (leave the elevator)
            for (Request r : onboard) {
                if (r.getToFloor() == nextFloor) {
                    alighting.add(r);
                }
            }
            onboard.removeAll(alighting);

            // Second: passengers board (enter the elevator) - respect capacity
            int currentLoad = onboard.size();
            for (Request r : toPickup) {
                if (r.getFromFloor() == nextFloor) {
                    // Only board if capacity allows
                    if (currentLoad + boarding.size() < CAPACITY) {
                        boarding.add(r);
                    }
                }
            }
            
            // Remove boarded passengers from waiting list
            toPickup.removeAll(boarding);
            onboard.addAll(boarding);

            // Add this stop to the route only if something happens
            if (!boarding.isEmpty() || !alighting.isEmpty()) {
                route.add(new Stop(nextFloor, boarding, alighting));
            }

            // Continue planning from the selected floor
            currentFloor = nextFloor;
        }

        // Persist direction for next invocation
        elevatorDirections.put(elevatorId, direction);

        return route;
    }

    /**
     * Convenience method that uses a default elevator ID.
     * Provided for compatibility but won't persist direction between calls.
     */
    public List<Stop> getSuboptimalStops(List<Request> requests, int startFloor) {
        return getSuboptimalStops(requests, startFloor, "default");
    }

    /**
     * Reset the stored direction for a specific elevator.
     * Useful when starting a fresh simulation.
     */
    public void resetDirection(String elevatorId) {
        elevatorDirections.remove(elevatorId);
    }

    /**
     * Reset all stored directions.
     * Should be called when starting a new simulation run.
     */
    public void resetAllDirections() {
        elevatorDirections.clear();
    }
}
