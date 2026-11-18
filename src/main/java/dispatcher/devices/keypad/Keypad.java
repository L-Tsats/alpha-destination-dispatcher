package dispatcher.devices.keypad;
import java.util.Scanner;

import dispatcher.api.BrainApiClient;
import dispatcher.api.dto.AssignmentResponse;
import dispatcher.api.dto.PassengerRequest;

public class Keypad {
    private final int currentFloor;   // the floor this keypad is mounted on
    private final int nFloors;        // must match the brain's floor count
    private final BrainApiClient client;
    private KeypadState currentState;

    // Everything the keypad needs is given here: it never guesses its floor, the building size or the brain's address
    public Keypad(int currentFloor, int nFloors, String brainUrl) {
        this.currentFloor = currentFloor;
        this.nFloors = nFloors;
        this.client = new BrainApiClient(brainUrl);
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
                        if (destinationFloor < 1 || destinationFloor > nFloors) {
                            System.out.println("Invalid floor. Please enter a floor between 1 and " + nFloors + ".");
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
        }
    }

    // Launcher for one keypad. Arguments: <keypad floor> <number of floors> [brain URL]
    // e.g. "3 40" or "3 40 http://localhost:8080"
    public static void main(String[] args) throws InterruptedException {
        int floor   = args.length >= 1 ? Integer.parseInt(args[0]) : 2;
        int nFloors = args.length >= 2 ? Integer.parseInt(args[1]) : 0;
        String url  = args.length >= 3 ? args[2] : "http://localhost:8080";

        System.out.println("Keypad on floor " + floor + " (" + nFloors + " floors), brain at " + url);
        new Keypad(floor, nFloors, url).start();
    }


}
