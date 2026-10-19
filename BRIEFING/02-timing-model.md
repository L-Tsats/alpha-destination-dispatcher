# Timing Model — standard elevator constants + travel-time computation

> The choisen desing choise and the following constants turn a SEQUENCE 
> OF STOPS into a predicted TIMELINE, which is what lets us compute waiting 
> time, slack, ETAs, and the promotion deadline. Values are from industry-standard
> sources so they're defensible/citable, not invented. Field name: 
> "elevator/lift traffic analysis"; core concept: Round Trip Time (RTT). 
> (Content rephrased from sources for licensing compliance.)

## Sources
- Peters Research "Elevate" (industry-standard lift simulation software) door-time
  defaults, which reference CIBSE Guide D 2020 Table 7.1:
  https://elevate-desktop.freshdesk.com/support/solutions/articles/203000003184-door-times
- CIBSE Guide D (Transportation Systems in Buildings) — the standard reference
  for lift traffic design values.
- General elevator-traffic-analysis literature (RTT calculations).

## Standard constant values (defaults for our model)
| Constant | Symbol | Typical value | Notes |
|----------|--------|---------------|-------|
| Rated (top) speed | v | 1.0-2.5 m/s (mid-rise); higher for tall | pick ~2.5 m/s for 40 floors |
| Acceleration / deceleration | a | ~1.0 m/s^2 | comfort limit |
| (Jerk) | j | ~1.0-1.6 m/s^3 | rate-of-change of accel; can ignore v1 |
| Floor-to-floor height | h | ~3.3-4.0 m | use ~3.5 m |
| Door open time | t_open | ~1.5-1.9 s | from car level to doors fully open |
| Door close time | t_close | ~2.0-2.9 s | from start closing to locked |
| Passenger transfer time | t_pax | ~1.0-1.2 s per person | boarding or alighting, per person |
| Start delay / motor-to-move | t_start | ~0.5-1.0 s | small fixed overhead per move |

(For v1 we can keep ONE door cycle constant t_door = t_open + t_close ~ 4 s, and
one t_pax ~ 1.2 s/person. Refine later.)

## Travel time between two floors (the core computation)
Moving the car n floors (distance D = n * h) is NOT just D / v, because the car
must accelerate up to v and decelerate back to 0. Two cases:

1. SHORT hop (never reaches top speed — accelerates then immediately decelerates):
   distance to reach v = v^2 / a. If D <= v^2 / a:
       travel_time = 2 * sqrt(D / a)
2. LONG run (reaches top speed, cruises, then decelerates):
   If D > v^2 / a:
       travel_time = (v / a) + (D / v)
   (= time spent accelerating+decelerating, which is v/a, PLUS cruise distance/v,
    accounting for the accel/decel ramps.)

Plus a small fixed start delay t_start per move.

(Jerk-limited profiles are more accurate but add complexity — v1 uses the
trapezoidal accel model above. Note it as a refinement.)

## Time spent AT a stop (dwell)
At each stop the car spends:
    stop_time = t_open  +  (max(boarding, alighting) * t_pax)  +  t_close
- boarding/alighting happen during the door-open window.
- use max() not sum() if people can board and alight simultaneously through the
  same doors (typical). If modelled as sequential, use (boarding+alighting).
  >> DECISION NEEDED: simultaneous (max) or sequential (sum)? Simultaneous is
     more realistic for a single door; pick that for v1.

## Putting it together: time for a planned route
Given a car at floor f and an ordered list of stops S1, S2, ... :
    total_time = sum over consecutive pairs of travel_time(floor_i -> floor_{i+1})
               + sum over each stop of stop_time(stop)
This gives the predicted TIMELINE, from which we derive:
- each request's PICKUP time (when the car reaches its origin) -> WAIT time
- each request's DELIVERY time (when the car reaches its destination) -> JOURNEY time
- SLACK = (time under current plan) - (direct pickup->delivery time with no detours)
- PROMOTION trigger = when a request's estimated journey time approaches the
  percentile-driven deadline ceiling.

## The "hard ceiling" idea
We want a deadline ceiling that is almost NEVER breached, so the model stays
simple and fairness is guaranteed by the promotion override (not baked into the
optimizer). The ceiling should be:
- percentile-driven (set so the vast majority of requests finish under it),
- made as STRICT as possible UP TO the point where tightening it further would
  start hurting the MEAN time, AND only while the standard deviation is already
  tight/compact (so we're not sacrificing overall performance for the tail).
- We will likely TRAIN/tune this ceiling alongside the model (a learned
  parameter), so the hard ceiling is both tight and rarely breached.

## Open decisions to confirm
- simultaneous (max) vs sequential (sum) passenger transfer -> lean simultaneous.
- exact v for each Stage's building specs -> ~2.5 m/s (sanity-check vs sources).
