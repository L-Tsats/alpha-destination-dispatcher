// EXERCISE 40 - Threads (reference solution)
// NOTE: output order is non-deterministic (threads interleave). "Correct" =
// it runs and all threads finish, not an exact output match.

public class Threads {

    public static void main(String[] args) throws InterruptedException {
        // Task 1 — one Runnable in one Thread, started.
        Thread t = new Thread(new Counter("A"));
        t.start();
        t.join();   // wait for it before moving on (so the demos don't tangle)

        // Task 2 — array of 3 counters: start all, then join all.
        System.out.println("--- three counters concurrently ---");
        Counter[] counters = {
            new Counter("X"), new Counter("Y"), new Counter("Z")
        };
        Thread[] threads = new Thread[counters.length];
        for (int i = 0; i < counters.length; i++) {
            threads[i] = new Thread(counters[i]);
        }
        for (Thread th : threads) th.start();
        for (Thread th : threads) th.join();
        System.out.println("all done");

        // Task 3 — cocktail guests chattering concurrently.
        System.out.println("--- the party ---");
        Thread g1 = new Thread(new Guest("Nice food!", 1, 3));
        Thread g2 = new Thread(new Guest("Ha ha ha...", 1, 2));
        Thread g3 = new Thread(new Guest("Hi, I'm Maria.", 2, 4));
        g1.start(); g2.start(); g3.start();
        g1.join(); g2.join(); g3.join();
        System.out.println("party over");
    }
}

// Tasks 1 & 2
class Counter implements Runnable {
    private final String name;

    Counter(String name) { this.name = name; }

    @Override
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println(name + ": " + i);
        }
    }
}

// Task 3
class Guest implements Runnable {
    private final String mumble;
    private final int pause;   // seconds between remarks
    private final int stay;    // total seconds to stay
    private int hereFor = 0;

    Guest(String mumble, int pause, int stay) {
        this.mumble = mumble;
        this.pause = pause;
        this.stay = stay;
    }

    @Override
    public void run() {
        try {
            while (hereFor < stay) {
                Thread.sleep(pause * 1000L);
                hereFor += pause;
                System.out.println(mumble);
            }
        } catch (InterruptedException e) {
            System.out.println("Something came up; got to go.");
        } finally {
            System.out.println("Bye. (" + mumble + ")");
        }
    }
}
