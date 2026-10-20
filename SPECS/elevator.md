# Elevator Car Controller Specification

## Overview
The elevator car is a mechanical client that pulls its next destination from the brain, moves to it, operates doors, and updates its display panels. It owns door timing locally — the brain never controls doors directly.

## Timing Constants (from research)
- **v**: 2.5 m/s (rated speed)
- **a**: 1.0 m/s² (acceleration)
- **h**: 3.5 m (floor height)
- **t_door**: 4 s (open + close as one action)
- **t_pax**: 1.2 s/person (transfer time per person)
- **t_start**: 0.5 s (motor startup delay)
- **Dwell ceiling**: t_door + max(boarding, alighting) * t_pax

## States

### IDLE
- Car stationary, doors closed.
- Pulls next destination from brain (GET /api/elevators/{id}/next-destination).
- 204 → stay idle. 200 → proceed to destination.

### MOVING
- Car travels to destination floor.
- In-car display shows target floor.
- On arrival → open doors.

### DOORS_OPENING → DOORS_OPEN → DOORS_CLOSING
- Doors open (part of the 4s door cycle).
- People board and alight during dwell (derived from brain's boarding[] data).
- Doors close (remaining part of 4s cycle).
- This is ONE atomic action (open + close together).

### Door Safety
- Doors cannot open or close while car is moving.
- If obstruction detected during close → reopen briefly and retry.
- If closing on passenger (pressure sensor) → immediate reopen + one retry.
- After repeated failures → report error to brain, display "Maintenance Required".
- These error cases are rare (passengers warned in advance via panels) → NOT included in dwell ceiling.

## Display Panels

### In-Car Display
- Shows the NEXT floor the car will stop at.
- Riders watch this to know when to exit.
- Updates when entering MOVING state.

### Landing Panel (at each floor)
- Shows who boards at that floor: "X people for floor Y".
- Data comes from the `boarding[]` array in brain's response.
- Displayed before arrival so passengers can prepare.

## Brain Communication

### Pull Next Destination
- Endpoint: GET /api/elevators/{id}/next-destination
- Called from IDLE state.
- Response: `{ floor: 5, boarding: [{ requestId, destinationFloor, count }] }`
- 204 = nothing to do, stay idle.

### Report Status (Optional)
- Endpoint: POST /api/elevators/{id}/status
- Called on arrival (doors opening).
- Payload: elevator, currentFloor, direction, doorState, event (ARRIVED), arrivalTime.
- Telemetry only; the pull confirms the previous stop is complete.