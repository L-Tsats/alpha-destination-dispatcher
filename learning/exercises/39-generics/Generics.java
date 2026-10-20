/*
 * EXERCISE 39: Generic classes  (parametric types)
 * =================================================
 * You've USED generics (List<String>) and WRITTEN generic methods (rung 27).
 * Now you'll WRITE GENERIC CLASSES — classes that take a type parameter, so one
 * class definition works for many types, decided at compile time.
 *
 * THE IDEA
 * --------
 * A class with <T> after its name has a "type parameter" T that stands in for a
 * real type chosen when you use the class:
 *       class Box<T> {
 *           private T value;
 *           Box(T v) { value = v; }
 *           T get() { return value; }
 *       }
 *       Box<String> b = new Box<>("hi");   // here T = String
 *       Box<Integer> n = new Box<>(42);    // here T = Integer
 * One class, any element type, with full type-safety (no casting).
 *
 * A class can take MORE THAN ONE type parameter: class Pair<A, B> { ... }.
 * And a generic class can refer to ITSELF generically (a recursive structure
 * like a linked list), which is how generic data structures are built.
 *
 * Declare the helper classes at TOP LEVEL in this file (outside the Generics
 * class), like the Spinellis material does.
 *
 * ------------------------------------------------------------------
 * TASK 1 — Box<T> (one type parameter)
 * ------------------------------------------------------------------
 * Write a class  Box<T>  with:
 *   - a private field  T value;
 *   - a constructor  Box(T value)
 *   - a getter  T get()
 *   - a setter  void set(T value)
 *   - a toString() returning "Box(" + value + ")"
 * In main: make a Box<String> and a Box<Integer>, get/set them, print them.
 *
 * ------------------------------------------------------------------
 * TASK 2 — Pair<A, B> (two type parameters)
 * ------------------------------------------------------------------
 * Write a class  Pair<A, B>  holding two values of (possibly) different types:
 *   - private final A first;  private final B second;
 *   - constructor  Pair(A first, B second)
 *   - getters  A getFirst()  and  B getSecond()
 *   - toString() returning "(" + first + ", " + second + ")"
 * In main: make e.g. a Pair<String, Integer> ("age", 25) and print it, and a
 * Pair<Integer, Integer> and print it.
 *
 * ------------------------------------------------------------------
 * TASK 3 — LinkedList<E> (a recursive generic structure)
 * ------------------------------------------------------------------
 * Build a tiny immutable singly-linked list, generic over its element type E:
 *   class LinkedList<E> {
 *       private E value;
 *       private LinkedList<E> next;        // refers to its OWN type generically
 *       LinkedList(E v) { value = v; next = null; }
 *       // add: return a NEW list node holding v, pointing at THIS as its next
 *       LinkedList<E> add(E v) { ... }
 *       // toString: "value -> value -> value" (recurse through next)
 *   }
 * In main: start a LinkedList<Integer> with 0, add a few numbers, and print it.
 * (Tip for add: make a new node, set its next = this, return it. For toString,
 *  if next == null return value.toString(), else value + " -> " + next.)
 *
 */
