# REST Message Contracts (the standardization deliverable)

> Payload format: **JSON** (the REST norm; light; Gson-friendly in Java).
> Each contract defines exactly WHAT the "right stuff" is for a message:
> every field, its type, whether it's required, and its rules. This is what
> every teammate builds against so modules wire together cleanly.
>
> Convention: floors are integers 1..200; counts are non-negative integers;
> ids are strings; times are ISO-8601 timestamps (e.g. "2027-01-13T09:30:00Z").

---

## MESSAGE 1 — Request   (Keypad -> Brain)   [LOCKED]

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
