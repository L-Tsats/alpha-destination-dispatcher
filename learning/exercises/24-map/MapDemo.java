/*
 * EXERCISE 24: Map — key -> value lookups  (LADDER rung 4 of 8)
 * ============================================================
 * A Map stores PAIRS: each unique KEY maps to a VALUE. Think of a dictionary:
 * the word (key) -> its definition (value). Or item name -> price.
 *
 * THE IDEA
 * --------
 * Map<K, V> is an INTERFACE with TWO type parameters: K = key type, V = value
 * type. HashMap<K, V> is the usual implementation:
 *       Map<Double> price = new HashMap<>();   // String keys, int values
 *
 * NOTE: a Map is NOT a Collection (it's not a bag of single elements; it's
 * pairs). It's part of the same framework but its own thing.
 *
 * COMMON METHODS:
 *       price.put("milk", 2)      add/replace: key "milk" -> value 2
 *       price.get("milk")         look up the value for a key (null if absent)
 *       price.containsKey("milk") is this key present?
 *       price.size()              how many pairs
 *       price.remove("milk")      remove a pair by key
 *
 * KEYS ARE UNIQUE: put("milk", 2) then put("milk", 5) REPLACES — "milk" now
 * maps to 5, not two entries. (Values can repeat; keys cannot.)
 *
 * LOOPING a Map — iterate the entries (each is a key+value pair):
 *       for (Map.Entry<String, Integer> e : price.entrySet())
 *           System.out.println(e.getKey() + " = " + e.getValue());
 *   (You can also loop price.keySet() for just keys, or price.values() for
 *    just values.)
 *
 * Imports:
 *       import java.util.Map;
 *       import java.util.HashMap;
 *
 * YOUR TASK
 * ---------
 * 1. Make a Map<String, Integer> of item -> price. put() a few pairs.
 * 2. Look up ONE item with get(...) and print its price.
 * 3. put() a NEW price for an existing key and show get(...) now returns the
 *    new value (keys are unique — it replaced).
 * 4. Use containsKey(...) for a present and an absent key.
 * 5. Loop the whole map with entrySet() and print "key = value" for each.
 *
 */
