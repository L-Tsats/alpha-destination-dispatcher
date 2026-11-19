package dispatcher.devices.keypad;
import java.io.IOException;

import dispatcher.api.BrainApiClient;
import dispatcher.api.dto.AssignmentResponse;
import dispatcher.api.dto.PassengerRequest;

public class Keypad {
    private final int keypadFloor;   // the floor this keypad is mounted on
    private final int nFloors;        // must match the brain's floor count
    private final BrainApiClient client;
    private String screen;

    // Everything the keypad needs is given here: it never guesses its floor, the building size or the brain's address
    public Keypad(int keypadFloor, int nFloors, String brainUrl) {
        this.keypadFloor = keypadFloor;
        this.nFloors = nFloors;
        this.client = new BrainApiClient(brainUrl);
    }

    public void press(int destFloor) {
        FloorCheckResult check = checkFloor(destFloor);
        switch(check) {
            // Later TODOs: Add to the below messages (NOT replace, because
            // existing tests use them) with actual GUI screen results
            case OUT_OF_BOUNDS -> {
                setScreen("Invalid destination: Out of bounds");
            }
            case SAME_FLOOR -> {
                setScreen("Invalid destination: Same floor as current");
            }
            case ACCEPTED -> {
                PassengerRequest request = new PassengerRequest(keypadFloor, destFloor);
                AssignmentResponse response;
                try {
                    response = client.submitRequest(request);
                    setScreen("Submitted! Await elevator: " + response.elevatorID());
                } catch (IOException e) {
                    setScreen("Brain unavailable");
                } catch (InterruptedException e) {
                    setScreen("Brain unavailable");
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private FloorCheckResult checkFloor(int destFloor) {
        FloorCheckResult result;
        if(destFloor < 1 || destFloor > nFloors) {
            result = FloorCheckResult.OUT_OF_BOUNDS;
        } else if(destFloor == keypadFloor) {
            result = FloorCheckResult.SAME_FLOOR;
        } else {
            result = FloorCheckResult.ACCEPTED;
        }
        return result;
    }

    private void setScreen(String message) {
        screen = message;
    }

    public String getScreen() {
        return screen;
    }

}
