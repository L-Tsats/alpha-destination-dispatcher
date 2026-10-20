/*
 * EXERCISE 13: Random numbers & simulation (dice)
 * ===============================================
 * (Material: Chapter 6 Part II — Random class, Math.random, scaling &
 *            shifting, rolling a die 6000 times, Craps idea)
 *
 * KEY CONCEPTS:
 * - Programs model chance (games, simulations) with PSEUDO-RANDOM numbers.
 *   The java.util.Random class generates them, so we import it.
 *       Random randomNumbers = new Random();
 *
 * - nextInt(n) returns an int in the range 0 .. n-1 (0 inclusive, n EXCLUSIVE).
 *   To get a die face 1..6 we SCALE and SHIFT:
 *       face = 1 + randomNumbers.nextInt(6);
 *     - 6 is the "scaling factor" (how many distinct values: 0..5)
 *     - 1 is the "shift value" (the first number of the range we want)
 *   General form:  shift + randomNumbers.nextInt(scale)
 *
 * - SEED: new Random(seed) always produces the SAME sequence. Handy while
 *   debugging so a bug is reproducible. Omit the seed for a fresh sequence
 *   each run.
 *
 * - Alternative: Math.random() returns a double in [0.0, 1.0).
 *
 * - SIMULATION pattern: roll many times and tally how often each outcome
 *   appears using an array of counters indexed by the face value.
 *
 * TRY:
 * 1. Change the die to 20-sided (D20). Fix the scaling factor.
 * 2. Roll 60000 times instead of 6000 — do the frequencies get closer to 1/6?
 * 3. Simulate rolling TWO dice and tally the sums (2..12).
 * 4. Add a fixed seed and confirm the output repeats across runs.
 */

import java.util.Random;

public class RandomSimulation {

    public static void main(String[] args) {
        Random randomNumbers = new Random();   // use new Random(42) for repeatable runs

        // A few sample rolls of a 6-sided die
        System.out.print("Six sample rolls: ");
        for (int i = 0; i < 6; i++) {
            int face = 1 + randomNumbers.nextInt(6);   // scale 6, shift 1 -> 1..6
            System.out.print(face + " ");
        }
        System.out.println();

        // Simulation: roll a die 6000 times, tally the frequency of each face.
        // frequency has 7 slots (0..6); we ignore index 0 and use 1..6.
        final int ROLLS = 6000;
        int[] frequency = new int[7];

        for (int roll = 1; roll <= ROLLS; roll++) {
            int face = 1 + randomNumbers.nextInt(6);
            frequency[face]++;   // the rolled face is itself the array index
        }

        System.out.printf("%n%-6s%-10s%-12s%n", "Face", "Count", "Percent");
        for (int face = 1; face <= 6; face++) {
            double percent = 100.0 * frequency[face] / ROLLS;
            System.out.printf("%-6d%-10d%-11.2f%%%n", face, frequency[face], percent);
        }
    }
}
