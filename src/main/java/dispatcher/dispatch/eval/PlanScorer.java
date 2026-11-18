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
}
