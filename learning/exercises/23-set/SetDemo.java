/*
 * EXERCISE 23: Set — a collection with NO duplicates  (LADDER rung 3 of 8)
 * ========================================================================
 * Almost identical to List, with ONE defining difference: a Set refuses
 * duplicates. Adding the same value twice has no effect the second time.
 *
 * THE IDEA
 * --------
 * Set<E> is an INTERFACE ("a group of UNIQUE elements, no guaranteed order").
 * HashSet<E> is the usual implementation:
 *       Set<String> tags = new HashSet<>();
 *
 * COMMON METHODS (mostly the same as List, but NO get(index) — a Set has no
 * positions/order):
 *       tags.add("x")        add (ignored if "x" already present)
 *       tags.size()          how many UNIQUE elements
 *       tags.contains("x")   membership test (this is what Sets are great at)
 *       tags.remove("x")     remove by value
 *
 * Two things you'll SEE:
 *   - a duplicate add doesn't grow the set
 *   - the print order is NOT the insertion order (HashSet is unordered).
 *     (If you ever need sorted order, TreeSet does that — not required here.)
 *
 * Imports:
 *       import java.util.Set;
 *       import java.util.HashSet;
 *       import java.util.List;        // only if you build it from a List
 *       import java.util.ArrayList;
 *
 * YOUR TASK
 * ---------
 * 1. Make a Set<String> and add several items, including a DUPLICATE.
 * 2. Print the set and its size(). Confirm the duplicate collapsed to one.
 * 3. Use contains(...) for a present and an absent item.
 * 4. BONUS (ties back to rung 2): make a List<String> WITH duplicates, then
 *    build a Set from it in one line:  var unique = new HashSet<>(myList);
 *    Print both and compare the sizes — the Set is the "de-duplicated" view.
 *
 */
