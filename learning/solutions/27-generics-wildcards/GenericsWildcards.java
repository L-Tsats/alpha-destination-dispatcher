// EXERCISE 27 - Generic methods & wildcards (reference solution)

import java.util.ArrayList;
import java.util.List;

public class GenericsWildcards {

    public static void main(String[] args) {
        List<String> words = new ArrayList<>(List.of("alpha", "beta", "gamma"));
        List<Integer> nums = new ArrayList<>(List.of(10, 20, 30));

        System.out.println("=== printAll(words) ===");
        printAll(words);
        System.out.println("=== printAll(nums) ===");
        printAll(nums);

        System.out.println("size(words) = " + size(words));
        System.out.println("size(nums)  = " + size(nums));

        System.out.println("firstOf(words) = " + firstOf(words));
        System.out.println("firstOf(nums)  = " + firstOf(nums));
    }

    // Generic method: works for a List of ANY element type E.
    static <E> void printAll(List<E> items) {
        for (E item : items) {
            System.out.println(item);
        }
    }

    // Wildcard: only needs size(), never touches the element type -> use '?'.
    static int size(List<?> items) {
        return items.size();
    }

    // Must NAME the type here, because the return value IS an E.
    static <E> E firstOf(List<E> items) {
        return items.get(0);
    }
}
