/*
 * EXERCISE 18: static members, final constants & composition
 * ==========================================================
 * (Material: Chapter 8 — static variables/methods, final instance variables,
 *            composition / "has-a" relationship)
 *
 * KEY CONCEPTS:
 *
 * STATIC (class) VARIABLES:
 * - A normal (instance) field has one copy PER OBJECT.
 * - A "static" field has ONE copy SHARED by ALL objects of the class — it is
 *   "class-wide" information. Classic example: a counter of how many objects
 *   have been created.
 *       private static int count;   // shared by every Employee
 *
 * STATIC METHODS:
 * - Belong to the class; call them as ClassName.method(). A static method can
 *   access only static members directly (there is no "this" / current object).
 *
 * final CONSTANTS:
 * - "final" means a value cannot change after it is initialized.
 *       private final int id;            // set once in the constructor
 *       public static final double PI = 3.14159;   // a shared constant
 *   The "principle of least privilege": give code only the access it needs.
 *
 * COMPOSITION ("has-a"):
 * - A class can hold references to objects of OTHER classes as fields. That is
 *   composition. An Employee HAS-A Date (birth date). This is different from
 *   inheritance ("is-a"), which comes in the next exercises.
 *
 * TRY:
 * 1. Create three Employees and print Employee.getCount() after each.
 * 2. Try to reassign an Employee's final id — see the compile error, then undo.
 * 3. Add a static final constant COMPANY and print it without any object.
 * 4. Give Employee a second Date field (hire date) via composition.
 */
public class StaticFinalComposition {

    public static void main(String[] args) {
        System.out.println("Employees created so far: " + Employee.getCount());

        Employee e1 = new Employee("Tassos", new Date(1, 15, 2003));
        Employee e2 = new Employee("Maria", new Date(7, 3, 2002));

        System.out.println(e1.describe());
        System.out.println(e2.describe());

        // The static counter is shared across ALL Employee objects
        System.out.println("\nEmployees created so far: " + Employee.getCount());

        // A shared constant accessed through the class name, no object needed
        System.out.println("Company: " + Employee.COMPANY);
    }
}

/* A small value class used by composition (Employee HAS-A Date). */
class Date {
    private int month;
    private int day;
    private int year;

    public Date(int month, int day, int year) {
        this.month = month;
        this.day = day;
        this.year = year;
    }

    public String toString() {
        return String.format("%02d/%02d/%04d", month, day, year);
    }
}

class Employee {
    // Shared constant: same for every employee, cannot change
    public static final String COMPANY = "Uni Corp";

    // Shared counter: one copy for the whole class
    private static int count = 0;

    // Per-object fields; id is final (set once, never changes)
    private final int id;
    private String name;
    private Date birthDate;   // composition: an Employee HAS-A Date

    public Employee(String name, Date birthDate) {
        this.name = name;
        this.birthDate = birthDate;
        count++;              // bump the shared counter
        this.id = count;      // assign a unique id, then it is frozen (final)
    }

    // Static method: callable as Employee.getCount(), no object required
    public static int getCount() {
        return count;
    }

    public String describe() {
        return String.format("#%d %s (born %s) @ %s", id, name, birthDate, COMPANY);
    }
}
