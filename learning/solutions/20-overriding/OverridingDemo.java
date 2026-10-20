/*
 * EXERCISE 20: Overriding, @Override & toString
 * =============================================
 * (Material: Chapter 9 — overriding superclass methods, @Override annotation,
 *            the Object class, toString, calling super's version)
 *
 * KEY CONCEPTS:
 * - OVERRIDING: a subclass provides its OWN version of a method it inherited,
 *   using the SAME signature (name + parameter list). At run time, the
 *   subclass's version is the one that runs for a subclass object.
 *   (Do not confuse with OVERLOADING, which is same name but DIFFERENT
 *    parameters, resolved at compile time.)
 *
 * - @Override annotation:
 *   Put @Override above a method you intend to override. The compiler then
 *   checks that it really matches a superclass method. If you mistype the
 *   signature, you get a helpful compile error instead of a silent bug
 *   (which would secretly create an overload rather than an override).
 *
 * - toString():
 *   Every class inherits toString() from Object. The default returns something
 *   like "ClassName@1b6d3586" (not useful). Override it to return a readable
 *   description. It is called AUTOMATICALLY when an object is used where a
 *   String is expected, e.g. System.out.println(animal) or "" + animal.
 *
 * - super.method():
 *   Inside an overriding method you can still call the superclass version with
 *   super.method(), then extend its result (as in super.toString()).
 *
 * - Restriction: an override cannot REDUCE visibility (a public method cannot
 *   become protected/private in the subclass) — that would break the IS-A.
 *
 * TRY:
 * 1. Add a Cat subclass that overrides speak() to return "Meow".
 * 2. Remove @Override from Dog.speak and mistype it ("spek") — see what breaks.
 * 3. Put Dog and Cat in an Animal[] and loop printing each (toString auto-runs).
 * 4. Make Dog.toString() call super.toString() and append the breed.
 */
public class OverridingDemo {

    public static void main(String[] args) {
        Animal generic = new Animal("Generic", 3);
        Dog dog = new Dog("Rex", 5, "Labrador");

        // speak() is overridden in Dog; toString() is overridden in both
        System.out.println(generic);          // println calls toString() for us
        System.out.println("says: " + generic.speak());

        System.out.println("\n" + dog);
        System.out.println("says: " + dog.speak());

        // Treat a Dog as an Animal — the Dog's overridden methods still run
        System.out.println("\n=== As an Animal reference ===");
        Animal asAnimal = dog;                 // a Dog IS-A Animal
        System.out.println(asAnimal.speak());  // prints "Woof" (Dog's version)
    }
}

class Animal {
    protected String name;
    protected int age;

    public Animal(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Base behaviour — subclasses may override this
    public String speak() {
        return "...";
    }

    // Override Object's toString() to give a readable description
    @Override
    public String toString() {
        return String.format("%s (age %d)", name, age);
    }
}

class Dog extends Animal {
    private String breed;

    public Dog(String name, int age, String breed) {
        super(name, age);     // initialize inherited fields first
        this.breed = breed;
    }

    // Override the inherited speak(); @Override makes the compiler verify it
    @Override
    public String speak() {
        return "Woof";
    }

    // Override toString() but reuse the superclass version via super.toString()
    @Override
    public String toString() {
        return super.toString() + " [" + breed + "]";
    }
}
