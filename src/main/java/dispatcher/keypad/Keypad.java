package dispatcher.keypad;

public class Keypad {
    private int currentFloor;
    private KeypadState currentState;
    private BrainController brain;

    public Keypad(int currentFloor, BrainController brain) {
        this.currentFloor = currentFloor;
        this.brain = brain;
        this.currentState = KeypadState.INPUTFLOOR;
    }

    public void start() {
        while (true) {
            switch (currentState) {
                case INPUTFLOOR:
                    // Code to handle input floor state
                    break;
                case AWAITING:
                    // Code to handle awaiting state
                    break;
                case ACCEPTED:
                    // Code to handle accepted state
                    break;
                case REJECTED:
                    // Code to handle rejected state
                    break;
                case MAINTENANCE:
                    // Code to handle maintenance state
                    break;
            }
        }
    }

    private void showInputFloor() {
        // Code to show input floor state
    }

    private void showAwaiting() {
        // Code to show awaiting state
    }

    private void showAccepted() {
        // Code to show accepted state
    }

    private void showRejected() {
        // Code to show rejected state
    }

    private void showMaintenance() {
        // Code to show maintenance state
    }

}
