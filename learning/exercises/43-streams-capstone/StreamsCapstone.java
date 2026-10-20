/*
 * EXERCISE 43: Streams capstone — unique words from a file
 * ========================================================
 * The famous "Knuth vs McIlroy" task: output the sorted list of a file's unique
 * words. McIlroy did it in a Unix one-liner; in Java, a stream pipeline is just
 * as elegant. This capstone ties together THREE things you've learned: FILES
 * (rung 29), REGEX (rung 34, splitting on \W+), and STREAMS (rung 42).
 *
 * THE PIPELINE (this is basically the whole program)
 * --------------------------------------------------
 *   Files.lines(path)                              // a Stream<String> of lines
 *        .flatMap(line -> Stream.of(line.split("\\W+")))  // lines -> words
 *        .filter(w -> w.length() > 0)              // drop empties
 *        .map(String::toLowerCase)                 // normalise case
 *        .distinct()                               // unique
 *        .sorted()                                 // alphabetical
 *        .forEach(System.out::println);            // print each
 *
 * KEY NEW OP — flatMap: map() turns each element into ONE element; flatMap turns
 * each element into a STREAM and flattens them all into one stream. Here each
 * line becomes a stream of its words, all flattened into one stream of words.
 *
 * WHY split("\\W+"): \W is "non-word char" (rung 33/34); splitting on runs of
 * them tokenises a line into words regardless of spaces/punctuation. (The leading
 * token can be empty if a line starts with punctuation — that's why we filter
 * out length-0 strings.)
 *
 * Files.lines throws IOException — wrap in try/catch (or declare throws).
 *
 * Imports:
 *   import java.nio.file.Files;
 *   import java.nio.file.Paths;
 *   import java.util.stream.Stream;
 *   import java.io.IOException;
 *
 * SET-UP: this module ships  sample.txt  at its root (a few lines of text with
 * repeated words and mixed case/punctuation).
 *
 * YOUR TASK
 * ---------
 * Read sample.txt and print its unique words, lowercased, one per line, in
 * alphabetical order, using ONE stream pipeline like the one above. Path:
 *       Paths.get("exercises/43-streams-capstone/sample.txt")
 * BONUS: instead of printing, count them — swap the terminal forEach for
 *       .count()  and print the number of distinct words.
 *
 */
