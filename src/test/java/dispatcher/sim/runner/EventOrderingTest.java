package dispatcher.sim.runner;

import java.util.PriorityQueue;

import org.junit.jupiter.api.Test;

import dispatcher.sim.traffic.Passenger;

public class EventOrderingTest {

    @Test
    public void pollsEventsInTimeOrderRegardlessOfInsertOrder() {
        PriorityQueue<Event> queue = new PriorityQueue<>();

        // add events DELIBERATELY out of order (times: 9.0, 2.0, 5.0, 1.0, 7.0)
        queue.add(new Event(9.0, dummy(), EventType.KEYPAD_REQUEST));
        queue.add(new Event(2.0, dummy(), EventType.KEYPAD_REQUEST));
        queue.add(new Event(5.0, dummy(), EventType.KEYPAD_REQUEST));
        queue.add(new Event(1.0, dummy(), EventType.KEYPAD_REQUEST));
        queue.add(new Event(7.0, dummy(), EventType.KEYPAD_REQUEST));

        // poll them out — must come in ASCENDING time order
        double previous = Double.NEGATIVE_INFINITY;
        while (!queue.isEmpty()) {
            Event e = queue.poll();
            org.junit.jupiter.api.Assertions.assertTrue(
                e.getTime() >= previous,
                "events came out of order: " + e.getTime() + " after " + previous);
            previous = e.getTime();
        }
    }

    // a throwaway passenger — floors/weight irrelevant, we only test ordering by time
    private Passenger dummy() {
        return new Passenger(1, 1, 2, 0.0, 0.0);
    }
}
