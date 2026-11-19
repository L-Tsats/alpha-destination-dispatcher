package dispatcher.dispatch.eval;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dispatcher.timing.Dwell;
import dispatcher.timing.TravelModel;

public class Evaluator {
    private final double[] travelTable;

    public Evaluator(int nFloors) {
        TravelModel model = new TravelModel();
        this.travelTable = model.buildTravelTable(nFloors);
    }

    public PlanEvaluation evaluate(List<Stop> stops, int startFloor, double currentTime) {
        double clock = currentTime;
        int currentFloor = startFloor;
        Map<String, Double> pickupWaits = new HashMap<>();
        Map<String, Double> deliveryWaits = new HashMap<>();

        for(Stop stop : stops) {
            clock += travelTable[Math.abs(stop.getFloor() - currentFloor)];
            currentFloor = stop.getFloor();

            for(Request boardingRequest : stop.getBoarding()) {
                pickupWaits.put(boardingRequest.getRequestId(), clock - boardingRequest.getArrivalTime());
            }

            for(Request deliveryRequest : stop.getAlighting()) {
                deliveryWaits.put(deliveryRequest.getRequestId(), (clock - deliveryRequest.getArrivalTime()));
            }

            clock += Dwell.SECONDS;
        }

        return new PlanEvaluation(pickupWaits, deliveryWaits);
    }
    
}
