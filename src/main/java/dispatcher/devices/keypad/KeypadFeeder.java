package dispatcher.devices.keypad;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dispatcher.sim.traffic.Passenger;
import dispatcher.timing.RunClock;

// Plays passengers in REAL time: each one presses the keypad on their floor at their time
public class KeypadFeeder {
    private final Map<Integer, Keypad> keypads = new HashMap<>();   // floor -> its keypad
    private final List<Passenger> passengers;                     // sorted by time

    public KeypadFeeder(int nFloors, String brainUrl, List<Passenger> passengers) {
        for (int floor = 1; floor <= nFloors; floor++) {
            keypads.put(floor, new Keypad(floor, nFloors, brainUrl));
        }
        this.passengers = passengers;
    }

    public void run() throws InterruptedException {
        long start = System.nanoTime();
        for (Passenger p : passengers) {
            // TODO 1: work out how many milliseconds after `start` this passenger arrives
            //         (p.getTime() is in minutes)
            long passengerArrivalTime = (long) (p.getTime() * 60_000L);  // convert minutes to milliseconds
            // TODO 2: sleep until then (if that moment has already passed, don't sleep)
            long elapsedTime = (System.nanoTime() - start) / 1_000_000L; // convert nanoseconds to milliseconds
            long sleepTime = passengerArrivalTime - elapsedTime;

            if (sleepTime > 0) {
                Thread.sleep(sleepTime);
            }

            // TODO 3: press the keypad on the passenger's floor with their destination
            Keypad keypad = keypads.get(p.getFromFloor());
            keypad.press(p.getToFloor());
            // TODO 4: print the floor, the destination and what the screen shows
            System.out.println(RunClock.stamp() + " Floor " + p.getFromFloor() + " -> " + p.getToFloor() + ": " + keypad.getScreen());
        }
    }


    public static void main(String[] args) throws InterruptedException {
        List<Passenger> ps = List.of(
            new Passenger(1, 2, 6, 0.0, 80.0),   // at 0 s
            new Passenger(1, 5, 1, 0.1, 80.0),   // at 6 s
            new Passenger(1, 3, 9, 0.2, 80.0));  // at 12 s
        new KeypadFeeder(10, "http://localhost:8080", ps).run();
    }

}