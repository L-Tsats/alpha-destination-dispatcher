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

---

## MESSAGE 1 — Keypad POST Request to Controller:

**Endpoint:** `POST /api/requests`  (keypad calls the brain).
**Responses:** `201 Created` + the ack body on success; `400 Bad Request` if the
body is malformed or destinationFloor == fromFloor (rejected).

Request body:
```json
{
  "fromFloor": 3,
  "destinationFloor": 17,
  "boardingCount": 2,
  "time": "2027-01-13T09:30:12Z"
}
```
| field            | type     | rules                                   | meaning                         |
|------------------|----------|-----------------------------------------|---------------------------------|
| fromFloor        | integer  | 1..200, required                        | which floor this keypad is on   |
| destinationFloor | integer  | 1..200, required, != fromFloor          | where they want to go           |
| boardingCount    | integer  | >= 1, required                          | how many people board here      |
| time             | ISO-8601 | required                                | exact moment of req. creation   |

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

## MESSAGE 2 — Elevator[i] GET NextOrder from QueueEnsemble

**Endpoint:** `GET /api/elevators/{id}/next-destination`  (the car calls this when
it is ready to move — i.e. doors about to close). PULL model, not push: the brain
continuously maintains each car's ordering stack internally; the car asks for its
single NEXT destination at the last safe moment and gets the brain's freshest
answer. The car is "dumb" — it just goes where told and drives its panels.
**Responses:** `200 OK` + the body below; `204 No Content` if the car has nothing
to do (stay put / go idle).

Response body (the car's NEXT destination only — NOT the whole queue):
```json
{
  "elevator": "A",
  "nextFloor": 5,
  "boarding": [
    { "requestId": "r-00042", "destinationFloor": 17, "count": 2 }
  ]
}
```
| field                       | type    | rules                     | meaning                                      |
|-----------------------------|---------|---------------------------|----------------------------------------------|
| elevator                    | char    | the car id                | which car this is for                        |
| floor                       | integer | 1..N                      | the single next floor to go to               |
| boarding[]                  | array   | may be empty              | who boards at that floor (for landing panel) |
| boarding[].requestId        | string  | matches a known request   | ties to the request (brain tracking)         |
| boarding[].destinationFloor | integer | 1..N                      | where this group goes (for the landing panel)|
| boarding[].count            | integer | >= 1                      | how many board here (for the landing panel)  |
