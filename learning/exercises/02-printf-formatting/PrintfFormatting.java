/*
 * EXERCISE 02: Formatted output with printf
 * =========================================
 * (Material: Chapter 2 Part A — printf; Chapter 5 — field width & precision)
 *
 * KEY CONCEPTS:
 * - System.out.printf prints FORMATTED text. The first argument is a FORMAT
 *   STRING containing "format specifiers" (placeholders) filled by the values
 *   listed after it, in order.
 *       %s   a String      %d   an int      %f   a double (6 decimals default)
 *       %n   a newline (portable)
 *   Example:  printf("%s is %d%n", name, age);
 *
 * WIDTH & PRECISION:
 *       %5d    width 5, right-aligned      %-5d   width 5, left-aligned
 *       %.2f   2 decimals                  %8.2f  width 8, 2 decimals
 *       %,d    thousands separators        %05d   zero-padded to width 5
 *
 * YOUR TASK:
 * 1. Print your name and age on one line using %s and %d.
 * 2. Print a double (e.g. a GPA) to 2 decimals with %.2f.
 * 3. Print the same int right-aligned, left-aligned, and zero-padded (width 8).
 * 4. Print a small price table aligned with %-10s and %8.2f.
 * 5. Print a large number with thousands separators using %,d.
 *
 */
