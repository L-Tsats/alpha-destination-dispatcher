/*
 * EXERCISE 22: List — a resizable, ordered sequence  (LADDER rung 2 of 8)
 * =======================================================================
 * You met arrays already (fixed size). A List is like an array that can GROW
 * and SHRINK, with handy methods. This rung is ONLY about List. Nothing else.
 *
 * THE IDEA
 * --------
 * List<E> is an INTERFACE ("an ordered sequence of E, duplicates allowed").
 * ArrayList<E> is the usual concrete class that implements it. Declare by the
 * interface, build with the class:
 *       List<String> names = new ArrayList<>();   // <> = "same type as the left"
 * The <String> is the element type (generics) — this list holds Strings.
 *
 * COMMON METHODS:
 *       names.add("x")        append to the end
 *       names.get(0)          read the element at an index (0-based, like arrays)
 *       names.size()          how many elements
 *       names.set(0, "y")     replace the element at an index
 *       names.remove(0)       remove by index  (or names.remove("x") by value)
 *       names.contains("x")   true/false membership test
 *
 * LOOPING: a List is iterable, so for-each works:
 *       for (String n : names) { ... }
 *
 * You'll need imports at the top of the file:
 *       import java.util.List;
 *       import java.util.ArrayList;
 *
 * YOUR TASK
 * ---------
 * 1. Make a List<String> of a few shopping items. ADD a duplicate on purpose
 *    (e.g. add "milk" twice) — Lists keep duplicates.
 * 2. Print the whole list with a for-each loop.
 * 3. Print size(), and the element at index 0 with get(0).
 * 4. Use contains(...) to check for an item that IS in the list and one that
 *    is NOT, printing both results.
 * 5. Replace one element with set(...), remove another, and print the list
 *    again to see it change.
 *
 */
