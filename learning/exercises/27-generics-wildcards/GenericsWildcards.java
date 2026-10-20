/*
 * EXERCISE 27: Generic methods & the ? wildcard  (LADDER rung 7 of 8)
 * ===================================================================
 * You've USED generics (List<String>). Now you'll WRITE methods that are
 * generic. This demystifies the <E> and <?> symbols from the Spinellis slide.
 *
 * THE IDEA
 * --------
 * (1) A GENERIC METHOD declares its own type parameter in angle brackets,
 *     placed right before the return type:
 *
 *         static <E> void printAll(List<E> items) {
 *             for (E item : items)
 *                 System.out.println(item);
 *         }
 *
 *     The <E> after 'static' says "this method works for some type E, decided
 *     per call". Call it with a List<String> and E is String; with a
 *     List<Integer>, E is Integer. One method, many element types.
 *
 * (2) The WILDCARD '?' means "some unknown type — I don't need to name it".
 *     Use it when the method only READS/traverses and doesn't care what the
 *     elements are:
 *
 *         static void printAll(List<?> items) {
 *             for (Object item : items)      // read as Object; we don't know E
 *                 System.out.println(item);
 *         }
 *
 *     Difference in a nutshell:
 *       <E>  -> you NAME the type, so you can use E inside (declare E vars, etc.)
 *       <?>  -> you DON'T name it; fine when you only read the elements as Object
 *
 * Imports:
 *       import java.util.List;
 *       import java.util.ArrayList;
 *
 * YOUR TASK
 * ---------
 * 1. Write a GENERIC method:
 *        static <E> void printAll(List<E> items)
 *    that prints every element on its own line.
 * 2. Write a WILDCARD method:
 *        static int size(List<?> items)
 *    that returns items.size()  (it doesn't need to know the element type).
 * 3. Write another generic method:
 *        static <E> E firstOf(List<E> items)
 *    that returns the first element (items.get(0)). Note the return type is E —
 *    this is why you must NAME the type here (a wildcard couldn't return it).
 * 4. In main: make a List<String> and a List<Integer>, and call all three
 *    methods on BOTH lists to prove one method handles different element types.
 *
 */
