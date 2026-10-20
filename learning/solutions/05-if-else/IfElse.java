/*
 * EXERCISE 05: Decisions — if, if/else, nested if/else, ternary
 * =============================================================
 * (Material: Chapter 2 Part B — relational/equality operators;
 *            Chapter 4 — if, if/else, nested if/else, ?: , blocks)
 *
 * KEY CONCEPTS:
 * - A CONDITION is an expression that is either true or false.
 * - RELATIONAL & EQUALITY operators build conditions:
 *       >   <   >=   <=        (greater / less / or-equal)
 *       ==  equal to           (two equals signs! not one)
 *       !=  not equal to
 *   WATCH OUT:  =  assigns a value,  ==  compares. Mixing them is a classic bug.
 *
 * - if: do something only when the condition is true.
 *       if (grade >= 60)
 *           System.out.println("Passed");
 *
 * - if/else: choose between two paths.
 *       if (grade >= 60) ... else ...
 *
 * - BLOCKS: to run MORE than one statement, wrap them in braces { }.
 *   Without braces, an if controls only the single next statement.
 *
 * - NESTED if/else (the "else if" ladder) picks one of many ranges:
 *       if (g >= 90) 'A' else if (g >= 80) 'B' else ... else 'F'
 *
 * - TERNARY operator ?:  — a compact if/else that produces a value:
 *       String result = (grade >= 60) ? "Passed" : "Failed";
 *   Read it as: condition ? valueIfTrue : valueIfFalse
 *
 * TRY:
 * 1. Change the grade and watch which letter prints.
 * 2. Add a check for 'A+' (>= 97) at the top of the ladder.
 * 3. Rewrite the pass/fail ternary as a full if/else and confirm same output.
 * 4. Print whether a number read from input is even or odd (hint: % 2 == 0).
 */

import java.util.Scanner;

public class IfElse {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter a grade (0-100): ");
        int grade = input.nextInt();

        // Single-selection if + a block in the else
        if (grade >= 60) {
            System.out.println("You passed.");
        } else {
            System.out.println("You failed.");
            System.out.println("You must take this course again.");
        }

        // Nested if/else ladder: letter grade
        char letter;
        if (grade >= 90) {
            letter = 'A';
        } else if (grade >= 80) {
            letter = 'B';
        } else if (grade >= 70) {
            letter = 'C';
        } else if (grade >= 60) {
            letter = 'D';
        } else {
            letter = 'F';
        }
        System.out.printf("Letter grade: %c%n", letter);

        // Ternary operator: condition ? valueIfTrue : valueIfFalse
        String passFail = (grade >= 60) ? "PASS" : "FAIL";
        System.out.printf("Result: %s%n", passFail);

        // Even / odd using the remainder operator
        System.out.print("\nEnter a number to test even/odd: ");
        int n = input.nextInt();
        System.out.printf("%d is %s%n", n, (n % 2 == 0) ? "even" : "odd");

        input.close();
    }
}
