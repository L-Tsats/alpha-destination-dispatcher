// EXERCISE 43 - Streams capstone: unique words from a file (reference solution)

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.io.IOException;

public class StreamsCapstone {

    public static void main(String[] args) {
        try {
            Files.lines(Paths.get("exercises/43-streams-capstone/sample.txt"))
                 .flatMap(line -> Stream.of(line.split("\\W+")))
                 .filter(w -> w.length() > 0)
                 .map(String::toLowerCase)
                 .distinct()
                 .sorted()
                 .forEach(System.out::println);

            // BONUS: count distinct words instead of printing them
            long distinctCount = Files.lines(Paths.get("exercises/43-streams-capstone/sample.txt"))
                 .flatMap(line -> Stream.of(line.split("\\W+")))
                 .filter(w -> w.length() > 0)
                 .map(String::toLowerCase)
                 .distinct()
                 .count();
            System.out.println("--- distinct word count: " + distinctCount);

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}
