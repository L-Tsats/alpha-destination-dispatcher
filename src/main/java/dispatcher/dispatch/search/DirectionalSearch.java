
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DirectionalSearch {

    public List<Stop> getSuboptimalStops(List<Request> requests, int startFloor) {

        List<Stop> route = new ArrayList<>();

        List<Request> toPickup = new ArrayList<>();
        List<Request> onboard = new ArrayList<>();

        // Split requests by status
        for (Request r : requests) {
            if (r.getStatus() == Status.WAITING) {
                toPickup.add(r);
            } else if (r.getStatus() == Status.BOARDED) {
                onboard.add(r);
            }
        }

        int currentFloor = startFloor;
        int direction = 1; // Going UP

        // Continue until all passengers are delivered
        while (!toPickup.isEmpty() || !onboard.isEmpty()) {

            ArrayList<Integer> floorsAbove = new ArrayList<>();
            ArrayList<Integer> floorsBelow = new ArrayList<>();

            // Floors where passengers must board
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

            // Floors where passengers must leave
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

            // Serve current floor first
            if (!floorsAbove.isEmpty() && floorsAbove.get(0) == currentFloor) {
                nextFloor = floorsAbove.remove(0);

            } else if (direction == 1 && !floorsAbove.isEmpty()) {
                nextFloor = floorsAbove.remove(0);

            } else if (direction == -1 && !floorsBelow.isEmpty()) {
                nextFloor = floorsBelow.remove(0);

            } else if (!floorsAbove.isEmpty()) {
                direction = 1;
                nextFloor = floorsAbove.remove(0);

            } else if (!floorsBelow.isEmpty()) {
                direction = -1;
                nextFloor = floorsBelow.remove(0);

            } else {
                break;
            }

            List<Request> boarding = new ArrayList<>();
            List<Request> alighting = new ArrayList<>();

            // Passengers leaving at this floor
            for (Request r : onboard) {
                if (r.getToFloor() == nextFloor) {
                    alighting.add(r);
                }
            }
            onboard.removeAll(alighting);

            // Passengers boarding at this floor
            for (Request r : toPickup) {
                if (r.getFromFloor() == nextFloor) {
                    boarding.add(r);
                }
            }
            toPickup.removeAll(boarding);
            onboard.addAll(boarding);

            // Add this stop to the route
            route.add(new Stop(nextFloor, boarding, alighting));

            // Continue planning from the selected floor
            currentFloor = nextFloor;
        }

        return route;
    }
}
