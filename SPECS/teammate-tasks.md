# Finishing the real devices: end-to-end checklist

The server side is ready: `api/BrainServer` with `POST /api/requests` and `GET /api/elevators/{id}/next-destination` (long polling: the request is held open until there is a stop, or answers **204** after about 25 s). It rejects invalid requests (bad floors, from = to, broken JSON) with **400**. The brain needs NO changes for the steps below.

Weight / overweight on the real devices is NOT part of this. It comes later through `POST status`.

## The overall shape (decided 2026-11-18)

One launcher builds the whole building. Devices have NO `main` of their own; they are classes the launcher starts.

```
app/Main (launcher)
  1. start BrainServer on 8080
  2. for each elevator A, B, ...: new Thread(new ElevatorRun(id, nFloors, brainUrl)).start()
  3. new KeypadFeeder(nFloors, brainUrl, passengers).run()
```

- Everything talks to the brain over HTTP on localhost, even inside one program. Never bypass HTTP (a partner team's devices will run as separate programs).
- Floor and elevator counts are read ONCE in the launcher and passed to the brain, the elevators and the feeder, so they can never disagree.
- Elevators are active (own thread, own loop). Keypads are passive (they only react to `press`).

---

## A. The elevator (`devices/elevator`)

Already there, keep: `Elevator` (id, current floor, target floor, state), `ElevatorState`, `ElevatorRun.movetoFloor(target)`, `BrainApiClient.getNextDest(id)`.

1. **Constructor:** `ElevatorRun(String id, int nFloors, String brainUrl)` creates its own `Elevator(id, 1)` and its own `BrainApiClient(brainUrl)`. Always starts at floor 1 (the brain assumes it).
2. **Make it a thread:** `ElevatorRun implements Runnable`. `run()` loops forever:
   - ask `getNextDest(id)`;
   - if the answer is `null`: ask again straight away;
   - otherwise `movetoFloor(floor)`;
   - catch `IOException` (brain down): print it, sleep 2 s, continue;
   - catch `InterruptedException`: stop the loop.
3. **204 in `BrainApiClient.getNextDest`:** if the status is 204, return `null`. Keep throwing `IOException` on anything else that is not 200.
4. **Fix the same-floor early return in `movetoFloor`:** skip only the MOVING part. The doors must still open, otherwise a pickup at the elevator's current floor is silently ignored.
5. **Travel time from `TravelModel`:** build `new TravelModel().buildTravelTable(nFloors)` once in the constructor. The whole move takes `table[distance]` seconds; spread it across the floors passed for the prints.
6. **Doors:** one fixed dwell for now (the current 4 s is fine). The door model (try to close after 8 s, retry every 3 s) comes later.
7. **Prefix every print with the id**, e.g. `[A] passing floor 4`, so several elevators in one console stay readable.
8. **Delete `ElevatorRun.main` and `ElevatorHttpServer.java`:** the brain is the only server.

Not in the elevator, on purpose: who boards or gets off (the brain decides; the elevator only knows floors), weight, a start floor other than 1.

## B. Test the elevator on its own

9. **By hand:** start `app/Main`, start one elevator with `new Thread(new ElevatorRun("A", 40, "http://localhost:8080")).start()` (temporary main or a test), then send:
   ```powershell
   Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/requests -ContentType 'application/json' -Body '{"fromFloor":3,"toFloor":9}'
   ```
   Expected: A goes to 3, doors, goes to 9, doors, then goes quiet. After about 30 s of nothing: no crash (the 204 handling works).
10. **Automated test** (Lampis writes it once steps 1-4 exist): `BrainServer` on port 0 with `setPollTimeoutMs(500)`, one elevator thread, one request; wait (with a timeout) until the elevator's current floor is 9, then stop everything.

## C. The keypad side onto GitHub

11. **Keypad (`devices/keypad`): done and tested locally, still to push.** `Keypad(floor, nFloors, brainUrl)`; `press(destination)` checks the floor, sends the request, sets the screen; `getScreen()` reads it. `KeypadTest` (4 tests). (The `BrainServer` request validation, 400, is already pushed.) Push `Keypad.java`, `FloorCheckResult.java`, `KeypadTest.java` and the deletion of `KeypadState.java`, co-authored with the teammate who finishes the feeder.
12. **`KeypadFeeder.run()`: in progress.** Skeleton ready (one keypad per floor). For each passenger: sleep until `getTime()` MINUTES after start, then `keypads.get(from).press(to)` and print the screen. Commit only `KeypadFeeder.java`.

## D. The launcher

13. **`app/Main` (Lampis):** read the floor count, the elevator count and the brain URL once. Start `BrainServer`, start one `ElevatorRun` thread per elevator (A, B, ...), then run `KeypadFeeder` with a passenger list (hand-made first, then from the `Generator`). If the feeder is not pushed yet: brain + elevators only, requests with `Invoke-RestMethod`.

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
- Door model (8 s, retry every 3 s).
- Non-immediate mode with the web app / lobby screen. Both modes stay functional; see `parked-ideas.md` sections 1 and 5.
