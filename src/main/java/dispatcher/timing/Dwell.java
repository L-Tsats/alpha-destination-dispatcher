package dispatcher.timing;

// How long an elevator stays at a stop (doors open, people move, doors close).
// One value, used by the brain's predictions AND matched by the elevator, so they agree.
public final class Dwell {
    // Fixed: the elevator opens its doors, waits 8 s, and closes them.
    // For variable stops (later): 4.0 + 1.1 * max(boarding, alighting)
    public static final double SECONDS = 8.0;

    private Dwell() {}
}
