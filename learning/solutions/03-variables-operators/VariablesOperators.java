/*
 * EXERCISE 03: Variables, arithmetic & operator precedence
 * ========================================================
 * (Material: Chapter 2 Part B — variables & arithmetic;
 *            Chapter 4 — integer division, ++/--, casting)
 *
 * KEY CONCEPTS:
 * - A VARIABLE is a named box in memory that holds a value of a given TYPE.
 *   You must declare it with a type and a name before using it:
 *       int number1;        // declaration
 *       number1 = 7;        // assignment
 *       int number2 = 42;   // declare + initialize in one line
 * - Common primitive types:
 *       int     whole numbers,  range about -2.1 billion .. +2.1 billion
 *       double  numbers with a decimal point (3.14, -0.5)
 *       char    a single character  'A'
 *       boolean  true / false
 * - ARITHMETIC OPERATORS:  +  -  *  /  %
 *       %  is the remainder (modulus): 7 % 3 == 1
 *
 * INTEGER DIVISION — a classic trap:
 *   When BOTH operands are int, / throws away the fractional part.
 *       7 / 2   ==  3     (NOT 3.5 — the .5 is truncated, not rounded)
 *   To get 3.5 you need at least one double, e.g.  7 / 2.0  or (double) 7 / 2
 *
 * CASTING:  (double) x  temporarily converts x to a double for the calc.
 *
 * PRECEDENCE (order of evaluation):
 *   1) *  /  %   (left to right)
 *   2) +  -      (left to right)
 *   Parentheses () override the order.  2 + 3 * 4 == 14,  (2 + 3) * 4 == 20
 *
 * ++ and -- :  i++ means i = i + 1 ;  i-- means i = i - 1
 * Compound assignment:  x += 5  is short for  x = x + 5
 *
 * TRY:
 * 1. Predict the output of  7 / 2  and  7 % 2  before running. Were you right?
 * 2. Fix the integer-division line so avg prints 3.5 instead of 3.0.
 * 3. Add parentheses to  2 + 3 * 4  to make it print 20.
 * 4. Declare a double price, apply a 10% discount using  price *= 0.9.
 */
public class VariablesOperators {

    public static void main(String[] args) {
        int number1 = 7;
        int number2 = 2;

        int sum = number1 + number2;
        int difference = number1 - number2;
        int product = number1 * number2;
        int quotient = number1 / number2;   // integer division -> 3
        int remainder = number1 % number2;  // remainder        -> 1

        System.out.printf("sum        = %d%n", sum);
        System.out.printf("difference = %d%n", difference);
        System.out.printf("product    = %d%n", product);
        System.out.printf("quotient   = %d  (integer division truncates!)%n", quotient);
        System.out.printf("remainder  = %d%n", remainder);

        // Integer-division trap: this prints 3.0, not 3.5, because 7/2 is done
        // as INT first (=3) and only then stored into a double.
        double avgWrong = number1 / number2;
        System.out.printf("%navg (wrong) = %.1f%n", avgWrong);

        // Fixed: cast one operand to double so the division is floating-point.
        double avgRight = (double) number1 / number2;
        System.out.printf("avg (right) = %.1f%n", avgRight);

        // Precedence
        System.out.println("\n=== Precedence ===");
        System.out.printf("2 + 3 * 4   = %d%n", 2 + 3 * 4);     // 14
        System.out.printf("(2 + 3) * 4 = %d%n", (2 + 3) * 4);   // 20

        // ++ and compound assignment
        System.out.println("\n=== Increment / compound ===");
        int count = 10;
        count++;              // 11
        count += 5;           // 16
        System.out.printf("count after ++ then += 5 = %d%n", count);
    }
}
