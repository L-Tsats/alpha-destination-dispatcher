/*
 * EXERCISE 12: Method overloading & recursion
 * ===========================================
 * (Material: Chapter 6 Part II — method overloading, recursion
 *            (factorial, Fibonacci))
 *
 * KEY CONCEPTS:
 *
 * OVERLOADING:
 * - Several methods can share the SAME NAME in the same class, as long as their
 *   PARAMETER LISTS differ (by number, types, or order). This is the method's
 *   "signature". The compiler picks the right one from the arguments you pass.
 *       square(5)     -> calls square(int)
 *       square(5.0)   -> calls square(double)
 * - The RETURN TYPE alone is NOT enough to tell overloads apart — two methods
 *   with the same signature but different return types won't compile.
 *
 * RECURSION:
 * - A recursive method calls ITSELF to solve a smaller version of the problem.
 * - Every recursive method needs:
 *       1) a BASE CASE that stops the recursion (no further call), and
 *       2) a RECURSIVE CASE that moves toward the base case.
 * - factorial:  0! = 1 (base),  n! = n * (n-1)! (recursive)
 * - Fibonacci:  fib(0)=0, fib(1)=1 (bases),  fib(n)=fib(n-1)+fib(n-2)
 * - Each call is pushed on the method-call stack; missing/incorrect base cases
 *   cause infinite recursion -> StackOverflowError.
 *
 * TRY:
 * 1. Add a third overload  square(long x)  and call it with 7L.
 * 2. Trace factorial(4) on paper, then confirm with the program.
 * 3. Add an iterative (loop-based) factorial and compare results.
 * 4. Print the first 10 Fibonacci numbers using the recursive fib.
 */
public class OverloadingRecursion {

    public static void main(String[] args) {
        // Overloading: same name, chosen by argument type
        System.out.println("square(5)   = " + square(5));     // int version
        System.out.println("square(5.0) = " + square(5.0));   // double version

        // Recursion: factorial
        System.out.println("\n=== Factorials ===");
        for (int n = 0; n <= 6; n++) {
            System.out.printf("%d! = %d%n", n, factorial(n));
        }

        // Recursion: Fibonacci
        System.out.print("\nFirst 10 Fibonacci numbers: ");
        for (int i = 0; i < 10; i++) {
            System.out.print(fibonacci(i) + " ");
        }
        System.out.println();
    }

    // Overload #1: operates on int, returns int
    static int square(int x) {
        return x * x;
    }

    // Overload #2: operates on double, returns double (different parameter type)
    static double square(double x) {
        return x * x;
    }

    // Recursive factorial:  n! = n * (n-1)! , with 0! = 1 as the base case
    static long factorial(int n) {
        if (n <= 1) {       // base case
            return 1;
        }
        return n * factorial(n - 1);   // recursive case
    }

    // Recursive Fibonacci: fib(0)=0, fib(1)=1, else fib(n-1)+fib(n-2)
    static long fibonacci(int n) {
        if (n < 2) {        // base cases: fib(0)=0, fib(1)=1
            return n;
        }
        return fibonacci(n - 1) + fibonacci(n - 2);
    }
}
