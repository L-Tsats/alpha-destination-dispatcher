/*
 * EXERCISE 42: Streams  (functional data processing)
 * ==================================================
 * A STREAM is a processable sequence of elements. Unlike a collection it needs
 * no storage, is processed functionally (the source isn't changed), lazily, and
 * can be processed in parallel. You build a PIPELINE: a source, zero or more
 * INTERMEDIATE operations (each returns a new stream), and one TERMINAL
 * operation (which produces a result and consumes the stream).
 *
 *       collection.stream()                 // source
 *                 .filter(...)              // intermediate
 *                 .map(...)                 // intermediate
 *                 .forEach(...);            // terminal
 *
 * Intermediate: filter(Predicate), map(Function), sorted(), distinct(),
 *               limit(n), skip(n).
 * Terminal: forEach(Consumer), count(), collect(Collectors...), reduce(...),
 *           anyMatch/allMatch/noneMatch(Predicate), findFirst() (-> Optional).
 *
 * Imports:
 *   import java.util.*;
 *   import java.util.stream.*;
 *
 * ------------------------------------------------------------------
 * TASK 1 — a source, forEach, count
 * ------------------------------------------------------------------
 * Make a List<String> of a few words. From it:
 *   - words.stream().forEach(System.out::println);   // print each
 *   - print  words.stream().count()                  // how many
 * Also try a numeric source:  IntStream.range(1, 6).forEach(...)  prints 1..5.
 *
 * ------------------------------------------------------------------
 * TASK 2 — filter, map, sorted, distinct
 * ------------------------------------------------------------------
 * Start from a List<Integer>, e.g. [5, 3, 8, 3, 1, 8, 2]. Build ONE pipeline:
 *   - filter to keep only values > 2,
 *   - map each to its square,
 *   - distinct(),
 *   - sorted(),
 *   - forEach print.
 * Then separately: from the words list, map to w.toUpperCase(), sorted(), print.
 *
 * ------------------------------------------------------------------
 * TASK 3 — collect, reduce, match, count
 * ------------------------------------------------------------------
 *   - collect to a List:  List<Integer> evens = nums.stream()
 *         .filter(n -> n % 2 == 0).collect(Collectors.toList());  print it.
 *   - reduce to a sum:    int sum = nums.stream().reduce(0, Integer::sum);  print.
 *   - anyMatch:  boolean hasBig = nums.stream().anyMatch(n -> n > 7);  print.
 *   - count filtered:  long c = nums.stream().filter(n -> n > 2).count();  print.
 *
 * ------------------------------------------------------------------
 * TASK 4 — Optional (from findFirst)
 * ------------------------------------------------------------------
 * Optional<T> holds a value that MAY be absent. findFirst() returns one.
 *   Optional<Integer> firstBig = nums.stream().filter(n -> n > 7).findFirst();
 *   if (firstBig.isPresent()) print firstBig.get();  else print "none".
 * Try it once on a filter that matches (prints the value) and once on a filter
 * that matches nothing (prints "none") so you see both branches.
 *
 */
