/*
 * EXERCISE 19: Inheritance (extends, super, protected)
 * ====================================================
 * (Material: Chapter 9 — superclass/subclass, extends, is-a vs has-a,
 *            protected access, super(...) constructor chaining,
 *            CommissionEmployee / BasePlusCommissionEmployee example)
 *
 * KEY CONCEPTS:
 * - INHERITANCE lets a new class absorb the members of an existing class and
 *   add or change behaviour. It is a form of software reuse.
 *       class Sub extends Super { ... }
 *   - Super = SUPERCLASS (more general). Sub = SUBCLASS (more specific).
 *   - This models an "IS-A" relationship: a BasePlusCommissionEmployee
 *     IS-A CommissionEmployee. (Contrast with "HAS-A" = composition.)
 *   - Java has SINGLE inheritance: a class extends exactly one direct super.
 *   - Every class ultimately extends Object (java.lang).
 *
 * - ACCESS and inheritance:
 *       private   : NOT accessible in subclasses (reach via inherited get/set).
 *       protected : accessible in the class, its subclasses, and the package.
 *       public    : accessible everywhere.
 *   The material shows that keeping fields PRIVATE (and using get/set) is the
 *   better engineering choice; protected is shown as a trade-off.
 *
 * - CONSTRUCTOR CHAINING with super(...):
 *   A subclass constructor must call a superclass constructor FIRST (directly
 *   with super(...), or Java inserts an implicit super() no-arg call). This
 *   initializes the inherited fields before the subclass adds its own.
 *
 * TRY:
 * 1. Create a CommissionEmployee and print its earnings.
 * 2. Create a BasePlusCommissionEmployee and show it reuses the super's logic.
 * 3. Change the base salary via setBaseSalary and recompute earnings.
 * 4. Confirm the subclass's constructor calls super(...) first.
 */
public class InheritanceDemo {

    public static void main(String[] args) {
        // A plain commission employee
        CommissionEmployee ce =
                new CommissionEmployee("Sue", "Jones", "222-22-2222", 10000, 0.06);
        System.out.println("=== CommissionEmployee ===");
        System.out.println(ce.describe());
        System.out.printf("Earnings: %.2f%n", ce.earnings());

        // A base-plus-commission employee IS-A commission employee, plus salary
        BasePlusCommissionEmployee bce =
                new BasePlusCommissionEmployee("Bob", "Lewis", "333-33-3333",
                        5000, 0.04, 300);
        System.out.println("\n=== BasePlusCommissionEmployee ===");
        System.out.println(bce.describe());
        System.out.printf("Earnings: %.2f%n", bce.earnings());

        // Change the base salary and recompute
        bce.setBaseSalary(1000);
        System.out.printf("After raise, earnings: %.2f%n", bce.earnings());
    }
}

/* Superclass: an employee paid a percentage of gross sales. */
class CommissionEmployee {
    private final String firstName;
    private final String lastName;
    private final String ssn;
    private double grossSales;
    private double commissionRate;

    public CommissionEmployee(String firstName, String lastName, String ssn,
                              double grossSales, double commissionRate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.ssn = ssn;
        this.grossSales = grossSales;
        this.commissionRate = commissionRate;
    }

    public void setGrossSales(double grossSales) { this.grossSales = grossSales; }
    public double getGrossSales() { return grossSales; }

    public void setCommissionRate(double rate) { this.commissionRate = rate; }
    public double getCommissionRate() { return commissionRate; }

    public String getFirstName() { return firstName; }
    public String getLastName()  { return lastName; }
    public String getSsn()       { return ssn; }

    public double earnings() {
        return commissionRate * grossSales;
    }

    public String describe() {
        return String.format("%s %s (SSN: %s), sales %.2f @ %.0f%%",
                firstName, lastName, ssn, grossSales, commissionRate * 100);
    }
}

/* Subclass: inherits everything above and adds a base salary. */
class BasePlusCommissionEmployee extends CommissionEmployee {
    private double baseSalary;

    public BasePlusCommissionEmployee(String firstName, String lastName, String ssn,
                                      double grossSales, double commissionRate,
                                      double baseSalary) {
        // super(...) MUST be first: it initializes the inherited fields
        super(firstName, lastName, ssn, grossSales, commissionRate);
        this.baseSalary = baseSalary;
    }

    public void setBaseSalary(double baseSalary) { this.baseSalary = baseSalary; }
    public double getBaseSalary() { return baseSalary; }

    // Reuse the superclass earnings() and add the base salary on top
    public double earnings() {
        return baseSalary + super.earnings();
    }

    public String describe() {
        return super.describe() + String.format(", base salary %.2f", baseSalary);
    }
}
