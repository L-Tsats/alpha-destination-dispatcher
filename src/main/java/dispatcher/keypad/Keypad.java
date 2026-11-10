package dispatcher.keypad;
import java.util.Scanner;

import dispatcher.sim.http.AssignmentResponse;
import dispatcher.sim.http.BrainApiClient;
import dispatcher.sim.http.PassengerRequest;

public class Keypad {
    private int currentFloor;
    private KeypadState currentState;
    private final BrainApiClient client = new BrainApiClient("http://localhost:8080");

    public Keypad(int currentFloor) {
        this.currentFloor = currentFloor;
        this.currentState = KeypadState.INPUTFLOOR;
    }

    public void start() throws InterruptedException {
        PassengerRequest request = null;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            switch (currentState) {
                case INPUTFLOOR:
                    System.out.print("Enter destination floor: ");
                    int destinationFloor = scanner.nextInt();
                    if (destinationFloor < 1 || destinationFloor > 10) {
                        System.out.println("Invalid floor. Please enter a floor between 1 and 10.");
                        continue;
                    }
                    if (destinationFloor == currentFloor) {
                        System.out.println("You are already on this floor. Please enter a different floor.");
                        continue;
                    }
                    request = new PassengerRequest(currentFloor, destinationFloor);
                    currentState = KeypadState.AWAITING;
                    break;
                case AWAITING:
                    System.out.println("Request sent. Awaiting response...");
                    try {
                        AssignmentResponse response = client.submitRequest(request);
                        if (response != null) {
                            System.out.println("Elevator assigned: " + response.elevatorID());
                            currentState = KeypadState.ACCEPTED;
                        } else {
                            System.out.println("Request rejected. Please try again.");
                            currentState = KeypadState.REJECTED;
                        }
                    } catch (Exception e) {
                        System.out.println("Error communicating with the server: " + e.getMessage());
                        currentState = KeypadState.MAINTENANCE;
                    }
                    break;
                case ACCEPTED:
                    System.out.println("Request accepted. Elevator is on its way.");
                    Thread.sleep(2000); // Simulate waiting for the elevator
                    currentState = KeypadState.INPUTFLOOR; // Reset to input floor after acceptance
                    break;
                case REJECTED:
                    // Code to handle rejected state
                    break;
                case MAINTENANCE:
                    // Code to handle maintenance state
                    break;
            }
            
            scanner.close();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Δημιουργούμε ένα πληκτρολόγιο που βρίσκεται, για παράδειγμα, στον 3ο όροφο
        Keypad keypad = new Keypad(3); 
        
        // Ξεκινάμε τη μηχανή καταστάσεων (τη while λούπα σου)
        keypad.start();
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
