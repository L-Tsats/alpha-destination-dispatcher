/*
 * EXERCISE 07: Repetition with while — counter & sentinel loops
 * =============================================================
 * (Material: Chapter 4 — while, counter-controlled repetition,
 *            sentinel-controlled repetition, class-average problem)
 *
 * KEY CONCEPTS:
 * - A while loop repeats its body WHILE a condition stays true. It checks the
 *   condition FIRST, so the body may run zero times.
 *       while (condition) {
 *           ...body...
 *       }
 * - If nothing in the body ever makes the condition false, you get an
 *   INFINITE LOOP. Always change the control variable inside the loop.
 *
 * TWO CLASSIC PATTERNS FROM THE MATERIAL:
 *
 * 1) COUNTER-CONTROLLED (definite) repetition:
 *    You know in advance how many times to loop. Use a counter.
 *        int counter = 1;
 *        while (counter <= 10) { ...; counter++; }
 *
 * 2) SENTINEL-CONTROLLED (indefinite) repetition:
 *    You DON'T know how many values are coming. A special "sentinel" value
 *    marks the end of input (here: -1). Read one value BEFORE the loop, then
 *    read the next value at the END of the loop body.
 *
 * - A "total" accumulates a sum and starts at 0; a "counter" counts items.
 * - Beware integer division when computing an average: cast to double.
 *
 * TRY:
 * 1. Change the counter loop to print 1..20.
 * 2. Make the counter loop count DOWN from 10 to 1.
 * 3. Change the sentinel from -1 to 0 (and the prompt) and test it.
 * 4. Print how many numbers were entered, not just the average.
 */

import java.util.Scanner;

public class WhileLoops {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // 1) COUNTER-CONTROLLED: print the numbers 1..10
        System.out.println("=== Counting 1 to 10 (counter-controlled) ===");
        int counter = 1;
        while (counter <= 10) {
            System.out.print(counter + " ");
            counter++;
        }
        System.out.println();

        // 2) SENTINEL-CONTROLLED: average an unknown number of grades.
        //    The sentinel value -1 means "no more grades".
        System.out.println("\n=== Class average (sentinel-controlled) ===");
        System.out.println("Enter grades one at a time. Enter -1 to finish.");

        int total = 0;         // accumulates the sum
        int gradeCounter = 0;  // counts how many grades were entered

        System.out.print("Enter grade (-1 to end): ");
        int grade = input.nextInt();   // read the FIRST value before the loop

        while (grade != -1) {          // loop until the sentinel is seen
            total += grade;
            gradeCounter++;
            System.out.print("Enter grade (-1 to end): ");
            grade = input.nextInt();   // read the NEXT value at the end
        }

        // Guard against division by zero if no grades were entered
        if (gradeCounter != 0) {
            double average = (double) total / gradeCounter;  // cast avoids int division
            System.out.printf("%nGrades entered: %d%n", gradeCounter);
            System.out.printf("Class average : %.2f%n", average);
        } else {
            System.out.println("\nNo grades were entered.");
        }

        input.close();
    }
}
