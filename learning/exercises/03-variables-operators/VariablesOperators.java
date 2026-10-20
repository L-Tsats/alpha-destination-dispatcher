/*
 * EXERCISE 03: Variables, arithmetic & operator precedence
 * ========================================================
 * (Material: Chapter 2 Part B; Chapter 4 — integer division, ++/--, casting)
 *
 * KEY CONCEPTS:
 * - Declare a variable with a TYPE and a NAME:  int x = 7;
 *   Common types: int, double, char, boolean.
 * - Arithmetic operators:  +  -  *  /  %   (% is remainder)
 * - INTEGER DIVISION trap: when BOTH operands are int, / truncates:
 *       7 / 2 == 3   (NOT 3.5).  Use (double) to force real division.
 * - PRECEDENCE:  * / %  before  + -  ; parentheses override.
 * - ++ / -- increment/decrement; compound assignment:  x += 5.
 *
 * YOUR TASK:
 * 1. Declare two ints; print their sum, difference, product, quotient, remainder.
 * 2. Show the integer-division trap, then fix it with a cast so you get a decimal.
 * 3. Print 2 + 3 * 4 and (2 + 3) * 4 to see precedence change the result.
 * 4. Use ++ and += on a variable and print the result.
 *
 */
