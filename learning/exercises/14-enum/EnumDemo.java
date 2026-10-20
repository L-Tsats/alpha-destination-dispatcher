/*
 * EXERCISE 14: Enumerations (enum)
 * ================================
 * (Material: Chapter 6 Part II — enum type, Status.WON/LOST/CONTINUE example)
 *
 * KEY CONCEPTS:
 * - An enum declares a fixed set of named constants:
 *       enum Status { WON, LOST, CONTINUE }
 *   A variable of that type can only hold one of those constants (safer and
 *   clearer than magic numbers 0/1/2). Write constants in ALL CAPS.
 * - enums work with switch (you omit the enum name in case labels).
 * - Useful methods: values() (all constants), name(), ordinal().
 *
 * YOUR TASK:
 * 1. Declare an enum (e.g. Status { WON, LOST, CONTINUE }) and a Day enum for
 *    the week. (You can nest them inside this class.)
 * 2. Use a switch over a Status value to print a message.
 * 3. Loop over Day.values() printing each name() and ordinal().
 * 4. Map a number 1..7 to a Day via values()[n-1] and report if it's a weekend.
 *
 */
