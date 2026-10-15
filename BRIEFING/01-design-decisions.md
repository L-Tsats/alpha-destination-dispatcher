# Elevator System — Design Decisions (our creative take)

> We're NOT copying Spinellis's exact framing — we're designing our own clean
> destination-dispatch system. This doc records the decisions we make together.

## Core model (DECIDED)
- **Pure destination dispatch. NO in-car buttons.**
- The passenger declares intent BEFORE boarding, at a **keypad on their current
  floor**:
    - destination floor
    - how many people are boarding here
    - (and how many will get out — alighting count)
- The request goes to the **central controller**, which decides WHICH elevator
  serves it and tells the passenger which car to board (its letter).
- **Scale target: up to 20 elevators, up to 200 floors.** (Going for the big one.)

## Optimization metric (DECIDED)
- **How long requests stay PENDING** = wait time. The system's goal is to
  minimize this. The evaluation system measures it; the dispatch brain (later:
  HMM) tries to minimize it. This is the number everything is judged by.

## Why this model is good (briefing talking point)
Because the DESTINATION is known at request time, the controller knows where
everyone wants to go BEFORE committing a car. That's richer information than
button-based systems, enables batching/prediction, and is exactly what makes a
smart (HMM) dispatcher meaningful. We chose the model that makes the clever
part possible.

## The 4 core modules (maps cleanly to Spinellis, saves mental load)
1. KEYPAD / USER INTERFACE  — per floor; takes requests, shows assigned car.
2. CENTRAL CONTROLLER       — receives requests, dispatches, tracks pending.
3. ELEVATOR CONTROLLER      — one per car; obeys central, reports status.
4. EVALUATION SYSTEM        — runs scenarios, measures wait time.

(Detailed per-module "knows / does / reports" + data shapes: see 02-modules.md
as we fill it in.)

## Capacity (DECIDED)
- Model capacity as a **person count**, not kilograms. Real limit is kg, but we
  aggregate to people — nobody computes their additive weight, so a soft
  person-counter is easy and reliable.
- Each elevator has a max occupancy (e.g. 16). A full car cannot accept more
  boardings until some passengers alight. The optimizer must respect this (a
  full car is useless no matter how close).
- Cheap to add: just one extra input (count) alongside destination. Tassos
  takes the heavier load in modelling/optimization around it.

## Architecture: CENTRAL IS THE BRAIN (DECIDED — Style 1)
- The central controller holds the full picture and makes all the decisions:
  every elevator's position, direction, pending stops, and occupancy.
