package dispatcher.dispatch.eval;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class EvaluatorTest {

    @Test
    public void floor17Example() {
        Request r1 = new Request("r1", 20, 15, 0.0);
        Request r2 = new Request("r2", 15, 2, 10.7);
        Request r3 = new Request("r3", 3, 9, 15.2);

        List<Stop> plan = List.of(
            new Stop(20, List.of(r1), List.of()),
            new Stop(15, List.of(r2), List.of(r1)),
            new Stop(3,  List.of(r3), List.of()),
            new Stop(2,  List.of(),   List.of(r2)),
            new Stop(9,  List.of(),   List.of(r3))
        );

        Evaluator ev = new Evaluator(40);
        PlanEvaluation result = ev.evaluate(plan, 17, 20.3);

        // expected values (hand-verified)
        assertEquals(27.5,  result.getPickupWaits().get("r1"), 0.001);
        assertEquals(28.8,  result.getPickupWaits().get("r2"), 0.001);
        assertEquals(46.1,  result.getPickupWaits().get("r3"), 0.001);
        assertEquals(39.5,  result.getDeliveryWaits().get("r1"), 0.001);
        assertEquals(56.84, result.getDeliveryWaits().get("r2"), 0.01);
        assertEquals(67.14, result.getDeliveryWaits().get("r3"), 0.01);
    }
}
