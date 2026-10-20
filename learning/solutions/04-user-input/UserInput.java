/*
 * EXERCISE 04: Reading input with Scanner
 * =======================================
 * (Material: Chapter 2 Part B — Scanner, nextInt; Chapter 3 — reading a String)
 *
 * KEY CONCEPTS:
 * - To read what the user types, Java uses a Scanner object connected to
 *   the standard input (the keyboard), which is System.in:
 *       Scanner input = new Scanner(System.in);
 *   - The keyword "new" creates (constructs) a new Scanner object.
 *   - You must IMPORT it at the top:  import java.util.Scanner;
 *     (Scanner lives in the java.util package, which is NOT auto-imported —
 *      unlike java.lang, which gives us System and String for free.)
 *
 * - Reading methods (each waits for the user to type and press Enter):
 *       int n     = input.nextInt();      // reads an int
 *       double d  = input.nextDouble();   // reads a double
 *       String w  = input.next();         // reads ONE word (up to a space)
 *       String ln = input.nextLine();     // reads the WHOLE rest of the line
 *
 * - Assignment reads right-to-left: the right side is evaluated first,
 *   then stored into the variable on the left.
 *       number1 = input.nextInt();   // "number1 takes the value typed"
 *
 * TRY:
 * 1. Ask for two integers and print their sum, difference and product.
 * 2. Ask the user's name with next() and greet them by name.
 * 3. Ask for a price (double) and a quantity (int), print the total cost.
 * 4. BONUS: notice the next()/nextLine() mixing gotcha explained below.
 */

import java.util.Scanner;

public class UserInput {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // Read a single word (no spaces)
        System.out.print("Enter your name: ");
        String name = input.next();

        // Read two integers
        System.out.print("Enter the first integer: ");
        int number1 = input.nextInt();

        System.out.print("Enter the second integer: ");
        int number2 = input.nextInt();

        int sum = number1 + number2;

        System.out.printf("%nHi %s! %d + %d = %d%n", name, number1, number2, sum);

        // Read a double
        System.out.print("\nEnter a price: ");
        double price = input.nextDouble();

        System.out.print("Enter a quantity: ");
        int quantity = input.nextInt();

        System.out.printf("Total cost: %.2f%n", price * quantity);

        /*
         * GOTCHA (worth knowing): nextInt()/nextDouble() read the number but
         * leave the Enter key (newline) sitting in the input buffer. If you
         * call nextLine() right after, it reads that leftover empty line.
         * The fix is to call an extra input.nextLine() to consume it before
         * reading a full line of text. Try it out if you're curious.
         */

        input.close();
    }
}
