/*
 * EXERCISE 26: Make YOUR class work in a for-each  (LADDER rung 6 of 8)
 * =====================================================================
 * So far you've for-each'd over built-in collections. Now you'll make your OWN
 * class usable in a for-each. The secret: implement the Iterable interface.
 *
 * THE IDEA
 * --------
 * A for-each loop  for (X item : thing)  works on any 'thing' whose class
 * implements Iterable<X>. Iterable has ONE method you must provide:
 *       Iterator<X> iterator()
 * ...which hands back an Iterator (rung 5) that knows how to walk your data.
 *
 * THE EASY TRICK (used here): if your class already stores its data in a List,
 * you don't have to write an Iterator from scratch — just return the LIST's
 * own iterator. This is called "delegating".
 *
 *       class Playlist implements Iterable<String> {
 *           private final List<String> songs = new ArrayList<>();
 *           public void add(String s) { songs.add(s); }
 *           public Iterator<String> iterator() {
 *               return songs.iterator();   // delegate to the list
 *           }
 *       }
 *
 * Once that's in place, a Playlist works in a for-each exactly like a built-in
 * collection:
 *       for (String song : playlist) { ... }
 *
 * Imports:
 *       import java.util.List;
 *       import java.util.ArrayList;
 *       import java.util.Iterator;
 *
 * YOUR TASK
 * ---------
 * 1. Finish the Playlist class below so it implements Iterable<String>:
 *      - a private List<String> field to hold songs
 *      - an add(String) method
 *      - an iterator() method that returns the list's iterator
 * 2. In main, create a Playlist, add a few songs, and loop it with a for-each
 *    (for (String song : playlist)) — printing each. The fact that this
 *    compiles and runs IS the lesson: your type is now iterable.
 *
 */
