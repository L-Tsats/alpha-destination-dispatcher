# LOOK baseline planner (handoff spec)

Status: not started. Good first task for a teammate who wants to learn the dispatch mechanics before joining algorithm optimization.

## Why it exists

The dispatcher (brute-force search, later pruning/MCTS) needs something to beat. LOOK is the classic collective-control baseline: it sweeps in one direction, serves what it can on the way, and reverses only when nothing is left ahead.

It is the **single-car** baseline. It is not a strawman: it uses the same information a conventional elevator has (the direction of each request, `sign(to - from)`) and nothing more about the destination.

## Rules

- Direction of a request is `sign(to - from)`. No other destination information is used.
- Capacity is 3 requests onboard (same `K` as `Search`). Each request gets one pickup stop and one dropoff stop.
- Pick up a waiting request only when the car is travelling in that request's direction.
- Drop off any onboard request whose destination is the current floor.
- Reverse only when nothing is left ahead in the current direction. No trips to the end of the shaft.
- Output is a `List<Stop>` where `Stop(floor, boarding, alighting)`, the same type `Search` produces. It is scored by the same `Evaluator`.

## Pseudocode

```
direction = toward the nearest pickup (or nearest onboard dropoff)
loop until nothing is left:
    at each floor along the current direction:
        drop off onboard requests whose destination is here
        pick up waiting requests whose pickup is here and whose direction
            matches the car direction, if onboard < capacity
    if nothing is left ahead in this direction:
        reverse direction
```

Details to handle:
- Next stop in a direction = nearest floor ahead that has work (a dropoff, or a matching pickup).
- Several requests at the same floor can share one `Stop`, or be separate stops. Start with separate stops, merge later.
- Idle start: the direction is a tie-break decision. Default is "toward the nearest pickup". Document whatever you choose.

## Signatures (proposed)

```java
public class Look {
    public Look(int nFloors, int capacity) { ... }
    public List<Stop> plan(List<Request> requests, int startFloor) { ... }
}
```

Later, extract a small `Planner` interface so `Search` and `Look` can be swapped inside the Runner.

## Test to start from

Car idle at 5. A: 3->8 (up), B: 7->2 (down), C: 6->9 (up). Expected stop floors: `6, 9, 7, 2, 3, 8`.

```java
@Test
void sweepsUpThenDownThenUp() {
    List<Request> reqs = List.of(
            new Request("A", 3, 8, 0.0),
            new Request("B", 7, 2, 0.0),
            new Request("C", 6, 9, 0.0));

    List<Stop> stops = new Look(10, 3).plan(reqs, 5);

    List<Integer> floors = stops.stream().map(Stop::getFloor).toList();
    assertEquals(List.of(6, 9, 7, 2, 3, 8), floors);
}
```

Reasoning: start at 5 heading up (nearest work is above). Pick up C at 6, drop C at 9. B and A are skipped on the way up (B wants down, A is below the car). At 9 reverse, pick up B at 7, drop B at 2. At 2 reverse, pick up A at 3, drop A at 8.

Note: the accessor in the real repo is `Stop.getFloor()`. Adjust if it changes.

## Checks once it works

- Capacity: with 4 waiting requests that all fit a sweep, never more than 3 onboard at any time.
- Static sanity check: on the same snapshot, `Search` score <= `Look` score (mean journey time). If LOOK ever beats the exhaustive search, one of them has a bug.
- Add more hand-verified cases before trusting it (idle tie-break, requests on the car's current floor, reversal with nothing ahead).

## Open decisions (for whoever picks it up)

- Idle-direction tie-break (nearest pickup vs oldest request).
- Same-floor merging into one `Stop`.
- How the Runner calls it: replan on every new request (like `Search`), or keep a committed sweep. Start with replanning, since it plugs into the existing loop.

## Context

Objective for the comparison is mean journey time (request to dropoff). Report wait, journey, p90 and max for every system. See `simulation.md` and `search-next-layer.md`.
