/*
 * EXERCISE 19: Inheritance (extends, super, protected)
 * ====================================================
 * (Material: Chapter 9 — superclass/subclass, extends, is-a vs has-a,
 *            protected, super, constructor chaining,
 *            CommissionEmployee / BasePlusCommissionEmployee)
 *
 * KEY CONCEPTS:
 * - A subclass absorbs a superclass's members and can add/change behaviour:
 *       class Sub extends Super { ... }
 *   Models "IS-A" (a BasePlusCommissionEmployee IS-A CommissionEmployee).
 * - Java has single inheritance; every class ultimately extends Object.
 * - private members aren't directly accessible in subclasses (use inherited
 *   get/set); protected is an in-between level; public is everywhere.
 * - A subclass constructor must call a superclass constructor FIRST via
 *   super(...) to initialize inherited fields.
 *
 * YOUR TASK:
 * 1. Write a CommissionEmployee superclass (first/last name, SSN, grossSales,
 *    commissionRate) with get/set, an earnings() method, and describe().
 * 2. Write a BasePlusCommissionEmployee subclass that adds baseSalary, calls
 *    super(...) in its constructor, and overrides earnings() to add the base
 *    salary (reuse super.earnings()).
 * 3. In main, create one of each, print earnings, then change the base salary
 *    and recompute.
 *
 */
