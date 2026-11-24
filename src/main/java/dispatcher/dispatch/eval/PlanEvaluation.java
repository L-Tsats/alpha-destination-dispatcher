package dispatcher.dispatch.eval;

import java.util.Map;

public class PlanEvaluation {
    private final Map<String, Double> pickupWaits;
    private final Map<String, Double> deliveryWaits;

    public PlanEvaluation(Map<String, Double> pickupWaits, Map<String, Double> deliveryWaits) {
        this.pickupWaits = pickupWaits;
        this.deliveryWaits = deliveryWaits;

    }

    public Map<String, Double> getDeliveryWaits() {
        return deliveryWaits;
    }

    public Map<String, Double> getPickupWaits() {
        return pickupWaits;
    }
    
    
}
