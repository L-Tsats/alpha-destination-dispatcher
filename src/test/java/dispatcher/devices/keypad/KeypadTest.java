package dispatcher.devices.keypad;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dispatcher.api.BrainServer;

class KeypadTest {

    private BrainServer server;
    private Keypad keypad;   // on floor 2 of a 10-floor building

    @BeforeEach
    void startBrain() throws Exception {
        server = new BrainServer(0, 10, 2);   // port 0: any free port
        server.start();
        keypad = new Keypad(2, 10, "http://localhost:" + server.port());
    }

    @AfterEach
    void stopBrain() {
        server.stop();
    }

    @Test
    void floorsOutsideTheBuildingAreRejected() {
        keypad.press(0);
        assertEquals("Invalid destination: Out of bounds", keypad.getScreen());
        keypad.press(11);
        assertEquals("Invalid destination: Out of bounds", keypad.getScreen());
    }

    @Test
    void theKeypadsOwnFloorIsRejected() {
        keypad.press(2);
        assertEquals("Invalid destination: Same floor as current", keypad.getScreen());
    }

    @Test
    void aValidFloorShowsTheAssignedElevator() {
        keypad.press(6);
        assertEquals("Submitted! Await elevator: A", keypad.getScreen());
    }

    @Test
    void anUnreachableBrainShowsAMessageInsteadOfCrashing() throws Exception {
        // start and stop a second server just to get a port that nobody listens on
        BrainServer gone = new BrainServer(0, 10, 2);
        gone.start();
        int deadPort = gone.port();
        gone.stop();

        Keypad offline = new Keypad(2, 10, "http://localhost:" + deadPort);
        offline.press(6);
        assertEquals("Brain unavailable", offline.getScreen());
    }
}
