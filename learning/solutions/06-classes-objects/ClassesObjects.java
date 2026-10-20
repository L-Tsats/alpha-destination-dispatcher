/*
 * EXERCISE 06: Classes, objects, methods & constructors
 * =====================================================
 * (Material: Chapter 3 — classes, objects, instance variables,
 *            methods, constructors, parameters/arguments, driver class)
 *
 * KEY CONCEPTS:
 * - A CLASS is a blueprint. An OBJECT is a real thing built from that blueprint.
 *   Analogy: the class is the architect's drawing of a car; each object is an
 *   actual car built from it. Each car has its own fuel level, color, etc.
 *
 * - INSTANCE VARIABLES (fields) are the data each object carries. Every object
 *   has its OWN copy. (Here: a GradeBook's course name.)
 *
 * - METHODS are the actions an object can perform. The first line is the
 *   "method header":   [access] [returnType] name(parameters)
 *       public void displayMessage()   -> public, returns nothing, no params
 *       public String getCourseName()  -> returns a String
 *
 * - PARAMETERS vs ARGUMENTS:
 *       A parameter is the variable in the method's declaration.
 *       An argument is the actual value you pass when you call it.
 *
 * - CONSTRUCTOR: a special "method" with the SAME NAME as the class and NO
 *   return type. It runs once, when you create the object with "new", to
 *   initialize its data:   GradeBook gb = new GradeBook("Java");
 *
 * - DRIVER CLASS: GradeBook has no main, so it can't run on its own. We write
 *   a separate class (this one, with main) to create and test it.
 *
 * TRY:
 * 1. Create a second GradeBook for a different course and display its message.
 * 2. Change the course name after creation using setCourseName(...).
 * 3. Add a new instance variable "instructor" with its own get/set + show it.
 */
public class ClassesObjects {

    public static void main(String[] args) {
        // Create (construct) a GradeBook object, passing the course name as an argument
        GradeBook myGradeBook = new GradeBook("Introduction to Java Programming");

        // Call methods on the object using the dot (.) operator
        myGradeBook.displayMessage();

        System.out.println("\nStored course name: " + myGradeBook.getCourseName());

        // Change the field through a setter, then show it again
        myGradeBook.setCourseName("Data Structures");
        myGradeBook.displayMessage();
    }
}

/*
 * The GradeBook class — a blueprint for grade-book objects.
 * It has one instance variable, one constructor, and three methods.
 */
class GradeBook {

    // Instance variable: each GradeBook object has its own courseName
    private String courseName;

    // Constructor: initializes the object when "new GradeBook(...)" runs
    public GradeBook(String name) {
        courseName = name;
    }

    // Setter (mutator): changes the course name
    public void setCourseName(String name) {
        courseName = name;
    }

    // Getter (accessor): returns the course name
    public String getCourseName() {
        return courseName;
    }

    // A method that performs an action using the object's data
    public void displayMessage() {
        System.out.printf("Welcome to the grade book for%n%s!%n", getCourseName());
    }
}
