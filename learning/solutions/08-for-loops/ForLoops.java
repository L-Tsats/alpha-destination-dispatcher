/*
 * EXERCISE 08: The for loop
 * =========================
 * (Material: Chapter 5 — for repetition, compound-interest with Math.pow,
 *            printf field widths)
 *
 * KEY CONCEPTS:
 * - A for loop packs the three pieces of counter-controlled repetition onto
 *   ONE line:
 *       for ( initialization ; loopContinuationCondition ; increment )
 *           body
 *   Example:
 *       for (int i = 1; i <= 10; i++)   // start at 1, run while i<=10, +1 each pass
 *
 * - Order of execution:
 *       1. initialization runs ONCE at the start
 *       2. the condition is checked; if false, the loop ends
 *       3. the body runs
 *       4. the increment runs
 *       5. back to step 2
 *
 * - SCOPE: a variable declared in the init part (int i) exists only inside the
 *   loop. Using it after the loop is a compile error.
 *
 * - OFF-BY-ONE: to run exactly 10 times use  i <= 10  (or i < 11). Using
 *   i < 10 runs only 9 times — the classic "off-by-one" bug.
 *
 * - The increment can be any step:  i += 2  (evens),  i -= 1  (count down).
 *
 * - Math.pow(base, exp) is a STATIC method that raises base to a power and
 *   returns a double. Used below for a compound-interest table.
 *
 * TRY:
 * 1. Sum only the EVEN integers from 2 to 20 (step by 2).
 * 2. Print a countdown from 10 to 1 on one line.
 * 3. Change the interest rate to 7% and compare year-10 balances.
 * 4. Draw the bar chart for a different set of values.
 */
public class ForLoops {

    public static void main(String[] args) {
        // Basic counting
        System.out.println("=== 1 to 10 ===");
        for (int i = 1; i <= 10; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        // Summing with a step of 2 (even numbers)
        int sum = 0;
        for (int i = 2; i <= 20; i += 2) {
            sum += i;
        }
        System.out.println("\nSum of even numbers 2..20 = " + sum);

        // Compound interest with Math.pow:  a = p * (1 + r)^n
        System.out.println("\n=== Compound interest ($1000 at 5%) ===");
        double principal = 1000.0;
        double rate = 0.05;
        System.out.printf("%4s%20s%n", "Year", "Amount on deposit");
        for (int year = 1; year <= 10; year++) {
            double amount = principal * Math.pow(1.0 + rate, year);
            System.out.printf("%4d%,20.2f%n", year, amount);
        }

        // A simple bar chart made of asterisks
        System.out.println("\n=== Bar chart ===");
        int[] values = { 3, 7, 1, 5, 9 };
        for (int i = 0; i < values.length; i++) {
            System.out.printf("%2d: ", values[i]);
            for (int star = 0; star < values[i]; star++) {
                System.out.print("*");
            }
            System.out.println();
        }
    }
}
