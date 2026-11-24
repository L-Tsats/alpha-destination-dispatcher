package dispatcher.dispatch.eval;

import java.util.Map;

public class PlanScorer {
    public static double deliveryMean(PlanEvaluation plan) {
        double sum = 0;
        int count = 0;
        for (Map.Entry<String, Double> wait : plan.getDeliveryWaits().entrySet()) {
            sum += wait.getValue();
            count ++;
        }
        
        if (count == 0) return 0;
        return sum / count;
    }

    public static double deliveryTotal(PlanEvaluation plan) {
        double sum = 0;
        for (Map.Entry<String, Double> wait : plan.getDeliveryWaits().entrySet()) {
            sum += wait.getValue();
        }
        
        return sum;
    }

    public static double pickupMean(PlanEvaluation plan) {
        double sum = 0;
        int count = 0;
        for (Map.Entry<String, Double> wait : plan.getPickupWaits().entrySet()) {
            sum += wait.getValue();
            count ++;
        }
        
        if (count == 0) return 0;
        return sum / count;
    }

    public static double pickupTotal(PlanEvaluation plan) {
        double sum = 0;
        for (Map.Entry<String, Double> wait : plan.getPickupWaits().entrySet()) {
            sum += wait.getValue();
        }
        
        return sum;
    }

    public static double maxPickupWait(PlanEvaluation plan) {
        double max = 0;
        for (Map.Entry<String, Double> wait : plan.getPickupWaits().entrySet()) {
            max = max < wait.getValue() ? wait.getValue() : max;
        }
        return max;
    }

    public static double maxDeliveryWait(PlanEvaluation plan) {
        double max = 0;
        for (Map.Entry<String, Double> wait : plan.getDeliveryWaits().entrySet()) {
            max = max < wait.getValue() ? wait.getValue() : max;
        }
        return max;
    }

}
