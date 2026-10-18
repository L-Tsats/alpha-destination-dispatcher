# REST Message Contracts (the standardization deliverable)

> Payload format: **JSON** (the REST norm; light; Gson-friendly in Java).
> Each contract defines exactly WHAT the "right stuff" is for a message:
> every field, its type, whether it's required, and its rules, PLUS the REST
> envelope (method, path, response). This is what every teammate builds against
> so modules wire together cleanly.
>
> Convention: floors are integers 1..200; counts are non-negative integers;
> ids are strings; times are ISO-8601 timestamps (e.g. "2027-01-13T09:30:00Z").

## Topology — ONLY THE BRAIN IS AN HTTP SERVER
Keypads and elevators are CLIENTS; the central brain is the only SERVER. Flow:
- Keypad -> brain:   POST a request (keypad calls the brain).
- Elevator -> brain: PULLS its next destination (GET), and POSTs status reports.
  The brain does NOT push orders; the elevator asks when it's ready to move
  (this enforces no-stall + deferred-commitment: the car asks at the last safe
  moment and gets the brain's freshest answer).
- The brain's base URL is a CONFIG PARAMETER given to each module at startup
  (matches the "configurable parameters" / command-line-args requirement). No
  service discovery needed.

Endpoint summary:
| Msg | Caller            | Method | Path (on the brain)                      | Success |
|-----|-------------------|--------|------------------------------------------|---------|
| 1   | keypad  -> brain  | POST   | /api/requests                            | 201     |
| 2   | elevator-> brain  | GET    | /api/elevators/{id}/next-destination     | 200     |
| 3   | elevator-> brain  | POST   | /api/elevators/{id}/status               | 200     |

---

## MESSAGE 1 — Request   (Keypad -> Brain)   [LOCKED]

**Endpoint:** `POST /api/requests`  (keypad calls the brain).
**Responses:** `201 Created` + the ack body on success; `400 Bad Request` if the
body is malformed or destinationFloor == fromFloor (rejected).

Request body:
```json
{
  "fromFloor": 3,
  "destinationFloor": 17,
  "boardingCount": 2
}
```
| field            | type    | rules                                   | meaning                         |
|------------------|---------|-----------------------------------------|---------------------------------|
| fromFloor        | integer | 1..200, required                        | which floor this keypad is on   |
| destinationFloor | integer | 1..200, required, != fromFloor          | where they want to go           |
| boardingCount    | integer | >= 1, required                          | how many people board here      |

Response (Brain -> Keypad), the acknowledgment:
```json
{ "status": "received", "requestId": "r-00042" }
```
| field     | type   | rules                              | meaning                               |
|-----------|--------|------------------------------------|---------------------------------------|
| status    | string | "received" or "rejected"           | did the brain accept the request      |
| requestId | string | present when status = "received"   | id the brain assigns to TRACK this    |
|           |        |                                    | request (needed to measure wait time) |

Decisions:
- The brain assigns a **requestId**. This is the thread tying a request from
  creation -> service, so the evaluation system can measure its pending time.
- A request where destinationFloor == fromFloor is **rejected** (status
  "rejected", no requestId); the keypad only shows "sent" on acceptance.


---

## MESSAGE 2 — Next destination   (Elevator PULLS from Brain)   [LOCKED]

**Endpoint:** `GET /api/elevators/{id}/next-destination`  (the car calls this when
it is ready to move — i.e. doors about to close). PULL model, not push: the brain
continuously maintains each car's ordering stack internally; the car asks for its
single NEXT destination at the last safe moment and gets the brain's freshest
answer. The car is "dumb" — it just goes where told and drives its panels.
**Responses:** `200 OK` + the body below; `204 No Content` if the car has nothing
to do (stay put / go idle).

Response body (the car's NEXT destination only — NOT the whole stack):
```json
{
  "elevator": "A",
  "floor": 5,
  "boarding": [
    { "requestId": "r-00042", "destinationFloor": 17, "count": 2 }
  ]
}
```
| field                       | type    | rules                     | meaning                                      |
|-----------------------------|---------|---------------------------|----------------------------------------------|
| elevator                    | string  | the car id                | which car this is for                        |
| floor                       | integer | 1..N                      | the single next floor to go to               |
| boarding[]                  | array   | may be empty              | who boards at that floor (for landing panel) |
| boarding[].requestId        | string  | matches a known request   | ties to the request (brain tracking)         |
| boarding[].destinationFloor | integer | 1..N                      | where this group goes (for the landing panel)|
| boarding[].count            | integer | >= 1                      | how many board here (for the landing panel)  |

Decisions:
- NEXT DESTINATION ONLY (pull model). The brain updates the car's stack all the
  time; the car pulls just the top when ready to move, then reports its status
  (msg 3). The brain continues from where that order came in its own planning
  (internal optimization — not on the wire).
- NO `exiting` list. The landing panel only shows who BOARDS ("X for floor Y").
  People exit by seeing their destination arrive on the IN-CAR panel. The brain
  tracks who exits where internally (state + evaluation), not on the wire.
- ONE response drives motor + both panels: `floor` = in-car "next floor" panel;
  `boarding` = landing-panel text. No separate panel messages.
- A destination is just a FLOOR + a boarding list; NOT labelled pickup/dropoff.
  The car stops, opens doors, people board/exit, closes. Drop-off bookkeeping is
  brain-internal.

---

## MESSAGE 3 — Status report / arrival confirmation   (Elevator -> Brain)   [OPTIONAL TELEMETRY]

**Endpoint:** `POST /api/elevators/{id}/status`  (the car calls the brain).
**Responses:** `200 OK`.
**When sent:** on **ARRIVAL** at a floor (doors about to open). NOT on departure —
departure is already signalled implicitly by the next `GET /next-destination` pull.

**Why optional:** the core dispatch loop runs on the pull. Status reports are
safekeepers — they give the brain ground-truth to catch any real-world drift,
feed the visualizer, and give the evaluation honest timestamps. In the ideal
simulation (deterministic, bounded stop times) they add robustness; with real
hardware they become load-bearing.

```json
{
  "elevator": "A",
  "currentFloor": 5,
  "direction": "UP",
  "doorState": "OPENING",
  "event": "ARRIVED",
  "arrivalTime": "2027-01-13T09:30:12Z"
}
```
| field        | type     | rules                              | meaning                             |
|--------------|----------|------------------------------------|-------------------------------------|
| elevator     | string   | car id                             | which car                           |
| currentFloor | integer  | 1..N                               | floor just arrived at               |
| direction    | string   | "UP" / "DOWN" / "IDLE"             | travel direction before arrival     |
| doorState    | string   | "OPENING" / "OPEN"                 | doors opening on arrival            |
| event        | string   | "ARRIVED"                          | what just happened                  |
| arrivalTime  | ISO-8601 | required                           | exact moment of arrival             |

### Timing semantics (important for metrics)
A full stop-cycle has two distinct durations — reported separately:
- **Travel time** = movement from last departure to this ARRIVAL. Ends at
  `arrivalTime`. Passengers are SERVED (picked up / delivered) during the dwell
  that follows arrival — so pickup/delivery timestamps = arrivalTime, NOT
  departure time. Wait time and journey time key off arrival.
- **Dwell time** = doors open + transfer + close. NOT in this report. The brain
  can compute it as: (time of next pull) − arrivalTime. The hard ceiling bounds
  it. The next pull = "dwell done, ready to move" (implicitly).

### The pull drives the ABOARD lock (not this report)
When a car pulls its NEXT destination, that act confirms the PREVIOUS pickup stop
completed and the boarded parties are now ABOARD → the brain marks them ABOARD
and locks their destinations at that point. Message 3 is a ground-truth ARRIVED
signal; the ready/DEPARTED signal is the pull itself.

---

## MESSAGE 4 — Evaluation data   (Elevator/Brain -> Evaluation)   [DEPRIORITISED]

> Deprioritised for now. The evaluation wiring is deferred until
> we design the simulation and decide what the model trains on. The shape below is
> a PLACEHOLDER sketch only — NOT approved/locked. We'll define it properly during
> the simulation-design session. (Everything needed to measure wait/journey is
> already derivable from the requestId + timestamps the brain tracks internally.)

Placeholder sketch (do not treat as final):

```json
{
  "requestId": "r-00042",
  "originFloor": 5,
  "destinationFloor": 17,
  "count": 2,
  "timeRequested": "2027-01-13T09:30:00Z",
  "timePickedUp":  "2027-01-13T09:30:12Z",
  "timeDelivered": "2027-01-13T09:30:48Z"
}
```
| field            | type    | meaning                                            |
|------------------|---------|----------------------------------------------------|
| requestId        | string  | the request being reported (the tracking thread)   |
| originFloor      | integer | where it started                                   |
| destinationFloor | integer | where it ended                                     |
| count            | integer | party size                                         |
| timeRequested    | ISO-8601| when the keypad request was made                   |
| timePickedUp     | ISO-8601| when the car picked them up  (-> WAIT = picked - requested) |
| timeDelivered    | ISO-8601| when delivered (-> JOURNEY = delivered - requested) |

From these three timestamps the evaluation system derives WAIT and JOURNEY per
request, then aggregates: average wait (primary metric), plus the distribution
for the max-time guard / fairness analysis. Traffic-scenario inputs (demand
patterns, passenger weights for failure modelling) are defined later with the
simulation design.

---

## Note on where the clever logic lives
These four messages are DELIBERATELY SIMPLE. All the derived dispatch
intelligence — the tree search, slack/laxity, deadline promotion, deferred
commitment, no-stall rule, the lock invariant — is BRAIN-INTERNAL (see docs
03-brain-internal.md, 04-dispatch-model.md, 06-model-synthesis.md) and does NOT
appear on the wire. The contracts carry only what modules must exchange; the
brain's sophistication stays inside the brain. This separation is intentional.
