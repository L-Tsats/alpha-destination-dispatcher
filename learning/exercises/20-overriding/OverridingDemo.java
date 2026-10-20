/*
 * EXERCISE 20: Overriding, @Override & toString
 * =============================================
 * (Material: Chapter 9 — overriding, @Override, the Object class, toString,
 *            calling super's version)
 *
 * KEY CONCEPTS:
 * - OVERRIDING: a subclass redefines an inherited method with the SAME
 *   signature; the subclass's version runs for subclass objects. (Different
 *   from overloading, which changes the parameters.)
 * - @Override tells the compiler to verify you really are overriding — it
 *   catches typos in the signature.
 * - Every class inherits toString() from Object; override it to return a
 *   readable description. It's called automatically by println / string concat.
 * - super.method() calls the superclass version from inside the override.
 *
 * YOUR TASK:
 * 1. Write an Animal class (name, age) with a speak() method and an overridden
 *    toString().
 * 2. Write a Dog subclass that overrides speak() (e.g. "Woof") and overrides
 *    toString() by calling super.toString() and appending the breed.
 * 3. In main, print an Animal and a Dog (toString runs automatically), call
 *    speak() on each, and show that a Dog referenced as an Animal still runs
 *    the Dog's overridden speak().
 *
 */
