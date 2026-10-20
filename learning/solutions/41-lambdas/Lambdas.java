// EXERCISE 41 - Lambdas (reference solution)

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Lambdas {

    public static void main(String[] args) {
        // Task 1 — Function + apply + compose
        Function<Integer, Integer> square = (a) -> a * a;
        System.out.println("square(2) = " + square.apply(2));          // 4
        Function<Integer, Integer> fourthPower = square.compose(square);
        System.out.println("fourthPower(2) = " + fourthPower.apply(2)); // 16

        // Task 2 — Predicate, Consumer, Supplier
        Predicate<Integer> isEven = n -> n % 2 == 0;
        System.out.println("isEven(4) = " + isEven.test(4));  // true
        System.out.println("isEven(7) = " + isEven.test(7));  // false

        Consumer<String> shout = s -> System.out.println(s.toUpperCase());
        shout.accept("hello");                                 // HELLO

        Supplier<String> greet = () -> "hi there";
        System.out.println(greet.get());                       // hi there

        // Task 3 — method references
        Function<String, Integer> len = String::length;
        System.out.println("len(\"hello\") = " + len.apply("hello"));   // 5
        Consumer<String> printer = System.out::println;
        printer.accept("via method ref");
        Function<Integer, String> toStr = String::valueOf;
        System.out.println("toStr(42) = " + toStr.apply(42));           // "42"

        // Task 4 — custom functional interface
        Combiner arith = (a, op, b) -> switch (op) {
            case '+' -> a + b;
            case '*' -> a * b;
            default  -> throw new IllegalArgumentException("bad op: " + op);
        };
        System.out.println("5 + 3 = " + arith.combine(5, '+', 3));  // 8
        System.out.println("5 * 3 = " + arith.combine(5, '*', 3));  // 15
    }
}

// Task 4
@FunctionalInterface
interface Combiner {
    int combine(int a, char op, int b);
}
