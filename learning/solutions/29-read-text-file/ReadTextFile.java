// EXERCISE 29 - Read a text file line by line (reference solution)

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ReadTextFile {

    public static void main(String[] args) {
        // Use the given path, or default to sample.txt in the module folder
        String path = args.length > 0 ? args[0] : "sample.txt";

        // try-with-resources: 'in' is auto-closed when the block ends
        try (var in = new BufferedReader(new FileReader(path))) {
            String line;
            int count = 0;
            while ((line = in.readLine()) != null) {   // null == end of file
                count++;
                System.out.println(count + ": " + line);
            }
            System.out.println("(" + count + " lines)");
        } catch (IOException e) {
            System.err.println("Could not read " + path + ": " + e.getMessage());
        }
    }
}
