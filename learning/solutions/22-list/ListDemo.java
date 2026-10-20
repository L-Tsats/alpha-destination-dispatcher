// EXERCISE 22 - List (reference solution)

import java.util.ArrayList;
import java.util.List;

public class ListDemo {

    public static void main(String[] args) {
        List<String> items = new ArrayList<>();
        items.add("milk");
        items.add("bread");
        items.add("eggs");
        items.add("milk");   // duplicate: a List keeps it

        System.out.println("=== all items ===");
        for (String item : items) {
            System.out.println(item);
        }

        System.out.println("size   = " + items.size());
        System.out.println("get(0) = " + items.get(0));

        System.out.println("contains bread? " + items.contains("bread"));
        System.out.println("contains fish?  " + items.contains("fish"));

        // modify: replace index 1, remove index 0
        items.set(1, "butter");
        items.remove(0);
        System.out.println("after set + remove: " + items);
    }
}
