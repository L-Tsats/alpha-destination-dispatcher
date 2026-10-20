/*
 * EXERCISE 28: Write an Iterator FROM SCRATCH  (LADDER rung 8 of 8 — boss level)
 * ==============================================================================
 * In rung 26 your Playlist "cheated" — it returned a List's iterator. Here
 * there is NO underlying list. Your iterator GENERATES values on the fly. This
 * is the exact pattern in the Spinellis FibonacciSequence slide, and once you
 * get it, that whole example will read easily.
 *
 * THE IDEA
 * --------
 * You build TWO things:
 *   (A) a class that implements Iterable<T> and whose iterator() returns...
 *   (B) ...an Iterator<T> you wrote yourself, which COMPUTES each next value.
 *
 * The Iterator must provide:
 *   hasNext()  -> is there another value?
 *   next()     -> compute and return the next value, updating internal state
 *   remove()   -> doesn't make sense for a generated sequence, so throw:
 *                 throw new UnsupportedOperationException();
 *                 (you must still DECLARE it — it's in the interface contract)
 *
 * We'll make a FINITE counter instead of infinite Fibonacci, so your loop
 * actually terminates and is easy to verify.
 *
 * Imports:
 *       import java.util.Iterator;
 *
 * YOUR TASK
 * ---------
 * Build a class Range that represents the integers from 'start' up to (but not
 * including) 'end' — like Python's range(start, end) — WITHOUT storing them in
 * a list; the iterator produces them one at a time.
 *
 * 1. class Range implements Iterable<Integer>
 *      - fields: int start, int end (set in a constructor)
 *      - iterator() returns a new RangeIterator (your inner iterator)
 *
 * 2. an iterator that implements Iterator<Integer>:
 *      - keep a 'current' int, starting at start
 *      - hasNext(): return current < end
 *      - next():   return current, THEN increment current (return the old value)
 *      - remove(): throw new UnsupportedOperationException();
 *    (Tip: the idiomatic place for this is a private inner class inside Range,
 *     exactly like FibonacciIterator sits inside FibonacciSequence. If that's
 *     fiddly, a separate top-level class that takes start/end in its
 *     constructor is totally fine too.)
 *
 * 3. In main: create  new Range(1, 6)  and for-each over it — it should print
 *    1 2 3 4 5. Loop it a SECOND time to convince yourself a fresh iterator()
 *    starts over from the beginning.
 *
 */
