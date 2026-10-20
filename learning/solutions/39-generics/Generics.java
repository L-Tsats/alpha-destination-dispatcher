// EXERCISE 39 - Generic classes (reference solution)

public class Generics {

    public static void main(String[] args) {
        // Task 1 — Box<T>
        Box<String> bs = new Box<>("hello");
        Box<Integer> bi = new Box<>(42);
        System.out.println(bs + " / " + bi);
        bs.set("changed");
        bi.set(99);
        System.out.println(bs.get() + " / " + bi.get());

        // Task 2 — Pair<A,B>
        Pair<String, Integer> age = new Pair<>("age", 25);
        Pair<Integer, Integer> point = new Pair<>(3, 7);
        System.out.println(age);
        System.out.println(point);

        // Task 3 — LinkedList<E>
        LinkedList<Integer> list = new LinkedList<>(0);
        list = list.add(1);
        list = list.add(18);
        list = list.add(45);
        System.out.println(list);   // 45 -> 18 -> 1 -> 0
    }
}

// Task 1: one type parameter
class Box<T> {
    private T value;

    Box(T value) { this.value = value; }

    T get() { return value; }

    void set(T value) { this.value = value; }

    @Override
    public String toString() { return "Box(" + value + ")"; }
}

// Task 2: two type parameters
class Pair<A, B> {
    private final A first;
    private final B second;

    Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    A getFirst() { return first; }
    B getSecond() { return second; }

    @Override
    public String toString() { return "(" + first + ", " + second + ")"; }
}

// Task 3: recursive generic structure
class LinkedList<E> {
    private final E value;
    private LinkedList<E> next;

    LinkedList(E v) {
        value = v;
        next = null;
    }

    // Return a NEW node holding v, pointing at this list as its tail.
    LinkedList<E> add(E v) {
        LinkedList<E> n = new LinkedList<>(v);
        n.next = this;
        return n;
    }

    @Override
    public String toString() {
        if (next == null) {
            return value.toString();
        }
        return value + " -> " + next;
    }
}
