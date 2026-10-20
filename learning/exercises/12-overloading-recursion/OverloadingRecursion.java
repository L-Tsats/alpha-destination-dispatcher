/*
 * EXERCISE 12: Method overloading & recursion
 * ===========================================
 * (Material: Chapter 6 Part II — method overloading, recursion)
 *
 * KEY CONCEPTS:
 * - OVERLOADING: several methods with the SAME name but DIFFERENT parameter
 *   lists; the compiler picks by the arguments. Return type alone can't tell
 *   overloads apart.
 * - RECURSION: a method that calls itself on a smaller input. Needs a BASE CASE
 *   that stops it and a RECURSIVE CASE that moves toward the base.
 *       factorial: 0! = 1 ; n! = n * (n-1)!
 *       fibonacci: fib(0)=0, fib(1)=1 ; fib(n)=fib(n-1)+fib(n-2)
 *
 * YOUR TASK:
 * 1. Write two overloads: square(int) returning int, square(double) returning
 *    double. Show the compiler picks the right one for 5 vs 5.0.
 * 2. Write a recursive factorial(int) returning long; print 0!..6!.
 * 3. Write a recursive fibonacci(int); print the first 10 Fibonacci numbers.
 *
 */
