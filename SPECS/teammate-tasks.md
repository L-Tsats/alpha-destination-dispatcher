# Finishing the real devices: end-to-end checklist

The server side is ready: `api/BrainServer` with `POST /api/requests` and `GET /api/elevators/{id}/next-destination` (long polling: the request is held open until there is a stop, or answers **204** after about 25 s). It rejects invalid requests (bad floors, from = to, broken JSON) with **400**. The brain needs NO changes for the steps below.

Weight / overweight on the real devices is NOT part of this. It comes later through `POST status`.

## The overall shape (decided 2026-11-18)

One launcher builds the whole building. Devices have NO `main` of their own; they are classes the launcher starts.

```
app/Main (launcher)
  1. start BrainServer on 8080
  2. for each elevator A, B, ...: new Thread(new ElevatorController(id, nFloors, brainUrl)).start()
  3. new KeypadFeeder(nFloors, brainUrl, passengers).run()
```

- Everything talks to the brain over HTTP on localhost, even inside one program. Never bypass HTTP (a partner team's devices will run as separate programs).
- Floor and elevator counts are read ONCE in the launcher and passed to the brain, the elevators and the feeder, so they can never disagree.
- Elevators are active (own thread, own loop). Keypads are passive (they only react to `press`).

---

## A. The elevator (`devices/elevator`)

Done: `ElevatorController(id, nFloors, brainUrl)` (starts at floor 1), travel time from `TravelModel`, fixed 8 s doors (matches the brain's `Dwell.SECONDS`), `ElevatorHttpServer` deleted, 204 handled in `BrainApiClient.getNextDest` (returns `null`), `targetFloor` removed from `Elevator`, current floor updated on arrival.

Left: only `ElevatorController.java`, marked with TODO 1-4 in the file. Nothing else needs opening.
1. **TODO 1 (in `run()`):** if `answer` is `null` (HTTP 204, nothing to do yet), `continue` to ask again.
2. **TODO 2 (in `run()`):** otherwise go there: `moveToFloor(answer.nextFloor());`
3. **TODO 3 (in `moveToFloor`):** a pickup at the floor the elevator is already on must still open the doors. Delete the early `return`; put the moving part inside `if (targetFloor != elevator.getCurrentFloor()) { ... }` instead.
4. **TODO 4:** start every print with `"[" + elevator.getId() + "] "`, so several elevators in one console stay readable.

What you need to know, nothing more:
- `client.getNextDest(id)` returns the next floor, or `null` if there is nothing to do yet. The call itself waits up to about 25 s.
- `answer.nextFloor()` reads the floor out of the answer.
- `moveToFloor(floor)` does the whole trip, doors included.
- The try/catch around the loop is already written: brain down means wait 2 s and retry; interrupted means stop.

Not in the elevator, on purpose: who boards or gets off (the brain decides; the elevator only knows floors), weight, a start floor other than 1.

## B. Test the elevator on its own

9. **By hand:** start `app/Main`, start one elevator with `new Thread(new ElevatorController("A", 40, "http://localhost:8080")).start()` (temporary main or a test), then send:
   ```powershell
   Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/requests -ContentType 'application/json' -Body '{"fromFloor":3,"toFloor":9}'
   ```
   Expected: A goes to 3, doors, goes to 9, doors, then goes quiet. After about 30 s of nothing: no crash (the 204 handling works).
10. **Automated test** (Lampis writes it once steps 1-4 exist): `BrainServer` on port 0 with `setPollTimeoutMs(500)`, one elevator thread, one request; wait (with a timeout) until the elevator's current floor is 9, then stop everything.

## C. The keypad side onto GitHub

11. **Keypad (`devices/keypad`): done and tested locally, still to push.** `Keypad(floor, nFloors, brainUrl)`; `press(destination)` checks the floor, sends the request, sets the screen; `getScreen()` reads it. `KeypadTest` (4 tests). (The `BrainServer` request validation, 400, is already pushed.) Push `Keypad.java`, `FloorCheckResult.java`, `KeypadTest.java` and the deletion of `KeypadState.java`, co-authored with the teammate who finishes the feeder.
12. **`KeypadFeeder.run()`: in progress.** Skeleton ready (one keypad per floor). For each passenger: sleep until `getTime()` MINUTES after start, then `keypads.get(from).press(to)` and print the screen. Commit only `KeypadFeeder.java`.

## D. The launcher

13. **`app/Main` (Lampis):** read the floor count, the elevator count and the brain URL once. Start `BrainServer`, start one `ElevatorController` thread per elevator (A, B, ...), then run `KeypadFeeder` with a passenger list (hand-made first, then from the `Generator`). If the feeder is not pushed yet: brain + elevators only, requests with `Invoke-RestMethod`.

## E. End-to-end run

14. Launcher with 10 floors, 2 elevators, 3 hand-made passengers about 6 s apart. Expected:
    - each keypad line shows "Submitted! Await elevator: X";
    - that elevator goes to the pickup floor, then the destination;
    - with two elevators, both move;
    - nothing crashes while idle.

## F. Wrap up

15. Full `mvn test`, all green.
16. Commit the elevator work (the people who wrote it commit it). Pull before starting, commit only your files.
17. Delete leftovers: `sim/keypad/Keypad.java`, `KeypadState.java` (unless a GUI needs it), `keypadstructure.txt`.
18. Mark this file done for the elevator and the launcher.

---

## Known limits (fine for the first working system)

- The brain assumes people board the elevator it assigned them. Nothing checks it yet.
- The elevator's dwell differs from the brain's prediction (5.1 s for one party). This drift is expected and will be measured.
- `POST /api/elevators/{id}/status` does not exist yet. It comes later, for weight and overweight.

## Later (after this works)

- Weight report from the elevator (`POST status`), stored for the metrics.
- Door retry model (retry every 3 s if interrupted) once a people simulation can say who is still passing.
- `timing/Dwell`: one dwell setting, so the server can predict 8 s while the sim keeps its formula (the formula is currently duplicated in `BrainCore.dwell` and `Evaluator`).
- Non-immediate mode with the web app / lobby screen. Both modes stay functional; see `parked-ideas.md` sections 1 and 5.
