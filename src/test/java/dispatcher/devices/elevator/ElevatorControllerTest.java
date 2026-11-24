package dispatcher.devices.elevator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import dispatcher.api.BrainApiClient;
import dispatcher.api.BrainServer;
import dispatcher.api.dto.PassengerRequest;

// End-to-end over real HTTP, in real time (takes about 15 s)
class ElevatorControllerTest {

    @Test
    void elevatorServesARequestFromItsOwnFloorAndArrives() throws Exception {
        BrainServer server = new BrainServer(0, 10, 1);
        server.setPollTimeoutMs(500);   // short long-poll, so idle waits are quick
        server.start();
        String url = "http://localhost:" + server.port();

        ElevatorController elevator = new ElevatorController("A", 10, url);
        Thread thread = new Thread(elevator);
        thread.setDaemon(true);
        thread.start();
        try {
            // nothing to do yet: the elevator keeps asking (204s) without crashing
            Thread.sleep(1500);
            assertTrue(thread.isAlive());
            assertEquals(1, elevator.getCurrentFloor());

            // a pickup at the elevator's OWN floor (1): doors must open there, then it travels to 4
            assertEquals("A", new BrainApiClient(url).submitRequest(new PassengerRequest(1, 4)).elevatorID());

            // expected: doors at 1 (8 s), then 3 floors of travel (7.2 s), so about 15.2 s
            long deadline = System.currentTimeMillis() + 30_000;
            while (elevator.getCurrentFloor() != 4 && System.currentTimeMillis() < deadline) {
                Thread.sleep(200);
            }
            assertEquals(4, elevator.getCurrentFloor());
            assertTrue(thread.isAlive());
        } finally {
            thread.interrupt();
            server.stop();
        }
    }
}
