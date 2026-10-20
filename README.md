# ML Destination Dispatch — Smart EGCS

A destination-dispatch elevator control system (AUEB Programming II group project).
Passengers declare their destination at a floor keypad before boarding; a central
controller decides which car serves them and optimizes waiting time, with a
multi-car Monte Carlo Tree Search (MCTS) based dispatch optimizer (inspired by Alpha-Zero).

## System at a glance
Four modules communicating over REST (JSON):

| Module | Role |
|--------|------|
| Keypad (per floor) | Takes a request (destination + how many boarding), confirms "sent". |
| Central controller (the brain) | Tracks all cars, decides which car + where, optimizes pending time. |
| Elevator controller (one per car) | Mechanical: moves, owns its doors, in & out displays. |
| Evaluation/ SImulation system | Runs traffic scenarios, measures waiting time. |
| Visual Interface | Live-scenario graphic representation of elevator movements |

See `BRIEFING/` for the full design, decisions, and diagrams.

## Build plan
- Stage 1: 1 elevator, 10 floors
- Stage 2: 4 elevators, 40 floors, no transfers.
- Stage 3: 20 elevators, 200 floors, multiple sections with transfers.

## Tech
- Java 21, Maven. JSON payloads over REST (Gson).
