# Research Reading + Java Prerequisites (traffic gen + the dispatch algorithm)

> Curated so you understand (a) non-homogeneous Poisson for traffic generation,
> (b) how to implement the tree-search dispatcher, and (c) what Java libraries
> already exist vs. what you must build. Links are starting points; content
> rephrased for licensing compliance.

================================================================
## PART 1 — TRAFFIC GENERATION (non-homogeneous Poisson)
================================================================

### Why Poisson (the accepted model)
Elevator-traffic literature broadly accepts passengers arrive by a POISSON
process. Key refinement from real data: arrivals are often in BATCHES (groups),
and batch size depends on time of day — which matches our model (a request has a
party size / count).

### The core concepts, in order
1. HOMOGENEOUS Poisson = constant arrival rate lambda. Generate it by sampling
   INTER-ARRIVAL times from an EXPONENTIAL distribution with mean 1/lambda.
   (This is the one fact to anchor on: "gap to next arrival ~ Exponential(1/lambda)".)
2. NON-HOMOGENEOUS Poisson (NHPP) = rate VARIES over time, lambda(t). This is what
   we need: morning up-peak, lunch two-way, evening down-peak = a lambda(t) curve.
3. HOW to simulate an NHPP = the THINNING algorithm (Lewis-Shedler, 1979). This
   is THE standard method and it's simple:
     - pick a constant lambda_max >= lambda(t) for all t,
     - generate a homogeneous Poisson process at rate lambda_max,
     - for each candidate arrival at time t, KEEP it with probability
       lambda(t)/lambda_max, else DISCARD ("thin" it).
   The survivors are a correct NHPP sample. That's it — a loop with one
   accept/reject test. You can implement this yourself in ~20 lines.

### Reading (most useful first)
- **METE XIII — "Generating Elevator Passengers in Time"** (Peters/CIBSE lineage,
  ResearchGate 317388893). THE paper for our exact task: generating elevator
  passengers over time with Poisson, including batches. Start here.
- **"A study of arrival process of lift passengers"** (ResearchGate 306415796) —
  real-building evidence that arrivals are batched and time-dependent.
- Lewis & Shedler, "Simulation of Nonhomogeneous Poisson Processes by Thinning,"
  Naval Research Logistics Quarterly 26(3), 1979 (nps.edu / dtic.mil PDFs) — the
  original thinning method. Short, foundational.
- Columbia lecture notes on NSP / thinning (columbia.edu ~ks20 and ~ww2040) —
  clean worked explanations if the paper is dense.
