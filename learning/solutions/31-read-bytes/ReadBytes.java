// EXERCISE 31 - Read raw bytes & count them (reference solution)

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class ReadBytes {

    public static void main(String[] args) {
        String path = args.length > 0 ? args[0] : "data.bin";

        try (var in = new BufferedInputStream(new FileInputStream(path))) {
            int b;
            int count = 0;
            // read() returns the next byte as an int 0..255, or -1 at end-of-file.
            // (It returns int, not byte, so that -1 can mean "no more data".)
            while ((b = in.read()) != -1) {
                count++;
            }
            System.out.println(count + " bytes");
        } catch (IOException e) {
            System.err.println("Could not read " + path + ": " + e.getMessage());
        }
    }
}
