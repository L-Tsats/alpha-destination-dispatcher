/*
 * EXERCISE 10: Logical operators & complex conditions
 * ===================================================
 * (Material: Chapter 5 — &&, ||, &, |, ^, !, short-circuit evaluation,
 *            truth tables)
 *
 * KEY CONCEPTS:
 * - Logical operators combine simple true/false conditions into bigger ones.
 *
 *       &&   conditional AND  -> true only if BOTH sides are true
 *       ||   conditional OR   -> true if AT LEAST ONE side is true
 *       !    NOT              -> flips true<->false
 *
 * - SHORT-CIRCUIT evaluation (important!):
 *       &&  stops as soon as the left side is false (right side not evaluated)
 *       ||  stops as soon as the left side is true
 *   This lets you guard a risky test:
 *       if (i != 0 && 10 / i == 2)   // the division only runs when i != 0
 *   If you reversed the order you could divide by zero.
 *
 * - BOOLEAN LOGICAL operators &, |, ^ ALWAYS evaluate BOTH sides (no short
 *   circuit).  ^ is exclusive-or (XOR): true when the sides DIFFER.
 *
 * - Precedence (high -> low):  !   then &&   then ||
 *
 * TRY:
 * 1. Build a condition "age >= 18 && age <= 65" to test working age.
 * 2. Use || to accept either 'y' or 'Y' as yes.
 * 3. Show that && short-circuits (the right side's print never runs when left
 *    is false) using the helper method below.
 * 4. Print the full truth table for ^ (XOR).
 */

import java.util.Scanner;

public class LogicalOperators {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter your age: ");
        int age = input.nextInt();

        // && : both parts must hold
        boolean workingAge = age >= 18 && age <= 65;
        System.out.println("Of working age (18-65)? " + workingAge);

        // || : at least one part holds
        boolean teenagerOrSenior = age < 20 || age >= 65;
        System.out.println("Teenager-ish or senior? " + teenagerOrSenior);

        // ! : negation
        System.out.println("NOT of working age? " + (!workingAge));

        // Short-circuit demo: because (age != 0) is checked first with &&,
        // the division is safe even when age is 0.
        boolean safe = (age != 0) && (100 / age > 2);
        System.out.println("age != 0 and 100/age > 2 ? " + safe);

        // Truth table for XOR (^): true when the two operands differ
        System.out.println("\n=== XOR truth table (a ^ b) ===");
        boolean[] vals = { false, true };
        for (boolean a : vals) {
            for (boolean b : vals) {
                System.out.printf("%-5b ^ %-5b = %b%n", a, b, a ^ b);
            }
        }

        input.close();
    }
}
