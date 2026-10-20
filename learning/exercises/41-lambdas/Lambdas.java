/*
 * EXERCISE 41: Lambdas  (functional programming)
 * ==============================================
 * Functional programming treats FUNCTIONS as first-class values: you can store a
 * function in a variable, pass it to a method, and return it from a method. Java
 * expresses anonymous functions as LAMBDA EXPRESSIONS.
 *
 * LAMBDA SYNTAX
 * -------------
 *       (a) -> a * a          // one parameter, returns a*a
 *       (a, b) -> a + b       // two parameters
 *       () -> true            // no parameters
 *       (x) -> { ...; return y; }   // block body with explicit return
 *
 * THE FUNCTION INTERFACES (java.util.function)
 * --------------------------------------------
 *   Function<T,R>   T -> R      method: R apply(T t)
 *   Predicate<T>    T -> boolean      boolean test(T t)
 *   Consumer<T>     T -> void         void accept(T t)
 *   Supplier<R>     () -> R           R get()
 *   UnaryOperator<T>  T -> T          (a Function where in and out are same type)
 *   BinaryOperator<T> (T,T) -> T
 * A lambda's shape must match the interface's single method.
 *
 * Imports: import java.util.function.*;
 *
 * ------------------------------------------------------------------
 * TASK 1 — Function + apply, and compose
 * ------------------------------------------------------------------
 * Assign a lambda to a Function<Integer,Integer> square = (a) -> a * a;
 * Print square.apply(2)  (-> 4).
 * Then make  Function<Integer,Integer> fourthPower = square.compose(square);
 * Print fourthPower.apply(2)  (-> 16). (compose: apply the argument function
 * first, then this one.)
 *
 * ------------------------------------------------------------------
 * TASK 2 — Predicate, Consumer, Supplier
 * ------------------------------------------------------------------
 *   - Predicate<Integer> isEven = n -> n % 2 == 0;  print isEven.test(4) and test(7).
 *   - Consumer<String> shout = s -> System.out.println(s.toUpperCase());
 *     call shout.accept("hello").
 *   - Supplier<String> greet = () -> "hi there";  print greet.get().
 *
 * ------------------------------------------------------------------
 * TASK 3 — method references (::)
 * ------------------------------------------------------------------
 * A method that fits a functional interface can be used AS one via ClassName::method.
 *   - Function<String,Integer> len = String::length;   print len.apply("hello") (->5).
 *   - Consumer<String> printer = System.out::println;   printer.accept("via method ref");
 *   - (optional) Function<Integer,String> toStr = String::valueOf; print toStr.apply(42).
 *
 * ------------------------------------------------------------------
 * TASK 4 — your own @FunctionalInterface
 * ------------------------------------------------------------------
 * Declare, at top level in this file:
 *       @FunctionalInterface
 *       interface Combiner { int combine(int a, char op, int b); }
 * In main, assign a lambda implementing it that does arithmetic:
 *       Combiner arith = (a, op, b) -> switch (op) {
 *           case '+' -> a + b;
 *           case '*' -> a * b;
 *           default  -> throw new IllegalArgumentException("bad op: " + op);
 *       };
 * Print arith.combine(5, '+', 3) (->8) and arith.combine(5, '*', 3) (->15).
 *
 */
