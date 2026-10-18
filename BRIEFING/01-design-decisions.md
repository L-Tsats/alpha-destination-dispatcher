# Elevator System — Design Decisions

> Our own clean destination-dispatch design (not a copy of the brief's framing).
> Concise record of the decisions. Full model: 06-model-synthesis.md;
> message contracts: 02-contracts.md.

## Core model
- **Pure destination dispatch. NO in-car buttons.**
- A passenger declares intent BEFORE boarding at a **keypad on their floor**:
  destination floor + how many are boarding.
- The request goes to the **central controller (brain)**, which decides which car
  serves it. The passenger learns their car from the **landing panel**, not the
  keypad.

## Optimization metric
- Minimize **average waiting time** (request -> pickup). A separate **max-time
  ceiling** guards against anyone waiting too long (fairness). The evaluation
  system measures it.

## Why this model is good (talking point)
Because the DESTINATION is known at request time, the controller knows where
everyone wants to go BEFORE committing a car — richer information than
button-based systems, which enables smarter dispatch and prediction.

## The 4 modules
1. **Keypad / user interface** — per floor; takes a request, confirms "sent".
2. **Central controller (brain)** — receives requests, decides dispatch, tracks
   everything, optimizes waiting.
3. **Elevator controller** — one per car; mechanical only (move, doors, displays,
   status); pulls its next destination from the brain.
4. **Evaluation system** — runs traffic scenarios, measures waiting.

## Architecture: central is the brain
- The brain holds the full picture (every car's position, direction, stops,
  occupancy) and makes ALL dispatch decisions.
- Elevators are "arms": mechanical work only — pull next destination, move,
  **own their doors** (door timing is local, never the brain's concern), drive
  their displays, report status. Cars do NOT track their own occupancy; the brain
  does.
- Clean boundary (good to state): **brain decides WHICH car and WHERE; the
  elevator decides HOW it physically moves.** Doors are the clean example —
  purely local, never the brain's business.

## Capacity
- Modelled as a **person count**, not kilograms (nobody computes additive weight;
  a soft person-counter is easy and reliable). Each car has a max occupancy
  (e.g. K=8-16). A full car can't accept more boardings until some alight; the
  optimizer must respect this.

## Displays (all elevator-arm logic, kept simple)
1. **Keypad** — only takes a request and confirms "request sent". Does NOT show
   your car.
2. **Landing panel** (above each entrance) — as a car approaches, announces e.g.
   "X people for floor Y", a little before arrival. This is where a passenger
   learns their car. (Like real destination-dispatch buildings.)
3. **In-car display** — only the NEXT floor the car stops at, so riders know when
   to get off.

## Request + interaction model
- Keypad -> brain: { fromFloor, destinationFloor, boardingCount } -> ack.
  (No alighting count: who exits a floor = everyone whose destination is that
  floor; derivable, so the keypad stays simple.)
- **Pull model:** the brain continuously maintains each car's plan; the car
  PULLS its next destination when ready to move, then reports status. Only the
  brain is an HTTP server. (Enforces no-stall + last-moment commitment.)

## Three-stage build plan
Complexity added one axis at a time; each stage is a working, shippable system.
- **Stage 1:** 1 car, ~10 floors — core single-car logic (ordering).
- **Stage 2:** 4 cars, 40 floors — adds "which car" (assignment); single section.
- **Stage 3:** 20 cars, 200 floors — adds sections + transfers (a cross-section
  request splits into legs chained across sections).

Real-world basis: tall towers split into zones (~4 cars / ~40 floors) with
sky-lobby transfers, because cable weight / travel time make one shaft spanning
200 floors impractical. So **4:40 is our fixed section unit**, and Stage 3 tiles
the Stage-1/2 unit per section + a routing layer (extension by composition, not
rebuild).

## Evaluation (proposed; finalized with all teams)
- Scenarios = traffic patterns: up-peak (morning), down-peak (evening),
  mixed/interfloor (midday). A scenario generates synthetic requests for a
  duration, feeds them in as if real passengers, and records timestamps.
- Metrics: average waiting time (primary), average journey time, max wait,
  throughput, (optional) utilization.

## Open question
- How keypad units and elevators get their IDs / the brain's address for REST
  (planned: a config parameter given at startup).
