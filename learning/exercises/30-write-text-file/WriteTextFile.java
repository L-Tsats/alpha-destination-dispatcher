/*
 * EXERCISE 30: Write a text file  (FILE I/O rung 2 of 4)
 * ======================================================
 * The mirror image of rung 29. Same pattern (open -> use -> auto-close), but
 * now we WRITE instead of read. If you understood reading, this is easy.
 *
 * THE IDEA
 * --------
 * The friendliest class for writing text is PrintWriter — it has the SAME
 * print / println / printf methods you already use on System.out. You build it
 * by wrapping a FileWriter:
 *
 *       PrintWriter out = new PrintWriter(new FileWriter("output.txt"));
 *
 * Inside-out:
 *   FileWriter   -> opens/creates the file for writing characters
 *   PrintWriter  -> wraps it to give you println/printf etc.
 *
 * Then just use it like System.out, but it goes to the FILE:
 *       out.println("hello");
 *       out.printf("x = %d%n", 42);
 *
 * NOTE: opening a FileWriter CREATES the file if it doesn't exist, and
 * OVERWRITES it if it does. (new FileWriter(path, true) would APPEND instead.)
 *
 * Same try-with-resources + catch as before (writing can fail too):
 *       try (var out = new PrintWriter(new FileWriter(path))) {
 *           out.println(...);
 *       } catch (IOException e) { ... }
 *   // auto-closed here. (Closing also FLUSHES buffered text to disk — important!)
 *
 * Imports:
 *       import java.io.PrintWriter;
 *       import java.io.FileWriter;
 *       import java.io.IOException;
 *
 * YOUR TASK
 * ---------
 * 1. Choose an output filename (e.g. "output.txt").
 * 2. Open a PrintWriter on it inside a try-with-resources.
 * 3. Write several lines with println, and at least one with printf (e.g. a
 *    numbered line, or a formatted number). Maybe loop 1..5 writing a line each.
 * 4. catch (IOException e) and report it.
 * 5. After running, OPEN output.txt (it appears in the module folder) and
 *    confirm your text is there. BONUS: run exercise 29 pointing at it:
 *      mvn21.cmd -pl exercises/29-read-text-file compile exec:java -Dexec.mainClass="ReadTextFile" -Dexec.args="..\\30-write-text-file\\output.txt"
 *
 */
