import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DirectionalSearch{
    private int direction = 1; //Going UP
    List<Request> toPickup = new ArrayList<>();
    List<Request> onboard = new ArrayList<>();

    public Stop getSuboptimalStop(List<Request> requests, int startFloor){

        ArrayList<Integer> floorsAbove = new ArrayList<>();
        ArrayList<Integer> floorsBelow = new ArrayList<>();  

        ArrayList<Request> boarding = new ArrayList<>();
        ArrayList<Request> alighting = new ArrayList<>();

        for (Request r : requests) {

            int targetFloor;
            if (r.getStatus() == Status.WAITING) { //The ones that are meant to go in
                targetFloor = r.getFromFloor();
            } else if (r.getStatus() == Status.BOARDED) {// The ones that are meant to leave
                targetFloor = r.getToFloor();
            }
            else{
                continue;
            }
            if(targetFloor >= startFloor){
                if (!floorsAbove.contains(targetFloor)) {
                    floorsAbove.add(targetFloor);
                }
            } else if (targetFloor < startFloor){
                if (!floorsBelow.contains(targetFloor)) {
                    floorsBelow.add(targetFloor);
                }
            }

        }
        floorsAbove.sort(Comparator.naturalOrder());
        floorsBelow.sort(Comparator.reverseOrder());        
        int nextFloor;
        if (floorsAbove.get(0) == startFloor){
            nextFloor = floorsAbove.remove(0);
        }else if (direction == 1 && !floorsAbove.isEmpty()) {
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
            return null;
        }

        for (Request r : requests) {
            if (r.getStatus() == Status.WAITING
                    && r.getFromFloor() == nextFloor) {
                boarding.add(r);
            }

            if (r.getStatus() == Status.BOARDED
                    && r.getToFloor() == nextFloor) {
                alighting.add(r);
            }
        }

        return new Stop(nextFloor, boarding, alighting);


    }
}