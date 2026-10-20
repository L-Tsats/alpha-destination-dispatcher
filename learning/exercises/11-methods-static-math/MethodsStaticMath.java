/*
 * EXERCISE 11: Methods, static & the Math class
 * =============================================
 * (Material: Chapter 6 Part I — static methods, Math class, multiple params,
 *            the method-call stack, String concatenation)
 *
 * KEY CONCEPTS:
 * - Break work into small METHODS, each doing one well-named job.
 * - A method has a return type (void = none) and parameters:
 *       static int maximum(int a, int b, int c) { ... return result; }
 * - STATIC methods belong to the class; call them without an object:
 *       Math.max(3, 7).  main is static for the same reason.
 * - The Math class (auto-imported) has Math.max/min/abs/sqrt/pow and
 *   constants Math.PI, Math.E.
 *
 * YOUR TASK:
 * 1. Write a static method maximum(int, int, int) that returns the largest
 *    (feel free to reuse Math.max).
 * 2. Write a static method isEven(int) returning a boolean.
 * 3. In main, call maximum, call a few Math methods (max/abs/sqrt/PI), and use
 *    isEven in a loop to print the even numbers 1..10.
 *
 */
