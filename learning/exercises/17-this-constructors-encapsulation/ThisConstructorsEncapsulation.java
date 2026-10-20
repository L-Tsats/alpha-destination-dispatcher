/*
 * EXERCISE 17: this, constructors, set/get & encapsulation
 * ========================================================
 * (Material: Chapter 8 — encapsulation & data hiding, the "this" reference,
 *            overloaded constructors, this(...) call, set/get, validation)
 *
 * KEY CONCEPTS:
 * - ENCAPSULATION: make fields private; expose controlled public methods so an
 *   object can't reach an inconsistent state.
 * - set/get: a validating setter (mutator) can reject bad values; a getter
 *   (accessor) returns a field.
 * - "this" is the current object. Use it when a parameter shadows a field:
 *       this.hour = hour;   // field = parameter
 * - OVERLOADED CONSTRUCTORS: multiple constructors with different parameters.
 *   this(...) as the FIRST statement calls another constructor (code reuse).
 *
 * YOUR TASK:
 * 1. Below the driver, write a Time class with private hour/minute/second.
 * 2. Give it a no-arg constructor that delegates via this(0,0,0) and a full
 *    (hour, minute, second) constructor.
 * 3. Add validating setters (reject out-of-range values) and getters, plus a
 *    toUniversalString() returning "HH:MM:SS".
 * 4. In main, create Times, show a rejected invalid value, then a valid change.
 *
 */