- "nhppp: Simulating NHPPPs in R" (arXiv 2402.00358) — a modern, readable survey
  of NHPP simulation methods (it's for R, but the METHODS transfer to Java).
- Optional context: "Modelling Elevator Traffic Systems Using Queuing Theory"
  (ResearchGate 273761657) and "Dynamic behavior ... random inflow" (arXiv
  2012.12571) — queueing-theory framing + down-peak Poisson analysis.

### Our reference implementation to replicate/learn from
- charles-works/SimElevatorTraffic (already dissected; see PROJECT-elevator-sim.md)
  uses non-homogeneous Poisson for passenger flow. Use its STRUCTURE as
  inspiration for how a traffic generator plugs into the sim.

================================================================
## PART 2 — THE DISPATCH ALGORITHM (tree search + learned guidance)
================================================================

### What our algorithm IS, in standard terms (so papers are readable)
- A STATE-SPACE SEARCH / branch-and-bound over stop-orderings (tree search).
- Run in a RECEDING-HORIZON / model-predictive way (replan, commit first step).
- (If need be) Guided by a LEARNED POLICY when the tree is too big to fully search =
  the MCTS + policy/value network pattern (AlphaZero family).
- Fairness via EDF / least-laxity scheduling.

### Reading (most useful first)
- **Baeldung "Monte Carlo Tree Search for Tic-Tac-Toe"** (baeldung.com) — a
  concrete, beginner-friendly MCTS IN JAVA walkthrough. Best starting point to
  actually understand MCTS by code.
- MCTS + neural-net guidance papers (arXiv 1812.00045 "MCTS as a Demonstrator
  within Deep RL"; PhilippeW83440/mcts-nnet "MCTS + DQN for motion planning") —
  show the "pretrained net guides the tree search" architecture = exactly our
  plan. Read AFTER the Baeldung basics.
- Earliest-Deadline-First (EDF) / Least-Laxity-First — real-time scheduling
  textbooks/notes. This formalizes our slack/promotion rule. (Short; you already
  understand the idea, just want the formal name/guarantees.)
- Model Predictive Control (MPC) — one intro (any controls course note) to see
  the "plan-horizon, commit-first-step, replan" pattern formally.

================================================================
## PART 3 — JAVA LIBRARIES: what EXISTS vs. what we BUILD
================================================================

### EXISTS (use these — don't reinvent)
- **Apache Commons Math** (org.apache.commons:commons-math3) — has
  `ExponentialDistribution` and `PoissonDistribution` with `.sample()`. This gives
  the random draws for traffic generation directly. => Used for inter-arrival
  times (exponential) and batch sizes. This is your traffic-gen workhorse.
- **java.util.Random / RandomGenerator** (built in, JDK) — enough for uniform
  draws and the thinning accept/reject test; can do exponential via
  `-Math.log(1-rng.nextDouble())/lambda` if we don't want the Commons dependency.
- **mctreesearch4j** (JVM MCTS library, published in JOSS) — a ready MCTS
  implementation for the JVM built around MDPs. Option FOR LATER if/when we need
  MCTS for Stage 2/3, so we don't hand-roll the tree search.
- **Smile** (already chosen) — general ML + HMM (smile.sequence.HMM) if/when we
  do demand prediction. Has the stats tooling too.
- **Gson** (already in the project) — JSON for the REST contracts.
- Discrete-event simulation: likely DON'T need a DES library — a
  `PriorityQueue<Event>` ordered by timestamp (standard library) IS a DES engine
  and is exactly what we described in the model. Build this yourself; it's small
  and you'll understand it fully.

### BUILD FROM SCRATCH
- The DISCRETE-EVENT ENGINE: a `PriorityQueue<Event>` loop (poll earliest, jump
  clock, handle, maybe schedule new events). ~ the pattern from the dissected repo.
- The NHPP TRAFFIC GENERATOR: thinning algorithm on top of Commons Math's
  exponential sampling + a lambda(t) curve you define per scenario (up/down/lunch).
  ~20-40 lines. Nobody will hand  an "elevator traffic generator"; you compose it.
- The TIMING MODEL: travel-time (accel/decel) + dwell formulas from doc 05. Pure
  arithmetic, build it.
- The DISPATCHER: the tree/branch-and-bound search with slack pruning + deadline
  promotion (doc 06 Part C). The core of YOUR contribution. Build it; optionally
  swap in mctreesearch4j later for Stage 2/3 scale.
- The EVALUATION: aggregate the message-4 timestamps into average wait + the
  distribution. Simple stats (or use Commons Math DescriptiveStatistics).

### Rough prerequisite shopping list (Maven deps)
- org.apache.commons:commons-math3  (distributions + stats)   [traffic + eval]
- com.google.code.gson:gson          (already have)            [REST JSON]
- (later) Smile                        (already chosen)          [HMM / ML]
- (later) mctreesearch4j               (if MCTS needed)          [Stage 2/3 search]

================================================================
## SUGGESTED STUDY ORDER
================================================================
1. Exponential/Poisson basics + the THINNING algorithm (Columbia notes, 1 sitting).
2. METE XIII (elevator-specific passenger generation) — ties it to our domain.
3. Build a tiny traffic generator (Commons Math + thinning) as a spike.
4. Baeldung MCTS-in-Java (understand the search) — but remember Stage 1 doesn't
   need it; plain pruned search first.
5. (Later, for Stage 2/3) MCTS + policy-net papers, mctreesearch4j, Smile HMM.


================================================================
## PART 4 — MULTI-CAR DISPATCH (Stage 2/3): the three families
================================================================
Checked how industry/research actually does multi-car "which car?" assignment.
Three families:

### Family 1 — Cost-function / ETA-minimization (the COMMERCIAL standard)
When a request arrives, compute an estimated COST (est. time / waiting time) for
each car if it took the request; assign to the lowest-cost car. Greedy, one
request at a time. THIS IS WHAT REAL BUILDINGS DEPLOY (KONE; Peters "ETD"
dispatcher). == our Stage-2 v1 "best car takes it". So v1 is not a toy — it's
the real-world baseline.
- Peters Research - elevator dispatching: peters-research.com/index.php/papers/elevator-dispatching
- Peters - ETD dispatcher enhancements: peters-research.com/index.php/papers/enhancements-to-the-etd-dispatcher-algorithm/
- Peters - benefits & limits of destination dispatch (PDF, download.peters-research.com)

### Family 2 — Optimization / assignment formulation (ACADEMIC, rigorous)
Frame as a formal ASSIGNMENT problem: allocate every passenger to exactly one
car minimizing total cost; solve with OR methods (branch-and-price, submodular
maximization + greedy, matroid constraints). The globally-optimal-across-all-
pending version == our Stage-2 v2. Scheduling cars is NP-HARD (no known optimal)
-> everyone approximates. (Optimizations to add later, once the base works.)
- Aalto assignment formulation (EJOR 2016): sal.aalto.fi/publications/pdf-files/ejor2016_public.pdf
- ETH branch-and-price (static, destination control): strc.ethz.ch/2023/TorresDuran_Bierlaire.pdf
- Submodular function maximization for group elevator scheduling: arxiv.org/html/1707.00617v1

### Family 3 — Reinforcement Learning (RESEARCH frontier; OUR inspiration)
Learn to assign calls to cars from experience. Lineage from 1995 to today;
modern work uses Deep Q-Networks (DQN). NP-hardness is the stated motivation for
learning. The modern "planning layer + RL fine-tuning" split == our "rule-based
core + ML refinement" architecture (already published, so validating, and the
place to read for inspiration LATER). (Note: DQNs are conceptually close to MCTS.)
- Crites & Barto 1995, "Improving Elevator Performance Using RL" (NeurIPS) — the
  classic. papers.nips.cc/paper/1073-...pdf  (READ FIRST for RL framing.)
- Novel RL approach for efficient EGCS (2025): arxiv.org/abs/2507.00011
- Two-layer: queueing-based planning + safe RL (MDPI 2026): mdpi.com/2075-1702/14/3/352
  (closest to our architecture).
- DQN thesis (assign calls to best car): research.sabanciuniv.edu/51782/1/10691227.pdf
- Deep Q Learning + 3-peak traffic sim (UW 2022): arxiv.org/pdf/2210.00065

### Where OUR approach might be novel (honest, not hype)
- Tree-search / MCTS-style PLANNER is RARE in elevator control (field leans
  cost-function + DQN) -> a less-trodden angle.
- Our slack / deadline-promotion fairness mechanism as the pruning driver.
- Clean extension to MULTI-SECTION TRANSFERS (Stage 3) via request-splitting with
  per-leg slack — transfers are LESS studied than single-zone dispatch.
=> Potential paper IF Stage 3 works AND we benchmark against Family 1/2. Hold as
"promising if it pans out", not "guaranteed new". Keep ambition honest.
