# Model Synthesis — Environment + Dispatcher (the design)

> The complete, settled model we will simulate and build. Scope framing:
> STAGE 1 = one car (ordering); STAGE 2 = several cars (adds which-car);
> STAGE 3 = many cars + sections/transfers. Design-on-paper; the simulator and
> tuning come in the build. Values flagged [KNOB] are what tuning / ML optimizes.
> (Derivation history is kept separately, outside the repo.)

================================================================
## PART A — THE ENVIRONMENT (the simulated world)
================================================================

### A.1 Static setup
- Cars (Stage 1: 1), each with capacity K people (e.g. K = 8).
- N floors (Stage 1: ~10), floor height h (~3.5 m).
- Physics constants (from CIBSE Guide D / Peters "Elevate"; see doc 05):
  rated speed v (~2.5 m/s), accel/decel a (~1.0 m/s^2), door cycle t_door (~4 s),
  passenger transfer t_pax (~1.2 s/person), small start delay t_start.

### A.2 Dynamic state (at any instant)
- Per car: position, direction (up/down/idle), door state, current occupancy.
- Per request: { id, originFloor, destinationFloor, partySize,
  status: WAITING | ABOARD | DONE, timeRequested, timePickedUp }.
- The brain tracks all of the above (cars do not track their own occupancy).

### A.3 It is an ONLINE problem
Requests arrive over time, unpredictably, while cars are already moving. We do
NOT know future requests. Each arrival is an event.

### A.4 Discrete-event simulation (time jumps event-to-event)
Nothing changes between events, so the clock jumps to the next event. Events:
(1) a request arrives, (2) a car completes a stop (arrived + doors done),
(3) a car finishes a move. A priority queue ordered by time drives it.

### A.5 Timing model + PRECOMPUTED TIME TABLE (key enabler)
Door/accel/decel are constants; the ONLY variable in a travel time is the floor
difference. So precompute the full floor-to-floor TRAVEL-TIME TABLE once (N x N;
tiny even for 200 floors). Then evaluating any plan = table lookups + constant
door/transfer sums -> plan evaluation is cheap (lets us try many candidate plans).
  travel_time(i -> j): D = |i-j|*h ; if D <= v^2/a : 2*sqrt(D/a) else v/a + D/v ;
                       + t_start.
  stop_time: t_door + max(boarding, alighting) * t_pax.
From a plan's timeline we read each request's pickup and delivery time exactly.

================================================================
## PART B — THE OBJECTIVE
================================================================
Per request: WAIT = pickup - requested ; JOURNEY = delivery - requested.

- PRIMARY OBJECTIVE: minimize AVERAGE wait. (Not a percentile — a percentile
  objective is circular and needs data we don't have up front. Average is clean,
  computable, and the likely graded metric.)
- FAIRNESS GUARD (separate from the objective): a MAX-TIME ceiling [KNOB] on any
  single request. Drives the overdue/promotion rule (Part C). Optimize the
  average; guard the tail with the ceiling.
- Hard constraints (never violated): capacity (<= K); deliver only after pickup;
  every accepted request is eventually delivered (the lock invariant).

### Side sub-problem (bonus to present): setting the ceiling from a cold start
No prior distribution exists at start. Approach: ADAPTIVE while data is scarce,
then CONVERGE to a STATIC ceiling once the observed wait distribution is compact
(a std-dev threshold, confidence-weighted by sample size). Avoids the circularity
of chasing live percentiles.

================================================================
## PART C — THE DISPATCHER (sampled informed rollouts; MCTS-shaped)
================================================================
Each decision produces the next action by SAMPLING many complete candidate
plans, built by INFORMED ROLLOUT, and keeping the best legal one.

### C.1 Build one candidate plan (an informed rollout)
From the deciding car's current/end state, build a COMPLETE plan step-by-step
until all current requests are placed. At each step:
- candidates = floors that serve a request (a pickup or a delivery; a stop is
  just a floor + who-boards/who-exits, not labelled by purpose),
- NUDGE the next-step choice by two soft forces:
    * SLACK (= distance to the request's deadline, computed exactly from the time
      table): lower slack (more urgent) -> pull its DELIVERY sooner,
    * POSITIONAL COST (cheap-to-reach from here): can justify serving a nearby
      less-urgent request, barely delaying others,
- enforce LEGALITY as you build: a PICKUP only if capacity allows; never drop an
  ABOARD party's delivery. => plans are LEGAL BY CONSTRUCTION (no wasted illegals).

The two-phase nature is handled here: each request contributes a PICKUP event
(gated by capacity) and a DELIVERY event (pulled by slack); the rollout schedules
both in order within the plan.

### C.2 Score and select
Score each completed plan by average wait (cheap, via the time table). Because
the nudges are SOFT, different rollouts explore different orderings. Sample MANY;
keep the best legal plan. Commit ONLY its first action; the car moves (NO STALL);
replan on the next event.

