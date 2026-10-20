/*
 * EXERCISE 25: Iterator — walking a collection by hand  (LADDER rung 5 of 8)
 * ==========================================================================
 * Every for-each loop you've written SECRETLY uses an Iterator. This rung
 * pulls back the curtain and uses it directly — which unlocks one thing a
 * for-each CAN'T safely do: remove elements while looping.
 *
 * THE IDEA
 * --------
 * An Iterator<E> is a "cursor" that walks a collection. It has three methods:
 *       it.hasNext()   is there another element?        (boolean)
 *       it.next()      give me the next element, advance (returns the element)
 *       it.remove()    delete the element next() just returned
 *
 * You get one from any collection:
 *       Iterator<String> it = names.iterator();
 *       while (it.hasNext()) {
 *           String s = it.next();
 *           ...
 *       }
 *
 * THE KEY LESSON — remove while looping:
 *   If you try to delete from a collection DURING a for-each, Java throws a
 *   ConcurrentModificationException (you changed it out from under the loop).
 *   The Iterator's OWN it.remove() is the safe, correct way to delete mid-loop.
 *
 * Imports:
 *       import java.util.List;
 *       import java.util.ArrayList;
 *       import java.util.Iterator;
 *
 * YOUR TASK
 * ---------
 * 1. Make a List<Integer> with a mix of odd and even numbers. Print it.
 * 2. Get an Iterator from it. Use a while(hasNext) loop to print each element
 *    via next() — reproducing what a for-each does, but by hand.
 * 3. Now the real goal: using an Iterator, remove every EVEN number with
 *    it.remove(). Print the list afterward — only odds should remain.
 * 4. (SEE THE BUG, optional but recommended) Before doing step 3 the right
 *    way, TRY removing inside a for-each with list.remove(n). Run it and watch
 *    the ConcurrentModificationException. Then switch to the iterator. That
 *    failure is the whole reason Iterator.remove() exists — worth seeing once.
 *
 */
