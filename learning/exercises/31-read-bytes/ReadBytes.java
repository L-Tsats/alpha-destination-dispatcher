/*
 * EXERCISE 31: Read raw bytes & count them  (FILE I/O rung 3 of 4)
 * ================================================================
 * Rungs 29-30 handled TEXT (characters/lines). This one handles RAW BYTES —
 * for files that aren't text (images, etc.) or when you just want the raw
 * byte count. This is the Spinellis "ByteCount" example.
 *
 * TEXT vs BYTES (the key distinction of the whole chapter):
 *   - Characters/text  -> Reader / Writer classes (FileReader, BufferedReader)
 *   - Raw bytes        -> InputStream / OutputStream classes (below)
 * Same open->loop->close pattern; just a different family of classes.
 *
 * THE IDEA
 * --------
 * To read bytes, wrap a FileInputStream in a BufferedInputStream (buffer =
 * speed, fewer trips to the OS):
 *
 *       BufferedInputStream in =
 *           new BufferedInputStream(new FileInputStream(path));
 *
 * read() returns the next byte as an int 0..255, or -1 at end-of-file:
 *       int b;
 *       int count = 0;
 *       while ((b = in.read()) != -1)
 *           count++;
 *   (Note: read() returns an int, NOT a byte, precisely so that end-of-file
 *    can be signalled with -1 — a value no real byte 0..255 can take.)
 *
 * Same try-with-resources + catch (IOException).
 *
 * Imports:
 *       import java.io.BufferedInputStream;
 *       import java.io.FileInputStream;
 *       import java.io.IOException;
 *
 * SET-UP: this module ships with data.bin (a small file) at its root. You can
 * also count ANY file by passing its path as args[0].
 *
 * YOUR TASK
 * ---------
 * 1. Pick the path: args[0] if given, else "data.bin".
 * 2. Open a BufferedInputStream(new FileInputStream(path)) in try-with-resources.
 * 3. Loop read() until it returns -1, counting the bytes. Print the count.
 * 4. catch (IOException e) and report it.
 * 5. BONUS: point it at a text file (e.g. exercise 29's sample.txt) and compare
 *    the BYTE count here with the LINE count there — different units, same file.
 *
 */
