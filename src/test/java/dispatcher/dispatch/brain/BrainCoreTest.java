package dispatcher.dispatch.brain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import dispatcher.dispatch.eval.PlanEvaluation;
import dispatcher.dispatch.eval.Request;
import dispatcher.dispatch.eval.Status;
import dispatcher.dispatch.eval.Stop;

class BrainCoreTest {

    @Test
    void nextStopAndArrivedWalkOneRequestThroughItsRoute() {
        BrainCore brain = new BrainCore(40, 1);
        CarPlan plan = brain.getCarPlans().get("A");

        Request a = new Request("a", 3, 9, 60.0);
        plan.getRequests().add(a);
        plan.setRoute(new ArrayList<>(List.of(
                new Stop(3, List.of(a), List.of()),
                new Stop(9, List.of(), List.of(a)))));

        Stop first = brain.nextStop("A");
        assertEquals(3, first.getFloor());
        double next = brain.arrived("A", first, 65.8);
        assertEquals(70.9, next, 0.001);                       // 65.8 + dwell 5.1
        assertEquals(Status.BOARDED, a.getStatus());
        assertEquals(3, plan.getCar().getDestFloor());

        Stop second = brain.nextStop("A");
        assertEquals(9, second.getFloor());
        next = brain.arrived("A", second, 82.3);
        assertEquals(87.4, next, 0.001);
        assertEquals(Status.DELIVERED, a.getStatus());

        assertNull(brain.nextStop("A"));
        assertTrue(plan.getCar().isIdle());

        PlanEvaluation out = brain.result();
        assertEquals(5.8, out.getPickupWaits().get("a"), 0.001);
        assertEquals(22.3, out.getDeliveryWaits().get("a"), 0.001);
    }

    @Test
    void submitAssignsCarsAndWakesAnIdleCar() {
        BrainCore brain = new BrainCore(40, 2);
        brain.setFixedSearchSeconds(0.0);

        BrainCore.Ack first = brain.submit(3, 9, 60.0, null);
        assertEquals("r-00001", first.requestId());
        assertEquals("A", first.carId());
        assertEquals(60.0, first.wakeAt(), 0.001);

        BrainCore.Ack second = brain.submit(5, 7, 63.0, null);
        assertEquals("r-00002", second.requestId());
        assertEquals("B", second.carId());          // A is busy with 3 -> 9, B is empty
        assertEquals(63.0, second.wakeAt(), 0.001);
    }

    @Test
    void submitToABusyCarDoesNotWakeIt() {
        BrainCore brain = new BrainCore(40, 1);
        brain.setFixedSearchSeconds(0.0);

        brain.submit(3, 9, 60.0, null);
        BrainCore.Ack second = brain.submit(5, 7, 63.0, 70.0);

        assertEquals("A", second.carId());
        assertNull(second.wakeAt());
    }

}
