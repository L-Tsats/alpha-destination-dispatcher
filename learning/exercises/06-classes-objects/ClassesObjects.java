/*
 * EXERCISE 06: Classes, objects, methods & constructors
 * =====================================================
 * (Material: Chapter 3 — classes, objects, instance variables, methods,
 *            constructors, parameters/arguments, driver class)
 *
 * KEY CONCEPTS:
 * - A CLASS is a blueprint; an OBJECT is a thing built from it (with its own data).
 * - INSTANCE VARIABLES (fields) hold each object's data; keep them private.
 * - METHODS are actions:  [access] [returnType] name(params)
 *       public void displayMessage()    public String getCourseName()
 * - CONSTRUCTOR: same name as the class, no return type; runs on "new" to
 *   initialize the object:  new GradeBook("Java").
 * - DRIVER CLASS: a separate class with main() that creates and tests your class.
 *
 * YOUR TASK:
 * 1. Below this driver class, write a class GradeBook with:
 *      - a private String field courseName
 *      - a constructor that sets it
 *      - setCourseName / getCourseName
 *      - displayMessage() that prints a welcome using the course name
 * 2. In main (here), create a GradeBook, display its message, change the name
 *    with the setter, and display again.
 *
 */
