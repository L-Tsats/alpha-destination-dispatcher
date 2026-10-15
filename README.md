# ML Destination Dispatch — Smart Elevator System

A destination-dispatch elevator control system (AUEB Programming II group project).
Passengers declare their destination at a floor keypad before boarding; a central
controller decides which car serves them and optimizes waiting time, with an
ML-based dispatch optimizer at its core.

## System at a glance
Four modules communicating over REST (JSON):

| Module | Role |
|--------|------|
| Keypad (per floor) | Takes a request (destination + how many boarding), confirms "sent". |
| Central controller (the brain) | Tracks all cars, decides which car + where, optimizes pending time. |
| Elevator controller (one per car) | Mechanical: moves, owns its doors, drives displays, reports status. |
| Evaluation system | Runs traffic scenarios, measures waiting time. |

See `BRIEFING/` for the full design, decisions, and diagrams.

## Build plan
- Stage 1: 4 elevators, 40 floors, single section, no transfers (shippable).
- Stage 2: 20 elevators, 200 floors, multiple sections with transfers.

## Tech
- Java 21, Maven. JSON payloads over REST (Gson).

## Team
Members and task areas to be assigned.
