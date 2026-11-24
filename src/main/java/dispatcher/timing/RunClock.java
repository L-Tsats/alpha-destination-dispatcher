package dispatcher.timing;

import java.util.Locale;

// One shared start time for the whole run, so every log line uses the same clock
public final class RunClock {
    private static long startNanos = System.nanoTime();

    private RunClock() {}

    // Call once, at the very top of main
    public static void start() {
        startNanos = System.nanoTime();
    }

    // Seconds since start, e.g. "[  12.35 s]"
    public static String stamp() {
        return String.format(Locale.ROOT, "[%7.2f s]", (System.nanoTime() - startNanos) / 1e9);
    }
    
}
