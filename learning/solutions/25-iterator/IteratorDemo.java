// EXERCISE 25 - Iterator (reference solution)

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IteratorDemo {

    public static void main(String[] args) {
        List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8));
        System.out.println("before: " + numbers);

        // Walk by hand with an explicit iterator (what a for-each does internally)
        System.out.print("walking: ");
        Iterator<Integer> walk = numbers.iterator();
        while (walk.hasNext()) {
            System.out.print(walk.next() + " ");
        }
        System.out.println();

        // Remove the evens the SAFE way, using the iterator's own remove().
        Iterator<Integer> it = numbers.iterator();
        while (it.hasNext()) {
            int n = it.next();
            if (n % 2 == 0) {
                it.remove();
            }
        }
        System.out.println("after removing evens: " + numbers);

        /*
         * The WRONG way (would crash with ConcurrentModificationException):
         *
         *     for (int n : numbers) {
         *         if (n % 2 == 0) {
         *             numbers.remove(Integer.valueOf(n));  // modifies during for-each
         *         }
         *     }
         *
         * That's exactly why Iterator.remove() exists.
         */
    }
}
