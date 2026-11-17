# Finishing the real devices (no dummy clients)

The server side is ready: `api/BrainServer` with `POST /api/requests` and `GET /api/elevators/{id}/next-destination` (long polling: the request is held open until there is a stop, or answers **204** after about 25 s). Everything below makes the real keypad and elevator talk to it.

## Lampis

1. **`app/Main`**: start `BrainServer` on port 8080. Arguments: number of floors, number of elevators. Prints "Brain running on 8080".
2. **Server log** (for the showcase): the server writes every request, ack and pull, with a timestamp, to a JSON-lines file. Playback reads that file later at any speed.

## Elevator (`devices/elevator/ElevatorRun`)

1. **Loop forever:** pull `next-destination`, move there, run the doors, pull again. Today `main` pulls once and stops.
2. **Handle 204:** it means "nothing yet, ask again". `BrainApiClient.getNextDest` currently throws on anything that is not 200, so it should return `null` (or an empty result) on 204, and the loop just asks again.
3. **Travel time from `TravelModel`:** use `new TravelModel().buildTravelTable(nFloors)` and sleep `table[distance]` seconds, instead of 1 s per floor. Then the brain's predictions match the elevator.
4. **Doors:** open, try to close after 8 s, then pull. (Later: retry every 3 s if interrupted. With a console elevator nobody can interrupt it yet, so a fixed 8 s is fine for now.)
5. **Remove `ElevatorHttpServer`:** the brain is the only server. Delete the class and stop starting it in `main`.
6. **Arguments:** elevator id (A, B, ...), start floor (1), brain URL (default `http://localhost:8080`), number of floors.

## Keypad (`devices/keypad/Keypad`)

1. **Floor range from an argument** (number of floors) instead of the fixed 1..10.
2. **Bug:** `scanner.close()` is inside the `while (true)` loop, so the second input fails. Create the scanner once and close it after the loop (or never).
3. **Show the letter:** after a successful request, print "Go to elevator X" from the ack, then return to the floor screen.
4. **Arguments:** the keypad's own floor, number of floors, brain URL.

## How to run the whole system by hand

1. `app/Main 40 2` (brain, 40 floors, 2 elevators)
2. `ElevatorRun A 1` and `ElevatorRun B 1` in two terminals
3. `Keypad 3 40` in another terminal, type a destination, see the letter, and watch the assigned elevator move.

## Known limits (fine for the first working system)

- The brain assumes people board the elevator it assigned them. Nothing checks it yet.
- The elevator's dwell (8 s) differs from the brain's prediction (5.1 s for one party). This drift is expected and will be measured.
- `POST /api/elevators/{id}/status` does not exist yet. It comes later, for exceptions such as overweight.

## Later (after this works)

- Non-immediate mode with the web app / lobby screen. Both modes stay functional; see `parked-ideas.md` sections 1 and 5.
