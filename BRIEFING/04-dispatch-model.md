# Dispatch Model — paper design (ONE elevator first)

> Goal of this doc: model, on paper, HOW the dispatch decision works, BEFORE any
> code/sim/ML. We deliberately start with ONE elevator so the problem is pure
> ORDERING (what order to serve requests), not ASSIGNMENT (which car). Assignment
> is built later ON TOP of ordering ("run ordering for each candidate car, pick
> the best car"). The knobs/parameters here are what the ML will eventually tune.

## The nature of the problem: it's ONLINE
Requests arrive over time, unpredictably, while the car is already moving.
- OFFLINE = know all requests up front (easy). ONLINE = decide without knowing
  the future (our case, genuinely harder — a known, named difficulty).
- This is WHY prediction (the HMM) helps: it partially "un-blinds" us to near-
  future demand, shrinking the online disadvantage.

## One-elevator problem statement
One car, capacity K, at floor f, direction d (or idle). A set of requests, each =
(origin, destination, partySize, status). Status: WAITING / ABOARD / DONE.
Produce an ordered sequence of stops such that:
- capacity never exceeded (hard constraint),
- a party is never delivered before picked up (hard constraint),
- every accepted request is eventually delivered (the invariant),
- minimizing the objective below.

### No "illegal" actions — only COSTS
The car may go to any floor anytime. The only hard limits are capacity and
pickup-before-delivery. Everything else is TIME COST, so we need a timing model.

## Timing model (constants to source from real elevator engineering)
Turn a sequence of stops into a predicted TIMELINE, which is what lets us compute
waits/ETAs. Need defensible constants (LOOK UP from manufacturer specs / elevator-
traffic-analysis papers; do NOT invent):
- travel time per floor (from rated speed + floor height, e.g. ~1-8 m/s, ~3-4 m)
- accel / decel (~1 m/s^2)
- door cycle: open + dwell + close (~4-8 s total)
Store as named constants so they're easy to tune and cite.

## Objective (fairness-aware, NO arbitrary lambda)
Minimize a single interpretable measure of waiting — chosen to avoid outliers
WITHOUT a mystery weight:
- OPTION A (leaning): minimize a PERCENTILE of wait (e.g. 90th percentile) —
  "make the unluckiest 10% not-too-bad", inherently controls the tail.
- OPTION B: minimize average wait, with a penalty when mean > median (mean>median
  signals a right-skew = a few long-waiters). Keeps the tail short.
Either avoids the lambda tradeoff knob. (Decide A vs B when we have a sim to test.)
Primary clock per request = request -> delivery (journey time); waiting = request
-> pickup.

## Fairness mechanism: DEADLINE / AGING OVERRIDE (kept OUTSIDE the optimizer)
Key simplification: the optimizer stays simple (just minimize wait). Fairness /
"no outliers" is handled SEPARATELY by a hard guard:
- Normally the car plans freely for efficiency.
- If any request's ESTIMATED total time (wait + transit) is about to exceed a
  deadline limit, it is PROMOTED to be served next (a "can't starve" guard).
This is "aging" (from OS scheduling): a task waiting too long gets boosted so it
can't starve. It does the fairness job so the objective function doesn't have to —
fewer tangled heuristics in the optimizer.

## SLACK / LAXITY (key primitive)
For each request, estimate its DIRECT time (pickup->delivery with NO intermediate
stops). Compare to the time under the current plan (with detours). The difference
= SLACK (a.k.a. laxity): how much spare time we have to serve OTHERS in between
without hurting this request.
- High slack -> the car can tolerate detours / chaining for this request.
- Low slack -> serve it directly, don't make it wait.
This tells the planner WHOM it can safely delay to chain in someone else.

## When do we re-decide? (event-triggered, not clock-triggered)
We do NOT tick every second. We re-decide on EVENTS.
- STAGE-1 (simple, build this first): re-plan ONLY when a NEW REQUEST arrives.
  On finishing a stop, the car just pops the next item already in its stack —
  no re-think needed.
- REFINEMENT (later): also do a LIGHTWEIGHT recheck on stop-completion —
  "is my existing plan still best given where I now am / has anyone's slack
  shrunk / did timing drift?" Usually confirms "continue" (near-free), but catches
  drift. NOT needed for the first version.

## The planning approach: receding-horizon / replanning (the Framing 1+2 hybrid)
Resolves "plan the whole sequence" (Framing 1) vs "it's online, requests come
anytime" (Framing 2):
- On each event, take the CURRENT full state, compute a good WHOLE ordering for
  everything known now (global plan).
- Commit/execute only the FIRST step.
- On the next event, THROW AWAY the rest and re-plan from scratch with new info.
=> Global planning each time + responsive to online arrivals. (In control theory
this is "receding-horizon" / model-predictive control.)

## Where the ML plugs in (for later)
- The HMM predicts FUTURE demand (when/where requests will appear) and feeds it
  into the planning step, reducing online blindness.
- Tunable parameters ("knobs") the ML can optimize: the objective's percentile,
  the deadline-override threshold, the slack thresholds for allowing detours,
  (and later, for multi-car, the assignment scoring weights).
- IMPORTANT architecture principle: ML does NOT replace the logic — it TUNES the
  parameters of this rule-based dispatcher. So we build the rule-based version
  with hand-picked values FIRST (works on its own = Stage 1), then ML learns
  better values, judged against the wait metric.

## Decomposition (why one elevator first)
- ORDERING: one car, what order to serve. (This doc.)
- ASSIGNMENT: many cars, which car gets which request = run ordering per candidate
  car, pick the best outcome. Built ON TOP of ordering, later.
