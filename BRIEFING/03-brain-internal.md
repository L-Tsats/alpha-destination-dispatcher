# Brain Internal Design (NOT a message contract — the brain's own logic)

> These are rules the CENTRAL BRAIN obeys internally. They are not part of any
> REST message. The elevator cars never see this; they just execute the plan
> they're sent and use counts/destinations only for their display panels.
> This doc exists because the "a boarded passenger is never stranded" guarantee
> has to live SOMEWHERE — and that somewhere is the brain's state + planner.

## 1. Request lifecycle (brain tracks this per request)
Every request moves through states:
- **WAITING**  — requested, not yet picked up. Freely reassignable to any car,
                 reorderable, the optimizer can do whatever it wants with it.
- **BOARDED**  — picked up, riding in a specific car, not yet delivered.
                 Its destination stop is now FIXED (see rule 3).
- **DELIVERED** — dropped at destination. Done; leaves the active set.

The brain also tracks, per car, which parties are currently BOARDED in it,
and each car's current occupancy (for the capacity constraint).

## 2. The WAITING -> BOARDED transition (when the "lock" engages)
The brain does not physically see people board. The transition is driven by the
car's NEXT PULL (`GET /next-destination`):
- Car drives to F, opens doors, waits the FIXED BOARDING WINDOW, closes doors.
- Car pulls its next destination — this act confirms "stop complete, ready."
- On receiving that pull, the brain marks those pickup parties as BOARDED.
=> That instant is when their destination becomes locked.
An optional ARRIVED status report (msg 3) gives ground-truth timing for metrics
but does NOT drive the lock; the pull does.
Before the report: WAITING (reassignable). After: BOARDED (locked).

### Boarding-window assumption (DECIDED)
We assume the fixed boarding window is enough time for informed passengers to
enter. Anyone not aboard in time is simply NOT marked boarded; in a real
building they'd just make a new request. We do NOT build sensing/verification
to confirm everyone got in. Fails gracefully, no added complexity.

## 3. The plan invariant (the "lock" — this is correctness)
Whenever the brain builds or edits a car's plan, the following MUST hold:
- For every party currently BOARDED in that car, the plan CONTAINS that party's
  destination (drop-off) stop. The planner treats these as FIXED inputs.
- The brain may freely add / remove / reorder ONLY stops for WAITING parties
  (not-yet-boarded).
A plan that omits a boarded party's drop-off is INVALID — the brain is
forbidden to emit it. This holds at EVERY recompute, so no sequence of edits,
however long or with however many intervening stops, can ever strand a boarded
passenger. "Multiple chances to be forgotten" -> zero, because the constraint
re-applies at every single recompute.

### What the invariant does and does NOT promise
- GUARANTEES: a boarded passenger is always eventually delivered (correctness).
- DOES NOT guarantee they're delivered OPTIMALLY fast. The brain may route a
  car on a detour (e.g. dip down to grab a new party) before a boarded party's
  stop, as long as that stop stays in the plan. Detours are ALLOWED and SAFE.
  Keeping transit time *reasonable* is an OPTIMIZATION concern (the HMM /
  dispatch-quality work), separate from correctness.

## 4. Separation of concerns (why the contracts stay simple)
- CONTRACT (messages): the car just receives "your current full plan" and
  reports status. It does no dispatch logic. Counts/destinations in the message
  are for the DISPLAY PANEL only.
- BRAIN STATE: lifecycle + occupancy + which parties aboard which car.
- BRAIN PLANNER: obeys the invariant (correctness) + optimizes routing (quality).
The fuzziness we kept hitting was from trying to put brain-state logic into the
message contract. They are different layers. Messages stay simple; the brain
holds the rich state and the rules.

## 5. Hook for the ML work
The lifecycle + traffic state defined here IS the state the HMM/dispatch
optimizer operates on. So specifying this wasn't a detour — it's the foundation
the Tier-2 optimization builds on.
