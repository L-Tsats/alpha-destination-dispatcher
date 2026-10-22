
package dispatcher.sim;

import dispatcher.sim.elevator.Elevator;
import dispatcher.sim.http.ElevatorHttpServer;

public class Main {

    public static void main(String[] args) throws Exception {

        Elevator e1 = new Elevator(1, 5);

        ElevatorHttpServer httpServer = new ElevatorHttpServer(e1);

        httpServer.start();
    }
}
