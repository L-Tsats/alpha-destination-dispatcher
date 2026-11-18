package dispatcher.dispatch.brain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import dispatcher.dispatch.eval.PlanEvaluation;
import dispatcher.dispatch.eval.Status;
import dispatcher.dispatch.eval.Stop;

class BrainCoreTest {

    @Test
    void nextStopAndArrivedWalkOneRequestThroughItsRoute() {
        BrainCore brain = new BrainCore(40, 1);
        brain.setFixedSearchSeconds(0.0);
        brain.submit(3, 9, 60.0, null);                        // route becomes [3, 9]

        Stop first = brain.nextStop("A");
        assertEquals(3, first.getFloor());
        assertEquals(Status.WAITING, first.getBoarding().get(0).getStatus());
        double next = brain.arrived("A", first, 65.8);
        assertEquals(70.9, next, 0.001);                       // 65.8 + dwell 5.1
        assertEquals(Status.BOARDED, first.getBoarding().get(0).getStatus());
        assertEquals(3, brain.getCar("A").getDestFloor());

        Stop second = brain.nextStop("A");
        assertEquals(9, second.getFloor());
        next = brain.arrived("A", second, 82.3);
        assertEquals(87.4, next, 0.001);
        assertEquals(Status.DELIVERED, second.getAlighting().get(0).getStatus());

        assertNull(brain.nextStop("A"));
        assertTrue(brain.getCar("A").isIdle());

        PlanEvaluation out = brain.result();
        assertEquals(5.8, out.getPickupWaits().get("r-00001"), 0.001);
        assertEquals(22.3, out.getDeliveryWaits().get("r-00001"), 0.001);
    }

    @Test
    void submitAssignsCarsAndWakesAnIdleCar() {
        BrainCore brain = new BrainCore(40, 2);
        brain.setFixedSearchSeconds(0.0);

        BrainCore.Ack first = brain.submit(3, 9, 60.0, null);
        assertEquals("r-00001", first.requestId());
        assertEquals("A", first.elevatorId());
        assertEquals(60.0, first.wakeAt(), 0.001);

        BrainCore.Ack second = brain.submit(5, 7, 63.0, null);
        assertEquals("r-00002", second.requestId());
        assertEquals("B", second.elevatorId());          // A is busy with 3 -> 9, B is empty
        assertEquals(63.0, second.wakeAt(), 0.001);
    }

    @Test
    void submitToABusyCarDoesNotWakeIt() {
        BrainCore brain = new BrainCore(40, 1);
        brain.setFixedSearchSeconds(0.0);

        brain.submit(3, 9, 60.0, null);
        BrainCore.Ack second = brain.submit(5, 7, 63.0, 70.0);

        assertEquals("A", second.elevatorId());
        assertNull(second.wakeAt());
    }

    @Test
    void nextDestinationDrivesOneElevatorThroughItsRoute() {
        BrainCore brain = new BrainCore(40, 1);
        brain.setFixedSearchSeconds(0.0);
        brain.submit(3, 9, 60.0, null);

        assertEquals(Integer.valueOf(3), brain.nextDestination("A", 60.0));
        assertEquals(70.9, brain.getCar("A").getNextDecisionTime(), 0.001);   // 60 + 5.8 travel + 5.1 dwell

        assertEquals(Integer.valueOf(9), brain.nextDestination("A", 70.9));
        assertEquals(87.4, brain.getCar("A").getNextDecisionTime(), 0.001);   // 70.9 + 11.4 travel + 5.1 dwell

        assertNull(brain.nextDestination("A", 87.4));
        assertTrue(brain.getCar("A").isIdle());

        assertEquals(5.8, brain.result().getPickupWaits().get("r-00001"), 0.001);
        assertEquals(22.3, brain.result().getDeliveryWaits().get("r-00001"), 0.001);
    }

    @Test
    void hasElevatorKnowsOnlyTheBuiltCars() {
        BrainCore brain = new BrainCore(40, 2);
        assertTrue(brain.hasElevator("A"));
        assertTrue(brain.hasElevator("B"));
        assertFalse(brain.hasElevator("C"));
    }

}
