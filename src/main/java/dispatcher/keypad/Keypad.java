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
                    try {
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
                       
                    } catch (java.util.InputMismatchException e) {
                        System.out.println("Invalid input. Please enter a valid floor number.");
                        scanner.nextLine(); // Clear the invalid input
                    }
                    break;
                case AWAITING:
                    System.out.println("Request sent. Awaiting response...");
                    try {
                        AssignmentResponse response = client.submitRequest(request);
                        if (response != null) {
                            System.out.println("Elevator assigned: " + response.elevatorID());
                            currentState = KeypadState.ACCEPTED;
                        } else {
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
                    System.out.println("Request rejected. Please try again.");
                    Thread.sleep(3000); // Simulate waiting before allowing another input
                    currentState = KeypadState.INPUTFLOOR; // Reset to input floor after rejection
                    break;
                case MAINTENANCE:
                    System.out.println("System is under maintenance. Please try again later.");
                    Thread.sleep(5000); // Simulate maintenance duration
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


}
