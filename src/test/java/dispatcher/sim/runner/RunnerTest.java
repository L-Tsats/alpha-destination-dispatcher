package dispatcher.sim.runner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import dispatcher.dispatch.eval.PlanEvaluation;
import dispatcher.sim.traffic.Passenger;

public class RunnerTest {

    @Test
    public void threeHandBuiltRequestsPrintGrowingPlans() {
        // Passenger(count, fromFloor, toFloor, time in MINUTES, weight)
        List<Passenger> passengers = List.of(
            new Passenger(1, 3, 9, 0.5, 80.0),
            new Passenger(1, 5, 7, 1.0, 80.0),
            new Passenger(1, 2, 4, 1.5, 80.0)
        );

        new Runner(40).run(passengers);
    }

    @Test
    public void singlePassengerWaitAndJourney() {
        // minute 1.0 = 60 s, floors 3 -> 9, car starts at floor 1
        List<Passenger> passengers = List.of(new Passenger(1, 3, 9, 1.0, 80.0));

        PlanEvaluation result = new Runner(40).run(passengers);

        // arrival at 3: 60 + 5.8 = 65.8 -> wait 5.8
        // dwell 5.1 -> leaves at 70.9, +11.4 travel -> arrives at 9 at 82.3 -> journey 22.3
        assertEquals(5.8, result.getPickupWaits().get("r-00001"), 0.001);
        assertEquals(22.3, result.getDeliveryWaits().get("r-00001"), 0.001);
    }

    @Test
    public void carRestartsAfterGoingIdle() {
        // first passenger is finished long before the second arrives, so the car idles in between
        List<Passenger> passengers = List.of(
            new Passenger(1, 3, 9, 1.0, 80.0),
            new Passenger(1, 9, 3, 5.0, 80.0)   // 300 s, car is waiting at floor 9
        );

        PlanEvaluation result = new Runner(40).run(passengers);

        // second passenger: same floor as the car, gap 0 -> wait 0
        // dwell 5.1, then gap 6 = 11.4 s -> arrives at 300 + 5.1 + 11.4 = 316.5 -> journey 16.5
        assertEquals(0.0, result.getPickupWaits().get("r-00002"), 0.001);
        assertEquals(16.5, result.getDeliveryWaits().get("r-00002"), 0.001);
    }

}

