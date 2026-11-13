# Target project structure (for the team)

Root package: `dispatcher`. Based on the files in the repo as of 2026-11-13. Tests were not listed in detail: each test moves with the class it tests.

## Phase 1: MOVE ONLY (no logic changes)

Only package declarations, folder locations and imports change. One person does it, in one commit, while nobody has unpushed work. Everyone pulls before continuing.

```
src/main/java/dispatcher/
  app/
    Main.java                    <- from sim/Main.java (currently commented out)
  api/
    BrainServer.java             <- from sim/Brain/Brain.java (renamed; still the placeholder server)
    BrainApiClient.java          <- from sim/http/
    dto/
      PassengerRequest.java      <- from sim/http/
      AssignmentResponse.java    <- from sim/http/
      NextDestinationResponse.java <- from sim/http/
  devices/
    keypad/
      Keypad.java                <- from keypad/ (the console one)
      KeypadState.java           <- from keypad/
    elevator/
      Elevator.java              <- from sim/elevator/
      ElevatorRun.java           <- from sim/elevator/
      ElevatorState.java         <- from sim/elevator/
      ElevatorHttpServer.java    <- from sim/http/   (TEMPORARY: moved only so ElevatorRun still compiles; deleted in phase 2)
  dispatch/
    brain/
      Car.java                   <- from sim/runner/
      CarPlan.java               <- from sim/runner/
    eval/                        unchanged (Evaluator, PlanEvaluation, PlanScorer, Request, Status, Stop)
    search/                      unchanged (Search)
    assign/                      unchanged (InsertionAssigner, Insertion)
  timing/                        unchanged (TravelModel, ...)
  sim/
    traffic/                     unchanged (Generator, Scenario, Knot, Segment, TrafficType, Passenger)
    runner/
      Runner.java                stays
      Event.java                 stays
      EventType.java             stays
```

Left where it is for now, deleted in phase 2: `sim/keypad/Keypad.java` (the one-request test client). It is replaced by the replay client.

Rules for phase 1:
- Use the IDE's move/rename (or `git mv`), so history is kept and imports update.
- Package declarations must match the folder.
- `mvn test` must give the same result before and after.
- The old `sim/Brain` folder differs from `sim/brain` only by case. Moving the file to `api/BrainServer.java` avoids the Windows case-rename problem.

## Phase 2: ADDITIONS (after everyone has adjusted)

Owner in brackets.

```
  dispatch/brain/BrainCore.java        [Tassos] the 3 contract methods + stats, time as a parameter
  api/BrainServer.java                 [Tassos] real handlers, calls BrainCore
  api/dto/...                          [Tassos + team] add optional `time`; add `requestId` and `status` to the ack; add the status record
  api/BrainApiClient.java              [team] status POST; one shared HttpClient; timeouts
  timing/Dwell.java                    [Tassos] the dwell formula in one place
  sim/play/ScenarioSpec.java           [Tassos] scenario as JSON: traffic type, lambda knots, length, seed, floors, cars
  sim/play/HttpReplay.java             [Tassos] plays a generated scenario against a running server, with a speed factor
  experiment/Experiment.java, ExperimentConfig.java, RequestRecord.java, Summary.java, CsvWriter.java   [Tassos, or a teammate for Summary/CSV]
  devices/elevator/ElevatorRun.java    [team] use TravelModel for travel time; loop (pull, move, report arrival via POST status, repeat); speed factor; stop creating and starting ElevatorHttpServer
  devices/keypad/Keypad.java           [team] floor count from config; move scanner.close() out of the loop
  app/Main.java                        [Tassos] subcommands: brain, demo, replay, experiment
```

Also in phase 2:
- Delete `sim/keypad/Keypad.java`.
- Fold `keypadstructure.txt` into `SPECS/keypad.md`, or delete it.
- Delete `devices/elevator/ElevatorHttpServer.java`. DECIDED: one centralized server, inside the brain (`api/BrainServer`). Keypads and elevators are clients only. The elevator reports its state with `POST /api/elevators/{id}/status`. Anything that wants to read a car's floor (a GUI, for example) asks the brain, never the elevator. That may need a read endpoint on the brain later.

## Dependency rule

- `devices` talk to the brain only through `api` (the client and the DTOs), and may use `timing`.
- `api` (the server) calls `dispatch.brain.BrainCore`.
- `dispatch` never imports `api`, `devices` or `sim`.
- `sim` (the fast runner and the replay) may use `dispatch`, `timing` and `api`.

Dispatch logic exists once, so what you demo is what was tested.
