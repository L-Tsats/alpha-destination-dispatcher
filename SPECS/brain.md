# Brain (Central Controller) Specification

## Overview
The brain is the central controller. It receives requests, decides dispatch, tracks everything, and optimizes waiting time. Keypads and elevators are clients; the brain is the only server.

---

## Internal State

### Requests
Each request has:
- **requestId**: unique identifier (e.g., "r-00042")
- **fromFloor**: origin floor
- **destinationFloor**: destination floor
- **boardingCount**: how many people
- **time**: ISO-8601 timestamp of creation (the clock origin for wait/slack/deadline)
- **status**: WAITING → BOARDED → DELIVERED
- **boardedTo**: carId (which car it's boarded in; null while WAITING)

### Tree Search Input (RAW — stored state)
The tree search takes only the raw state:
- Current per-car state (destination, peopleCount, boardedIds, K)
- All WAITING requests (origin, dest, count, time)
- The precomputed floor-to-floor travel time table

### Tree Search Derived Metrics (COMPUTED per branch, not stored)
Everything below is a function of (raw inputs + the path taken so far). Recomputed at every node:
- **Running clock**: accumulated travel + dwell time along the branch so far.
- **Slack per request**: (planned pickup/delivery time) − (direct time with no detours). Spare time before this request suffers.
- **Deadline estimate**: each request's estimated total time (wait + transit) measured from its `time`.
- **Occupancy along the path**: peopleCount as it changes with each pickup/delivery.

### Tree Search Branching
At each step, consider ALL valid next stops:
- Any WAITING request's origin floor (pickup)
- Any BOARDED request's destination floor (delivery)
- Constraint: can't pick up more than (K - peopleCount)

### Validation (hard — kills the branch)
- Capacity exceeded (peopleCount + boarding > K) → invalid.
- A BOARDED party's delivery missing from the plan → invalid (lock invariant).

### Pruning (soft — drops weak branches)
- Negative slack (branch makes a request late) → prune.
- Deadline about to breach → force that request's promotion instead of exploring freely.

### Tree Branch Evaluation (scoring)
For each surviving branch, compute the average wait across all requests. Pick the branch with the lowest average wait.

### Next Destination Response (GET /api/elevators/{id}/next-destination)
The response sent to the elevator:
```json
{
  "elevator": "A",
  "nextFloor": 5,
  "boarding": [
    { "requestId": "r-00042", "destinationFloor": 17, "count": 2 }
  ]
}
```
- **elevator**: carId (e.g., "A")
- **nextFloor**: the floor to go to
- **boarding**: list of groups that board here (for the landing panel display)

### Request Lifecycle
- **WAITING**: requested, not yet picked up. Freely reassignable.
- **BOARDED**: picked up in a specific car, destination locked.
- **DELIVERED**: dropped at destination. Done.

The transition WAITING → BOARDED happens when a car PULLS its next destination (the pull confirms the stop is complete).

### Per-Car State
- **carId**: "A", "B", "C", ... (capital letter)
- **destination**: the floor the car is committed to go to (also the starting point for tree search)
- **isIdle**: true/false (everything else — moving/opening/closing — is the same to the tree)
- **peopleCount**: how many aboard (sum of boardingCount from boarded requests)
- **boardedIds**: list of requestIds that are BOARDED (the "debt" until delivery)
- **Capacity K**: max people (e.g., 8-16)

Sanity check: `sum(boardingCount of boardedIds) == peopleCount`

---

## The Lock Invariant (Correctness)
For every party currently BOARDED in a car, the car's queue MUST contain that party's destination stop. The brain may freely add/remove/reorder stops for WAITING parties only. This guarantees no boarded passenger is ever stranded.

---

## Dispatch Logic

### Ordering (One Elevator)
Given all WAITING requests, produce an ordered sequence of stops that:
- Never exceeds car capacity
- Picks up before delivering each party
- Minimizes average wait time

### Assignment (Many Elevators)
For each car, run the ordering algorithm. Pick the car with the best outcome (lowest average wait).

### Slack / Laxity
For each request, compute:
- Direct time: pickup → delivery with NO detours
- Planned time: under the current plan with detours
- SLACK = planned time - direct time

High slack → car can take detours. Low slack → serve directly.

### Deadline Override (Fairness)
If any request's estimated total time exceeds a deadline ceiling, it is PROMOTED to be served next. This prevents starvation.

### Receding-Horizon Replanning
On each event (new request, stop complete), re-compute the full plan from scratch. Execute only the first step. Throw away the rest and replan on the next event.

---

## Pull Model

Elevators NEVER receive commands. Instead:
1. Car is IDLE → calls GET /api/elevators/{id}/next-destination
2. Brain returns the next floor + who boards there
3. Car moves, stops, opens doors, people board/alight
4. Car pulls again → this act confirms the previous stop is complete

This is the "last safe moment" — the brain answers with the freshest plan right before the car moves.

---

## REST Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | /api/requests | Keypad submits request |
| GET | /api/elevators/{id}/next-destination | Elevator pulls next destination |
| POST | /api/elevators/{id}/status | Elevator reports arrival (optional) |

All defined in BRIEFING/02-contracts.md.

---

## Metrics Tracked

For each request:
- **Wait time**: request time → pickup time
- **Journey time**: request time → delivery time

Aggregates:
- Average wait (primary objective)
- Average journey
- 90th percentile wait (tail control)
- Max wait (worst case)

The simulation reads these from the brain after each scenario.

---

## Configuration (Startup)

The brain accepts at startup:
- Number of elevators (2-20)
- Number of floors (3-200)
- Per-car capacity K
- Max-wait ceiling (for deadline override)
- Brain's listen port