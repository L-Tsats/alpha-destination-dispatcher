/*
 * EXERCISE 15: Arrays (one-dimensional) & the enhanced for
 * ========================================================
 * (Material: Chapter 7 Part A/B — array basics, new, defaults, initializer
 *            lists, length, bounds, enhanced for, frequency counting)
 *
 * KEY CONCEPTS:
 * - An ARRAY is a fixed-size group of same-typed values; it's an object:
 *       int[] c = new int[12];           // 12 ints, default 0
 *       int[] n = { 10, 20, 30 };        // initializer list, size inferred
 * - Index from 0; last index is length - 1; array has a .length field.
 * - Out-of-range index -> ArrayIndexOutOfBoundsException.
 * - Enhanced for reads each element:  for (int v : n) { ... }  (read-only use).
 * - FREQUENCY pattern: use a value as the index into a counter array.
 *
 * YOUR TASK:
 * 1. Create an int array (initializer list) and print it with an indexed for.
 * 2. Sum it with an enhanced for; print the sum and the average.
 * 3. Find and print the max and min.
 * 4. Given some dice results 1..6, tally each face into an int[7] and print the
 *    counts.
 *
 */