### C.3 Why this isn't just greedy
Each single rollout is greedy-ish step-by-step, but foresight is EMERGENT from
sampling volume + soft exploration: across many sampled plans we try non-greedy
orderings and keep the best complete one. (How much to rely on sampling volume
vs. deliberate per-step lookahead is a tunable DIAL, not a structural choice.)

### C.4 No stalling + deferred commitment
Cars are always moving to a committed next floor; they never idle-wait. We don't
commit the next decision early — we keep sampling during travel and commit at the
LAST SAFE MOMENT (doors closing), using travel time as a buffer to fold in any
just-arrived requests (safe: a brand-new request has ample slack, can't breach in
that window).

### C.5 No ETA drift (by design)
The predicted timeline is trustworthy: every boarder is a KNOWN request (no
surprise passengers), and stop/dwell times are capped by a reliable hard ceiling.
So no "actual vs predicted diverged" correction machinery is needed. (Rests on
the bounded-stop-time guarantee; if ever relaxed, msg-3 status reports become the
re-grounding mechanism.)

### C.6 Baseline to beat
SCAN / LOOK and a GREEDY (nearest / lowest-cost) dispatcher are the baselines.
Our sampled-rollout dispatcher aims to beat their average wait while respecting
the max-time guard. The hand-crafted rules are BOTH a working v1 AND the
benchmark against which a later learned version is judged.

================================================================
## PART D — MULTI-CAR (Stage 2): same rollouts, adds "which car"
================================================================
A rollout step also assigns WHICH car. Build the multi-car plan LAYER BY LAYER
ACROSS CARS (not car-by-car):
- decide the 1st destination for EVERY car, then the 2nd for every car, then the
  3rd, ... until all requests are dispersed. (Keeps cars BALANCED — no car filled
  while another sits idle.)
- Trivial case (fewer requests than free cars): give each request to the car that
  reaches it soonest.
Score the complete multi-car plan by AGGREGATE wait; sample many; keep best;
commit the now-deciding car's first action; replan on events. The GROUPING of
requests to cars EMERGES from the slack/position-nudged layered assignment — not
a separate rigid clustering step.

================================================================
## PART E — WHERE ML PLUGS IN (Tier-2; after a working rule-based system)
================================================================
ML does NOT replace the logic — it (a) TUNES knobs (max-time ceiling + its
adaptive->static rule, slack weighting, position-vs-slack balance, the
sampling-vs-lookahead dial), and (b) improves the ROLLOUT GUIDANCE: a learned
policy biasing which plans to sample (so fewer samples reach the same quality).
Search + learned guidance = the AlphaZero pattern; our hand-crafted priors are
the baseline it must beat. Optionally, demand PREDICTION (e.g. HMM) feeds likely
future requests into planning to reduce online blindness. Build order: working
rule-based system FIRST, then learn better knobs/guidance, judged on average wait.

================================================================
## PART F — STAGE LADDER
================================================================
- STAGE 1: 1 car, ~10 floors. Pure ordering (Parts A-C).
- STAGE 2: 4 cars, 40 floors. Adds which-car (Part D). Single section.
- STAGE 3: 20 cars, 200 floors. Adds sections + TRANSFERS: a cross-section
  request splits into LEGS (chained sub-requests), each a normal request in its
  section with its own slack; reuses Stage-1/2 machinery per section + a routing
  layer. (Zoning mirrors real towers: cable/travel limits -> ~4 cars / ~40 floors
  per section with sky-lobby transfers; 4:40 is the fixed section unit.)

================================================================
## PART G — THE ONE PARKED-OPEN PIECE (deferred, not a gap)
================================================================
MEMORY / intelligent re-sampling: how good samples bias later samples to be
smarter (the real-MCTS selection/backprop machinery). The current design
("sample many, soft-nudge, keep best") WORKS without it; this is a v2 refinement
to read up on (MCTS / adaptive Monte Carlo) and tune in the simulator — and may
be subsumed by training a guidance network. Efficiency/speed is likewise an
EMPIRICAL question answered by building + timing the simulator (with the known
fallback: learned guidance to cut the number of samples needed), not by more
paper design.

TREE / SEARCH-WORK REUSE (parked optimization): the brain pull-model (elevator
GETs its next destination) means the brain can REUSE prior search work instead of
rebuilding from scratch at the new start position — continue from the committed
branch rather than re-deriving back to it. Also, a new request with ample slack
likely won't change the early (soon-upcoming) layers we've already filtered, so a
full replan is often unnecessary. Both are brain-INTERNAL performance
optimizations, invisible to the contract; deferred to the optimization phase.

================================================================
## OPEN ITEMS (confirm during build)
================================================================
- Confirm K, v, and the initial max-time ceiling for ~10-floor Stage 1.
- Passenger transfer: simultaneous (max) vs sequential (sum) -> leaning max.
- Representation of prediction output as a planning input (if/when HMM is added).
