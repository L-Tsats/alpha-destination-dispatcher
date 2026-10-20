# Startup & Wiring Specification

## Overview
Every module runs from the command line as a separate process. The brain is the only server; keypads and elevators are clients. Each module gets its config via command-line arguments at startup — no service discovery, no config files needed.

---

## Brain (Central Controller)

**Run:**
```
java Brain --port 8080 --elevators 4 --floors 40 --capacity 8 --maxWait 120
```

**Arguments:**
- **--port**: the port the brain listens on (clients connect here)
- **--elevators**: number of cars (2-20), auto-assigned IDs "A", "B", "C", ...
- **--floors**: number of floors (3-200)
- **--capacity**: max people per car (K)
- **--maxWait**: max-wait ceiling in seconds (for deadline override)

The brain starts the HTTP server and waits for clients to connect.

---

## Elevator Car

**Run:**
```
java Elevator --id A --brain http://localhost:8080
```

**Arguments:**
- **--id**: this car's identifier letter (must match one the brain expects)
- **--brain**: the brain's base URL

The elevator starts IDLE and begins pulling its next destination from the brain.

---

## Keypad (User Interface)

**Run:**
```
java Keypad --floor 3 --brain http://localhost:8080
```

**Arguments:**
- **--floor**: the floor this keypad is installed on (becomes fromFloor in requests)
- **--brain**: the brain's base URL

The keypad starts at its floor-input screen, ready to take requests.

---

## Simulation / Evaluation

**Run:**
```
java Simulation --brain http://localhost:8080 --scenario day-cycle --floors 40
```

**Arguments:**
- **--brain**: the brain's base URL (sends synthetic requests here)
- **--scenario**: which traffic scenario to run (up-peak, down-peak, midday, day-cycle)
- **--floors**: number of floors (must match the brain's config)

The simulation generates requests and feeds them to the brain, then reads metrics back.

---

## Startup Order

1. **Brain first** — it's the server; everyone connects to it.
2. **Elevators** — each registers by pulling its first destination (204 if nothing to do).
3. **Keypads** — ready to take requests immediately.
4. **Simulation** (optional) — starts feeding traffic once everything is up.

---

## How Modules Find Each Other
- Only the brain's URL needs to be known; it's passed to every client as --brain.
- The brain never initiates connections — clients always call it.
- No DNS, no registry, no discovery. Just the URL as a startup argument.

---

## Config Consistency
- Floor count must match across brain, elevators, and simulation.
- Elevator IDs must match what the brain expects (A..N for N elevators).
- A keypad's floor must be within the brain's floor range.
- Mismatches are the operator's responsibility (no runtime negotiation in v1).