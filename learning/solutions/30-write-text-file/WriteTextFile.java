// EXERCISE 30 - Write a text file (reference solution)

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class WriteTextFile {

    public static void main(String[] args) {
        String path = args.length > 0 ? args[0] : "output.txt";

        // PrintWriter wraps FileWriter: gives us println/printf, writing to the file.
        // Opening overwrites the file if it exists (use new FileWriter(path, true)
        // to append instead).
        try (var out = new PrintWriter(new FileWriter(path))) {
            out.println("Lines written by exercise 30:");
            for (int i = 1; i <= 5; i++) {
                out.printf("line %d (squared = %d)%n", i, i * i);
            }
            out.println("Done.");
        } catch (IOException e) {
            System.err.println("Could not write " + path + ": " + e.getMessage());
            return;
        }

        System.out.println("Wrote " + path + " — open it to see the text.");
        // Closing the PrintWriter (done automatically here) flushes everything to disk.
    }
}
