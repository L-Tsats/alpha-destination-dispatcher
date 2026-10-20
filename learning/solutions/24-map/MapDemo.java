// EXERCISE 24 - Map (reference solution)

import java.util.HashMap;
import java.util.Map;

public class MapDemo {

    public static void main(String[] args) {
        Map<String, Integer> price = new HashMap<>();
        price.put("milk", 2);
        price.put("bread", 1);
        price.put("eggs", 3);

        System.out.println("price of bread = " + price.get("bread"));

        // keys are unique: putting "milk" again REPLACES its value
        price.put("milk", 5);
        System.out.println("price of milk after replace = " + price.get("milk"));

        System.out.println("has eggs?   " + price.containsKey("eggs"));
        System.out.println("has coffee? " + price.containsKey("coffee"));

        System.out.println("\n=== all pairs ===");
        for (Map.Entry<String, Integer> e : price.entrySet()) {
            System.out.println(e.getKey() + " = " + e.getValue());
        }
    }
}
