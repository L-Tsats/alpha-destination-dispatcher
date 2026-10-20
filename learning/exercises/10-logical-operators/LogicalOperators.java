/*
 * EXERCISE 10: Logical operators & complex conditions
 * ===================================================
 * (Material: Chapter 5 — &&, ||, &, |, ^, !, short-circuit, truth tables)
 *
 * KEY CONCEPTS:
 * - Combine conditions:  && (AND),  || (OR),  ! (NOT).
 * - SHORT-CIRCUIT: && stops if the left is false; || stops if the left is true.
 *   This lets you guard a risky test:  (i != 0 && 10 / i == 2).
 * - Boolean logical &, |, ^ always evaluate both sides; ^ is exclusive-or (XOR),
 *   true when the two sides DIFFER.
 * - Precedence:  !  then  &&  then  || .
 *
 * YOUR TASK:
 * 1. Read an age; print whether it's in the working range 18..65 using &&.
 * 2. Use || to test another combined condition.
 * 3. Print the negation (!) of one of your booleans.
 * 4. Demonstrate a short-circuit guard that avoids divide-by-zero.
 * 5. Print the full truth table for XOR (^) with nested loops over {false,true}.
 *
 */
