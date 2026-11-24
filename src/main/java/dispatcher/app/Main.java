
package dispatcher.app;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;

import dispatcher.api.BrainServer;
import dispatcher.devices.elevator.ElevatorController;
import dispatcher.devices.keypad.KeypadFeeder;
import dispatcher.sim.traffic.Passenger;
import dispatcher.dispatch.eval.PlanEvaluation;
import dispatcher.dispatch.eval.PlanScorer;

public class Main {
    // Takes in args [floors, elevators]
    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(new FileOutputStream("run-log.txt"), true));
        String url = "http://localhost:8080";
        int floors = args.length >= 2 ? Integer.parseInt(args[0]) : 20;
        int elevators = args.length >= 2 ? Integer.parseInt(args[1]) : 2;
        String mode = args.length >= 3 ? (args[2] == "MCTS" || args[2] == "LOOK" ? "MCTS" : "LOOK") : "MCTS";
        String scenario = args.length >= 4 && floors >= 10 ? (args[3] == "demo" || args[3] == "generated" ? "demo" : "generated") : "demo";

        List<Passenger> passengers = demoScenario();
        int lengthMinutes = 2;

        BrainServer server = new BrainServer(8080, floors, elevators);
        server.setDirectionalSearch(mode.equals("directional"));
        server.setEventLog(Path.of("events.jsonl"));
        server.start();
        System.out.println("Brain running on port 8080 (" + floors + " floors, " + elevators + " elevators, " + mode + " dispatcher, on " + scenario + " scenario)");

        // --- fire up the elevators ---
        for (int i = 0; i < elevators; i++) {
            String id = String.valueOf((char) ('A' + i));
            Thread t = new Thread(new ElevatorController(id, floors, url), "elevator-" + id);
            t.setDaemon(true);   // daemon: these threads don't keep the program alive at the end
            t.start();
        }

        // --- passengers press their keypads in real time  ---
        long start = System.nanoTime();
        new KeypadFeeder(floors, url, passengers).run();

        // --- wait until the scenario's time frame is over: no new requests after this ---
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        long leftMs = lengthMinutes * 60_000L - elapsedMs;
        if (leftMs > 0) Thread.sleep(leftMs);

        // --- then wait until every elevator has finished (safety limit: 5 more minutes) ---
        long giveUp = System.currentTimeMillis() + 5 * 60_000;
        while (!server.allIdle() && System.currentTimeMillis() < giveUp) {
            Thread.sleep(500);
        }

        // TODO 3: print the summary from server.results():
        //         delivered X of Y, mean and max wait, mean and max journey, and the mode
        PlanEvaluation r = server.results();
        System.out.println("Delivered: " + r.getDeliveryWaits().size() + " of " + passengers.size());
        for (String id : new TreeMap<>(r.getDeliveryWaits()).keySet()) {
            Passenger p = passengers.get(Integer.parseInt(id.substring(2)) - 1);   // "r-00003" -> 3 -> index 2
            System.out.printf("%s  %d -> %d  wait %.1f s  journey %.1f s%n",
                    id, p.getFromFloor(), p.getToFloor(),
                    r.getPickupWaits().get(id), r.getDeliveryWaits().get(id));
        }

        System.out.printf("%n%s | %d floors, %d elevators%n", mode, floors, elevators);
        System.out.printf("Wait:     mean %.1f s   max %.1f s%n", PlanScorer.pickupMean(r), PlanScorer.maxPickupWait(r));
        System.out.printf("Journey:  mean %.1f s   max %.1f s%n", PlanScorer.deliveryMean(r), PlanScorer.maxDeliveryWait(r));

        server.stop();
        System.exit(0);   // ends the program, including the daemon elevator threads
    }
    
    private static List<Passenger> demoScenario () {
        // Demo scenario: 8 passengers, 10 floors, all within the first minute of a 2-minute run.
        // Passenger(count, fromFloor, toFloor, time in MINUTES, weight)
        List<Passenger> passengers = List.of(
            new Passenger(1, 1, 7, 0.00, 80.0),
            new Passenger(1, 4, 9, 0.10, 80.0),
            new Passenger(1, 8, 2, 0.20, 80.0),
            new Passenger(1, 1, 5, 0.35, 80.0),   // floor 1 again: may share a stop with the first one
            new Passenger(1, 6, 1, 0.50, 80.0),
            new Passenger(1, 3, 10, 0.65, 80.0),
            new Passenger(1, 9, 4, 0.80, 80.0),
            new Passenger(1, 4, 8, 1.00, 80.0));  // floor 4 again
        return passengers;
    }
}
