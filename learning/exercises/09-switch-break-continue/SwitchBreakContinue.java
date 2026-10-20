/*
 * EXERCISE 09: do...while, switch, break & continue
 * =================================================
 * (Material: Chapter 5 — do...while, switch, break, continue)
 *
 * KEY CONCEPTS:
 * - do...while checks the condition AFTER the body, so the body runs at least
 *   once:  do { ... } while (condition);   (note the semicolon). Great for menus.
 * - switch chooses among cases by an int/char/String value:
 *       switch (v) { case 1: ...; break; default: ...; }
 *   Without break, execution "falls through" into the next case.
 * - break exits the nearest loop/switch; continue skips to the next iteration.
 *
 * YOUR TASK:
 * 1. Build a do...while menu loop (options + a quit option) that keeps showing
 *    the menu until the user chooses to quit.
 * 2. Use a switch to react to the chosen menu option.
 * 3. Print 1..20 but use continue to skip multiples of 3.
 * 4. Use break to stop a loop once a running sum exceeds 100.
 *
 */
