/*
 * EXERCISE 18: static members, final constants & composition
 * ==========================================================
 * (Material: Chapter 8 — static variables/methods, final instance variables,
 *            composition / "has-a")
 *
 * KEY CONCEPTS:
 * - A STATIC field has ONE copy shared by all objects (class-wide), e.g. a
 *   counter of how many objects exist. A STATIC method is called on the class.
 * - final means a value can't change after it's set (a constant). A static
 *   final is a shared constant:  public static final String COMPANY = "...";
 * - COMPOSITION ("has-a"): a class holds references to other objects as fields
 *   (e.g. an Employee HAS-A Date). Different from inheritance ("is-a").
 *
 * YOUR TASK:
 * 1. Write a small Date class (month/day/year) with a toString().
 * 2. Write an Employee class with: a private final id, a name, a Date birthDate
 *    (composition), a shared static counter, a public static final COMPANY, a
 *    static getCount(), and a describe() method.
 * 3. In main, create a few Employees and show the shared count rising; print
 *    COMPANY via the class name (no object needed).
 *
 */
