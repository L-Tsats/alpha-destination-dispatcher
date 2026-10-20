/*
 * EXERCISE 29: Read a text file, line by line  (FILE I/O rung 1 of 4)
 * ===================================================================
 * The whole File I/O chapter is ONE pattern wearing many costumes:
 *       open a stream  ->  loop until it ends  ->  close it
 * This rung does the single most common real case: read a text file's lines.
 *
 * THE IDEA
 * --------
 * To read TEXT (characters, lines) from a file, the handy class is
 * BufferedReader. You build it by WRAPPING a FileReader:
 *
 *       BufferedReader in = new BufferedReader(new FileReader("notes.txt"));
 *
 * Read that inside-out, like layers:
 *   FileReader      -> opens the file and reads it as characters
 *   BufferedReader  -> wraps that to add a speed buffer + the readLine() method
 *
 * readLine() returns the next line as a String, or null at end-of-file:
 *       String line;
 *       while ((line = in.readLine()) != null)
 *           System.out.println(line);
 *   (That  (line = in.readLine()) != null  idiom means: read a line, store it,
 *    and keep looping as long as it wasn't null. null = "no more lines".)
 *
 * TRY-WITH-RESOURCES (auto-close):
 *   File handles must be closed. Instead of calling close() by hand, declare
 *   the stream in the parentheses of a 'try' and Java closes it automatically,
 *   even if an error happens:
 *       try (var in = new BufferedReader(new FileReader(path))) {
 *           ...use in...
 *       } catch (IOException e) {
 *           ...handle the error...
 *       }
 *   // 'in' is closed for you here, no matter what.
 *
 * WHY catch: file operations can fail (missing file, read error), so they
 * throw a CHECKED exception (IOException) that you are REQUIRED to handle.
 *
 * Imports:
 *       import java.io.BufferedReader;
 *       import java.io.FileReader;
 *       import java.io.IOException;
 *
 * SET-UP: this module ships with a sample file at the module root called
 * sample.txt — read THAT. (Run the program from the module folder, or pass the
 * path as a command-line argument; see the task.)
 *
 * YOUR TASK
 * ---------
 * 1. Get the filename: use args[0] if the user passed one, otherwise default to
 *    "sample.txt". (Tip: String path = args.length > 0 ? args[0] : "sample.txt";)
 * 2. Open it with a BufferedReader inside a try-with-resources.
 * 3. Loop with readLine() until it returns null, printing each line. BONUS:
 *    keep a counter and print "N lines" at the end.
 * 4. catch (IOException e) and print a friendly error (e.getMessage()).
 *
 *   (reads sample.txt from the module folder)
 */
