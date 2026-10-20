/*
 * EXERCISE 04: Reading input with Scanner
 * =======================================
 * (Material: Chapter 2 Part B — Scanner, nextInt; Chapter 3 — reading a String)
 *
 * KEY CONCEPTS:
 * - To read keyboard input, create a Scanner on System.in:
 *       Scanner input = new Scanner(System.in);
 *   You must import it:  import java.util.Scanner;  (java.util is NOT auto-imported).
 * - Reading methods (each waits for Enter):
 *       input.nextInt()     an int
 *       input.nextDouble()  a double
 *       input.next()        one word
 *       input.nextLine()    the rest of the line
 * - GOTCHA: nextInt()/nextDouble() leave the newline in the buffer; a following
 *   nextLine() reads that leftover. Consume it with an extra nextLine() if needed.
 *
 * YOUR TASK:
 * 1. Import Scanner and create one on System.in.
 * 2. Ask for the user's name (a word) and two integers.
 * 3. Print a greeting and the sum of the two integers.
 * 4. Ask for a price (double) and quantity (int); print the total.
 * 5. Close the Scanner at the end.
 *
 */
