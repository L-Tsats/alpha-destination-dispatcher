/*
 * EXERCISE 11: Methods, static & the Math class
 * =============================================
 * (Material: Chapter 6 Part I — static methods, Math class, multiple
 *            parameters, the method-call stack, String concatenation)
 *
 * KEY CONCEPTS:
 * - "Divide and conquer": break a big program into small METHODS, each doing
 *   one well-named job. Methods can be reused and tested independently.
 *
 * - A METHOD takes zero or more PARAMETERS and may RETURN a value:
 *       static int maximum(int a, int b, int c) { ... return result; }
 *   - The return TYPE comes before the name (int here). void = returns nothing.
 *   - Each ARGUMENT you pass must match the parameter's type and position.
 *
 * - STATIC methods belong to the CLASS, not to an object. You can call them
 *   without creating an object:
 *       ClassName.methodName(args)      e.g.  Math.max(3, 7)
 *   main is static for exactly this reason: the JVM calls it before any object
 *   of your class exists.
 *   RULE: a static method can call other static methods / use static fields of
 *   the same class directly, but needs an object to reach non-static members.
 *
 * - The Math class (in java.lang, auto-imported) is full of static helpers:
 *       Math.max, Math.min, Math.abs, Math.sqrt, Math.pow, Math.random,
 *       and constants Math.PI, Math.E  (final = cannot change).
 *
 * - The method-call stack (LIFO): when main calls a method, the call is
 *   "pushed"; when it returns, it is "popped". Too many nested calls (e.g. a
 *   runaway recursion) overflows the stack -> StackOverflowError.
 *
 * TRY:
 * 1. Add a method  static int sum(int a, int b, int c)  and call it.
 * 2. Reimplement maximum using Math.max( a, Math.max(b, c) ).
 * 3. Print Math.sqrt(2), Math.abs(-5), Math.PI with 4 decimals.
 * 4. Write  static boolean isEven(int n)  and use it in a loop.
 */
public class MethodsStaticMath {

    public static void main(String[] args) {
        // Call our own static method with three arguments
        int biggest = maximum(27, 83, 42);
        System.out.println("maximum(27, 83, 42) = " + biggest);

        // Reuse Math's static methods
        System.out.println("Math.max(10, 20)    = " + Math.max(10, 20));
        System.out.println("Math.abs(-5)        = " + Math.abs(-5));
        System.out.printf("Math.sqrt(2)        = %.5f%n", Math.sqrt(2));
        System.out.printf("Math.PI             = %.4f%n", Math.PI);

        // Use a boolean-returning helper in a loop
        System.out.print("\nEven numbers 1..10: ");
        for (int i = 1; i <= 10; i++) {
            if (isEven(i)) {
                System.out.print(i + " ");
            }
        }
        System.out.println();
    }

    /*
     * Returns the largest of three int values.
     * Note how it reuses Math.max instead of writing all the if-logic by hand.
     */
    static int maximum(int a, int b, int c) {
        return Math.max(a, Math.max(b, c));
    }

    // A small reusable helper: true when n is even
    static boolean isEven(int n) {
        return n % 2 == 0;
    }
}
