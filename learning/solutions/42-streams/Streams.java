// EXERCISE 42 - Streams (reference solution)

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Streams {

    public static void main(String[] args) {
        List<String> words = List.of("beta", "alpha", "gamma", "alpha");
        List<Integer> nums = List.of(5, 3, 8, 3, 1, 8, 2);

        // Task 1 — source, forEach, count
        System.out.println("--- words ---");
        words.stream().forEach(System.out::println);
        System.out.println("count = " + words.stream().count());
        System.out.print("1..5: ");
        IntStream.range(1, 6).forEach(i -> System.out.print(i + " "));
        System.out.println();

        // Task 2 — filter, map, distinct, sorted
        System.out.println("--- >2, squared, distinct, sorted ---");
        nums.stream()
            .filter(n -> n > 2)
            .map(n -> n * n)
            .distinct()
            .sorted()
            .forEach(System.out::println);

        System.out.println("--- words uppercased, sorted ---");
        words.stream()
             .map(String::toUpperCase)
             .sorted()
             .forEach(System.out::println);

        // Task 3 — collect, reduce, match, count
        List<Integer> evens = nums.stream()
            .filter(n -> n % 2 == 0)
            .collect(Collectors.toList());
        System.out.println("evens = " + evens);

        int sum = nums.stream().reduce(0, Integer::sum);
        System.out.println("sum = " + sum);

        boolean hasBig = nums.stream().anyMatch(n -> n > 7);
        System.out.println("anyMatch(>7) = " + hasBig);

        long c = nums.stream().filter(n -> n > 2).count();
        System.out.println("count(>2) = " + c);

        // Task 4 — Optional from findFirst (present and absent)
        Optional<Integer> firstBig = nums.stream().filter(n -> n > 7).findFirst();
        System.out.println(firstBig.isPresent() ? "firstBig = " + firstBig.get() : "none");

        Optional<Integer> firstHuge = nums.stream().filter(n -> n > 100).findFirst();
        System.out.println(firstHuge.isPresent() ? "firstHuge = " + firstHuge.get() : "none");
    }
}
