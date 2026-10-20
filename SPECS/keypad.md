# Keypad UI Logic Specification

## Overview
The keypad captures user floor requests and sends them to the brain. It is a stateful UI with discrete screens.

## Keypad Payload (POST /api/requests)
The keypad sends this JSON to the brain:
```json
{
  "fromFloor": 3,
  "destinationFloor": 17,
  "boardingCount": 2
}
```
- **fromFloor**: The floor where this keypad is installed (hardcoded at startup).
- **destinationFloor**: The floor the user wants to go to.
- **boardingCount**: How many people are making this request.

## Brain Response
The brain replies with `{ "status": "received", "requestId": "r-00042" }`.
- **status**: "received" = accepted, "rejected" = denied.
- **requestId**: present only when accepted (tracks the request for metrics).

# DECISION TO BE MADE:
Do we ask for the extra **boardingCount** as well in the user input, 
or *NOT*, and figure out another way to compromise?

---

## UI Screens & Flow

### Screen 1: Floor Input (Initial State)
- Numeric keypad (0-9), backspace, and "Next" button.
- User types the destination floor.
- "Next" proceeds to Screen 2 if input is valid (1-200, ≠ fromFloor).

### Screen 2: People Count
- Numeric keypad (0-9), backspace, "Confirm", and "Cancel" button.
- "Cancel" aborts the request and returns to Screen 1.
- "Confirm" sends the request and moves to Screen 3.

### Screen 3: Awaiting Verification
- Displays "Awaiting verification..." while the HTTP POST is in flight.
- No user input accepted.

### Screen 4: Request Accepted
- Displays "Request accepted!" briefly (2 seconds).
- Auto-returns to Screen 1.

### Screen 5: Request Rejected
- Displays the error message from the brain.
- Auto-returns to Screen 1 after 3 seconds.

---

## Error Handling (Future Expansion)

### Network Error
- Show "Connection error. Try again." and return to Screen 1.

### Rate Limiting
- Add 500ms sleep between consecutive requests.

### Maintenance Mode
- Display "Elevator out of service" with the nearest functional floor.