- Central decides assignments and sends elevators explicit orders.
- Elevators are "arms": they do the MECHANICAL work only —
    - send/receive requests over REST (talk to central),
    - execute movement orders,
    - **manage their OWN doors** (door logic is NOT the brain's concern; it's
      solely the elevator's — open/close/timing lives locally).
    - report status back (floor, direction, next stops, occupancy).
- Central TRACKS occupancy (follows from brain-owns-everything).

Why: our metric (pending time) and the HMM optimizer both want ONE place with
global information. Also maps to the team — the fat, interesting central
controller is Tassos's; the thin mechanical elevator-executor is a well-scoped
task for a weaker teammate with low integration risk.

### Clean boundary worth stating in the briefing
BRAIN (central) decides WHICH car and WHERE it goes.
ELEVATOR decides HOW it physically does it (doors, local execution) and reports.
Doors are a good example: purely local, never the brain's business.

## Displays / UX (DECIDED)
Three display surfaces, kept deliberately simple so users (and our devs) follow:
1. **Keypad** — ONLY receives a request and confirms "request sent
   successfully". That's it. It does NOT tell you which car to board — that is
   announced shortly afterward on a landing panel. Dead simple.
2. **Landing panel** (above each elevator entrance, on the floor) — as a car
   approaches, announces e.g. "X people for floor Y", shown a LITTLE BEFORE the
   car arrives so waiting people can prepare to board. This is ALSO where a
   passenger learns which car is theirs (by seeing their floor announced above
   a given entrance). Realistic touch, like real destination-dispatch buildings.
3. **In-car display** — one thing only: the NEXT floor the car stops at, so
   riders know when it's their turn to get out.

All three displays are **elevator-arm logic** (not the brain).

### Important consequence for the dispatch message (note for schema design)
The landing panel ("X people for floor Y") needs info the BRAIN assembled — the
elevator doesn't inherently know who it's picking up or their destinations. So
the dispatch ORDER from central -> elevator must carry a small **manifest**, not
just a bare floor number:
    "at floor 5, pick up 3 people whose destination is 7"
We ALREADY have this data (destination-dispatch knows destinations up front), so
it just has to FLOW to the arm. The arm then simply "displays the current
manifest", refreshed whenever central sends/updates an order. Real-time timing
details deferred — not a problem, data is available.

=> This settles an earlier open question: the dispatch command carries the FULL
picture (manifest), not just "serve floor N".

## Request message (DECIDED — first schema)
- Keypad -> Brain:  { fromFloor (implicit from WHICH keypad), destinationFloor,
  boardingCount }.  No alightingCount — who gets OUT at a floor is derivable:
  it's everyone whose destinationFloor equals that floor. Simpler keypad.
- Brain -> Keypad:  just an acknowledgment ("received / sent ✓").

## VERIFIED FULL LOGIC (canonical picture)

Four modules:
  KEYPAD (per floor, stateless) -- request --> CENTRAL BRAIN -- order+manifest --> ELEVATOR ARM
                                <-- "sent ✓" --              <-- status report --
                                                    CENTRAL BRAIN -> EVALUATION (wait-time metric)

| Module        | Knows                              | Does                                   |
|---------------|------------------------------------|----------------------------------------|
| Keypad        | nothing (stateless)                | take request, confirm "sent"           |
| Central brain | ALL cars: pos, dir, stops, occupancy, pending | dispatch, assign LATE, optimize wait |
| Elevator arm  | its own car only                   | move, doors, 2 displays, report        |
| Evaluation    | scenario + wait times              | run scenarios, measure the metric      |

Three message flows (future schemas):
  1. Keypad -> Brain:   { fromFloor, destinationFloor, boardingCount }  -> ack
  2. Brain -> Elevator: order WITH MANIFEST ("go to floor X; pick up N for Y")
  3. Elevator -> Brain: status { position, direction, nextStops, occupancy }
  4. -> Evaluation:     operating data to measure pending time

Core principles (briefing one-liners):
  - Pure destination dispatch, no in-car buttons; intent declared before boarding.
  - Brain decides WHO/WHERE; arm decides HOW (doors = the clean example).
  - LATE BINDING: passenger not locked to a car until it arrives; optimizer stays free.
  - Metric = how long requests stay PENDING.
  - Scale: up to 20 cars, 200 floors; capacity as person-count.

## Evaluation system (PROPOSED — pending cross-team standardization)
> Spinellis makes evaluation standardization a GROUP-WIDE activity, so this is
> our STARTING proposal to bring to the cross-team meeting, not the final word.

- **Scenarios = traffic patterns** (the standard elevator-evaluation ones):
    - up-peak    (morning: everyone enters lobby, goes up)
    - down-peak  (evening: reverse)
    - mixed / interfloor (midday: random floor-to-floor)
  A scenario = "generate requests following pattern P for duration D."
  >> This is exactly where the HMM lives: modelling/predicting these patterns.
- **Metrics:**
    - average waiting time (request -> car arrives)  <-- PRIMARY metric
    - average transit/journey time (board -> arrive at destination)
    - max waiting time (worst case)
    - throughput (people moved per unit time)
    - (optional) average occupancy / car utilization
- **How it runs:** evaluation system GENERATES synthetic requests (plays the
  scenario), feeds them into the system as if real passengers, records
  timestamps, computes the metrics.

## Briefing depth — TWO TIERS (match depth to the room)
- **Tier 1 (whole group, incl. the 4 beginners):** the four modules, the
  message flows, and evaluation at the BEHAVIOURAL level only — "we test the
  system under different crowds (morning rush, 5pm exodus, random midday) and
  measure how long people wait." Everyone has ridden an elevator; everyone can
  brainstorm scenarios here. The architecture diagram is pitched at this tier.
- **Tier 2 (Tassos + most-trusted teammate only):** the dispatch OPTIMIZATION,
  the HMM, and the actual statistics/metrics math. NOT in the group briefing —
  a separate deeper conversation. Lots of existing material to adapt; we don't
  invent methodology from scratch.
- Rationale: dumping stats on 4 beginners in an alignment meeting creates fog
  and anxiety — the opposite of the meeting's goal (A = alignment). Keep deep
  stuff in its lane.

## TWO-STAGE PLAN (DECIDED) — the scope backbone

Real-world constraint behind this: supertall buildings SPLIT into zones (e.g.
floors 1-40, 40-80, ...) each served by its own elevator bank, because of
physical cable-weight / travel-time limits. People TRANSFER between zones at
"sky lobbies". A single flat bank serving a huge range isn't physically done.
We mirror that reality by staging:

### STAGE 1 — single zone (the realistic max for one bank)
- **4 elevators, 40 floors, NO transfers.**
- One controller, one flat bank — a complete, demo-able, SHIPPABLE system.
- Goal: all the MECHANICAL stuff exists and works; team gets the hang of it;
  Tassos gets the dispatch-logic hang of it. Even if Stage 2 never happened,
  this is a finished product.

### STAGE 2 — multi-zone (extend the working base)
- **20 elevators, 200 floors, WITH transfers.**
- Add: zone coverage, TRANSITION/transfer floors, splitting a request into legs,
  measuring ETA per leg so we can manage the in-between (transfer) waiting times.
- Lands mostly on Tassos (brain work), but by now the TEAM has reps and can help
  with this layer too — a growth path for the people, not just the software.
- (Parked sub-idea: slight OVERLAP between zone coverage so a single transfer
  floor doesn't get overcrowded in busy buildings. Real technique; evaluate in
  Stage 2.)

Why staged (scope discipline): each stage is a shippable milestone, not a
half-thing. Sequences the LEARNING with the BUILDING — nobody builds the hard
part while still learning basics. Stage 2 becomes an EXTENSION of a working
system, not a from-scratch monster. Avoids the "roof before the walls" trap for
a team of ~2.5 effective devs + 4 beginners.

Briefing use: present Stage 1 as the target; show Stage 2 as the planned
extension so the team sees the ambition AND the safe path to it.

### The unifying abstraction (briefing gold — makes Stage 2 sound easy)
- **The 4-cars : 40-floors ratio is the SECTION UNIT.** We keep that ratio per
  section from the start, so every section is identical. It's the rule we build
  the extensions off of.
- **Stage 1 models, on point, the behaviour of ONE section = a single
  "entrance point."** Every floor has an entrance; one flat bank serves it.
  One point of entry/exit logic, done well.
- **Stage 2 is the SAME unit, tiled per section, with a "2-point entrance"**
  (each section's normal floors PLUS the transfer floor it shares with the next
  section), wrapped in a **multi-section travel-optimization layer** that splits
  a request into legs and routes it across sections.
- So Stage 2 is NOT a new system: it's Stage 1's logic repeated per section +
  a routing wrapper. We build the unit once, then compose/tile it. The Stage 1
  mechanical work is literally reused per section — extension by composition,
  not rebuild. THIS is why staging is safe and why the ratio stays fixed.

## Open questions still to brainstorm
- How do keypad floor-units and elevators get their IDs/addresses for REST?
