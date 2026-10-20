/*
 * EXERCISE 40: Threads  (concurrent execution)
 * ============================================
 * A THREAD is a flow of execution. A program can run many threads at once — to
 * do things concurrently (servers, games), reduce waiting, or use multiple CPU
 * cores. This rung is the core mechanics; synchronization/deadlock is OUT of
 * scope here (same as the Spinellis material).
 *
 * THE IDEA
 * --------
 * In Java a thread's work is defined by an object implementing the Runnable
 * interface, which requires ONE method:
 *       public void run() { ... the code the thread executes ... }
 * To run it:
 *       Runnable job = new MyJob();
 *       Thread t = new Thread(job);
 *       t.start();      // starts the thread; run() executes CONCURRENTLY
 * Note: call start(), NOT run(). start() spawns a new thread; run() would just
 * call the method on the current thread (no concurrency).
 *
 * WAITING for a thread: t.join() blocks until thread t has finished. The program
 * ends when all threads finish.
 *
 * NOTE ON OUTPUT: thread output INTERLEAVES and the order is NOT deterministic.
 * So "correct" here = it compiles, runs, and all threads complete — NOT an exact
 * output match. That's normal for concurrency.
 *
 * Imports: none special (Thread and Runnable are in java.lang).
 *
 * ------------------------------------------------------------------
 * TASK 1 — one Runnable, one Thread, start()
 * ------------------------------------------------------------------
 * Write a class  Counter implements Runnable  whose run() prints the numbers
 * 1..5, each on its own line (optionally prefixed with a name). In main:
 * create one Counter, wrap it in a Thread, and start() it.
 *
 * ------------------------------------------------------------------
 * TASK 2 — multiple threads + join()
 * ------------------------------------------------------------------
 * In main: create an ARRAY of 3 Counter objects (give each a different name),
 * wrap each in a Thread (an array of Threads), start ALL of them, then join()
 * ALL of them. Print "all done" AFTER the joins — it must appear only once
 * every thread has finished. Watch the three counters' output interleave.
 *
 * ------------------------------------------------------------------
 * TASK 3 — sleep + InterruptedException (the cocktail-guest pattern)
 * ------------------------------------------------------------------
 * Write a class  Guest implements Runnable  with fields: a String mumble, an int
 * pause (seconds between remarks), an int stay (total seconds to stay). Its
 * run() loops: Thread.sleep(pause * 1000L); print the mumble; accumulate time;
 * stop once it has stayed >= stay. Thread.sleep THROWS InterruptedException, so
 * wrap the loop in try/catch; in catch print "got to go"; add a finally that
 * prints "bye". In main: start 2-3 Guests with different pauses/stays and watch
 * them chatter concurrently.
 * (Use small numbers so it finishes quickly, e.g. pause 1, stay 3.)
 *
 */
