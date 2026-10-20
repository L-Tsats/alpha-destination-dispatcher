/*
 * EXERCISE 01: Hello World — print, println, escape sequences
 * ===========================================================
 * (Material: Chapter 2, Part A)
 *
 * KEY CONCEPTS:
 * - Every Java program lives inside a CLASS. The class name MUST match the
 *   file name exactly: class HelloWorld  ->  file HelloWorld.java
 * - Execution always starts in the special method:
 *       public static void main(String[] args)
 *   The JVM calls this automatically when you run the program.
 * - System.out is the "standard output" object (the console).
 *     - println("...")  prints the text AND moves to the next line
 *     - print("...")    prints the text and stays on the SAME line
 * - Every statement ends with a semicolon ;
 * - Text (a "String") goes inside double quotes "like this".
 *
 * ESCAPE SEQUENCES (special characters inside a String):
 *     \n   new line
 *     \t   tab
 *     \"   a literal double quote
 *     \\   a literal backslash
 *
 * TRY:
 * 1. Change the first message to print your own name.
 * 2. Swap a println for a print — notice how the next output sticks to it.
 * 3. Print a line that shows literal quotes, e.g.  She said "hello"
 * 4. Use a single println with \n in the middle to print two lines at once.
 */
public class HelloWorld {

    public static void main(String[] args) {
        // println: each one ends with a line break
        System.out.println("Hello, World!");
        System.out.println("Welcome to Java programming!");

        // print vs println: print stays on the same line
        System.out.print("These two ");
        System.out.print("are on the same line.");
        System.out.println(); // empty println just ends the line

        // One println, many lines, using the \n escape sequence
        System.out.println("Line one\nLine two\nLine three");

        // \t tab, \" literal quote
        System.out.println("Name:\tTassos");
        System.out.println("She said \"Java is fun\".");
    }
}
