/*
 * EXERCISE 07: Repetition with while — counter & sentinel loops
 * =============================================================
 * (Material: Chapter 4 — while, counter-controlled & sentinel-controlled
 *            repetition, class-average problem)
 *
 * KEY CONCEPTS:
 * - while repeats WHILE a condition is true; it checks FIRST, so it may run zero
 *   times. Change the control variable inside, or you get an infinite loop.
 * - COUNTER-CONTROLLED (you know the count): use a counter 1..N.
 * - SENTINEL-CONTROLLED (unknown count): a special value (e.g. -1) marks the end.
 *   Read one value BEFORE the loop, then read the next at the END of the body.
 * - A "total" starts at 0; guard against divide-by-zero; cast to double for an
 *   accurate average.
 *
 * YOUR TASK:
 * 1. Print the numbers 1..10 with a counter-controlled while loop.
 * 2. Read grades until the user enters -1 (sentinel). Accumulate total & count.
 * 3. Print how many grades were entered and their average (avoid integer
 *    division; guard the no-grades case).
 *
 */
