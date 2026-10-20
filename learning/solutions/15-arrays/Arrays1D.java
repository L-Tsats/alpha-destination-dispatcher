/*
 * EXERCISE 15: Arrays (one-dimensional) & the enhanced for
 * ========================================================
 * (Material: Chapter 7 Part A/B — array basics, new, default values,
 *            initializer lists, length, bounds checking, enhanced for,
 *            frequency counting)
 *
 * KEY CONCEPTS:
 * - An ARRAY is a fixed-size group of variables of the SAME type, stored
 *   together. Arrays are OBJECTS (reference types), created with "new":
 *       int[] c = new int[12];     // 12 ints, all default to 0
 *   Default values when created: numeric -> 0, boolean -> false, references -> null.
 *
 * - INDEXING: elements are numbered from 0. The last valid index is
 *   length - 1. Access with square brackets:  c[0], c[1], ...
 *       c[0] = 5;              // store
 *       int x = c[0];          // read
 *
 * - LENGTH: every array knows its own size via the  .length  field (not a
 *   method — no parentheses). It is final; an array cannot grow.
 *
 * - INITIALIZER LIST: create and fill in one step; the size is inferred:
 *       int[] n = { 10, 20, 30, 40, 50 };   // length 5, indices 0..4
 *
 * - BOUNDS CHECKING: using an index < 0 or >= length throws an
 *   ArrayIndexOutOfBoundsException at run time. The JVM protects you.
 *
 * - ENHANCED for ("for-each"): iterate every element without an index.
 *       for (int value : n) { ... }
 *   Read-only in effect: assigning to the loop variable does NOT change the
 *   array. Use a classic indexed for when you need to modify elements.
 *
 * - FREQUENCY pattern: use the data value itself as an index into a counter
 *   array to tally occurrences.
 *
 * TRY:
 * 1. Print the array both with an indexed for and with an enhanced for.
 * 2. Find the maximum and minimum values in the array.
 * 3. Deliberately access index 99 to see the exception, then remove it.
 * 4. Count how many values are even using a counter.
 */
public class Arrays1D {

    public static void main(String[] args) {
        // Initializer list: size inferred as 5
        int[] numbers = { 10, 25, 3, 42, 17 };

        // Indexed for: we have access to the position i
        System.out.println("=== Indexed for ===");
        for (int i = 0; i < numbers.length; i++) {
            System.out.printf("numbers[%d] = %d%n", i, numbers[i]);
        }

        // Enhanced for: cleaner when you just need the values
        System.out.println("\n=== Enhanced for (sum) ===");
        int sum = 0;
        for (int value : numbers) {
            sum += value;
        }
        System.out.println("Sum = " + sum);
        System.out.printf("Average = %.2f%n", (double) sum / numbers.length);

        // Find max and min
        int max = numbers[0];
        int min = numbers[0];
        for (int value : numbers) {
            if (value > max) max = value;
            if (value < min) min = value;
        }
        System.out.printf("Max = %d, Min = %d%n", max, min);

        // Frequency counting: roll results 1..6 tallied with a counter array
        System.out.println("\n=== Frequency counting ===");
        int[] rolls = { 1, 3, 3, 6, 2, 3, 5, 6, 1, 4, 6 };
        int[] frequency = new int[7];     // indices 0..6, we use 1..6
        for (int r : rolls) {
            frequency[r]++;               // the value is the index
        }
        for (int face = 1; face <= 6; face++) {
            System.out.printf("Face %d appeared %d time(s)%n", face, frequency[face]);
        }
    }
}
