/*
 * EXERCISE 02: Formatted output with printf
 * =========================================
 * (Material: Chapter 2 Part A — printf; Chapter 5 — field width & precision)
 *
 * KEY CONCEPTS:
 * - System.out.printf prints FORMATTED text. The "f" means "formatted".
 * - The first argument is a FORMAT STRING. It can contain plain text plus
 *   "format specifiers" that are placeholders for values.
 * - A format specifier starts with % and ends with a conversion letter:
 *       %s   a String
 *       %d   a decimal integer (int)
 *       %f   a floating-point number (double) — defaults to 6 decimals
 *       %n   a new line (portable; prefer it over \n inside printf)
 * - The values that fill the placeholders are listed after the format string,
 *   separated by commas, in order.
 *       printf("%s is %d years old%n", name, age);
 *
 * WIDTH & PRECISION (very common on exams):
 *       %5d     at least 5 characters wide, right-aligned
 *       %-5d    at least 5 wide, LEFT-aligned (the minus flag)
 *       %.2f    2 digits after the decimal point
 *       %8.2f   8 wide total, 2 decimals, right-aligned
 *       %,d     group thousands with separators (1,000,000)
 *       %05d    pad with leading zeros to width 5 (00042)
 *
 * TRY:
 * 1. Print your name and age on one line using %s and %d.
 * 2. Print the price 3.5 as "3.50" using %.2f.
 * 3. Line up a small table of items + prices using %-10s and %8.2f.
 * 4. Print 1234567 with thousands separators using %,d.
 */
public class PrintfFormatting {

    public static void main(String[] args) {
        String name = "Tassos";
        int age = 20;
        double gpa = 8.5;

        // Basic placeholders
        System.out.printf("%s is %d years old.%n", name, age);
        System.out.printf("GPA: %.2f%n", gpa);

        // Width and alignment
        System.out.println("\n=== Right vs left aligned (width 8) ===");
        System.out.printf("[%8d]%n", 42);    // right aligned
        System.out.printf("[%-8d]%n", 42);   // left aligned
        System.out.printf("[%08d]%n", 42);   // zero padded

        // A neatly aligned price table
        System.out.println("\n=== Price list ===");
        System.out.printf("%-10s %8.2f%n", "Coffee", 2.5);
        System.out.printf("%-10s %8.2f%n", "Sandwich", 3.75);
        System.out.printf("%-10s %8.2f%n", "Juice", 1.2);

        // Thousands separator
        System.out.println("\n=== Big number ===");
        System.out.printf("Population: %,d%n", 1234567);
    }
}
