/*
 * EXERCISE 09: do...while, switch, break & continue
 * =================================================
 * (Material: Chapter 5 — do...while, switch multiple selection,
 *            break, continue)
 *
 * KEY CONCEPTS:
 *
 * do...while:
 *   Like while, but checks the condition AFTER the body, so the body always
 *   runs AT LEAST ONCE. Great for menus and "ask until valid" prompts.
 *       do {
 *           ...body...
 *       } while (condition);     // note the semicolon
 *
 * switch (multiple selection):
 *   Chooses among many cases based on an integer/char/String value.
 *       switch (value) {
 *           case 1:  ...; break;      // break stops fall-through
 *           case 2:  ...; break;
 *           default: ...;             // runs if nothing matched
 *       }
 *   - WITHOUT break, execution "falls through" into the next case. Sometimes
 *     that is intentional (grouping cases), but forgetting break is a common bug.
 *   - switch compares with byte, short, int, char (and String/enum).
 *
 * break:    immediately exits the nearest loop or switch.
 * continue: skips the REST of the current loop iteration and goes to the next
 *           one (in a for loop, the increment still runs).
 *
 * TRY:
 * 1. Add a menu option 4 that prints something, before "0 = quit".
 * 2. Group two case labels so A and B print the same message (fall-through).
 * 3. Use continue to skip multiples of 3 while printing 1..20.
 * 4. Use break to stop a loop as soon as a running sum exceeds 100.
 */

import java.util.Scanner;

public class SwitchBreakContinue {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // do...while menu loop — always shows the menu at least once
        int choice;
        do {
            System.out.println("\n=== Menu ===");
            System.out.println("1) Say hello");
            System.out.println("2) Show the date idea");
            System.out.println("3) Grade category (enter a 0-100 grade next)");
            System.out.println("0) Quit");
            System.out.print("Choose: ");
            choice = input.nextInt();

            // switch on the menu choice
            switch (choice) {
                case 1:
                    System.out.println("Hello!");
                    break;
                case 2:
                    System.out.println("How about learning more Java today?");
                    break;
                case 3:
                    System.out.print("Enter grade: ");
                    int grade = input.nextInt();
                    // switch on grade/10 to pick a letter bucket
                    switch (grade / 10) {
                        case 10:
                        case 9:
                            System.out.println("Category: A");
                            break;
                        case 8:
                            System.out.println("Category: B");
                            break;
                        case 7:
                            System.out.println("Category: C");
                            break;
                        case 6:
                            System.out.println("Category: D");
                            break;
                        default:
                            System.out.println("Category: F");
                    }
                    break;
                case 0:
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Unknown option, try again.");
            }
        } while (choice != 0);

        // continue: print 1..20 but skip multiples of 3
        System.out.println("\n=== 1..20 skipping multiples of 3 (continue) ===");
        for (int i = 1; i <= 20; i++) {
            if (i % 3 == 0) {
                continue; // skip the println below for this iteration
            }
            System.out.print(i + " ");
        }
        System.out.println();

        // break: stop as soon as the running sum passes 100
        System.out.println("\n=== Stop when sum exceeds 100 (break) ===");
        int sum = 0;
        for (int i = 1; ; i++) {       // no condition = would loop forever...
            sum += i;
            if (sum > 100) {
                System.out.printf("Reached %d after adding up to %d%n", sum, i);
                break;                 // ...but break ends it
            }
        }

        input.close();
    }
}
