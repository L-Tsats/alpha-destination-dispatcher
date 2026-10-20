// EXERCISE 23 - Set (reference solution)

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetDemo {

    public static void main(String[] args) {
        Set<String> tags = new HashSet<>();
        tags.add("java");
        tags.add("code");
        tags.add("java");   // duplicate: ignored by a Set

        System.out.println("set   = " + tags);           // order not guaranteed
        System.out.println("size  = " + tags.size());    // 2, not 3

        System.out.println("contains code? " + tags.contains("code"));
        System.out.println("contains html? " + tags.contains("html"));

        // BONUS: de-duplicate a List by building a Set from it
        List<String> withDupes = new ArrayList<>(List.of("a", "b", "a", "c", "b"));
        var unique = new HashSet<>(withDupes);
        System.out.println("\nlist   = " + withDupes + "  (size " + withDupes.size() + ")");
        System.out.println("unique = " + unique + "  (size " + unique.size() + ")");
    }
}
