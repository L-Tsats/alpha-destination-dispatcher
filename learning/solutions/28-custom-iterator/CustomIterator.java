// EXERCISE 28 - Custom Iterator from scratch (reference solution)

import java.util.Iterator;

public class CustomIterator {

    public static void main(String[] args) {
        Range r = new Range(1, 6);

        System.out.print("first pass:  ");
        for (int n : r) {
            System.out.print(n + " ");
        }
        System.out.println();

        // A fresh iterator() means we can walk it again from the start.
        System.out.print("second pass: ");
        for (int n : r) {
            System.out.print(n + " ");
        }
        System.out.println();
    }
}

// Range represents start..end-1 WITHOUT storing the numbers; each value is
// generated on demand by the iterator. Same shape as Spinellis's
// FibonacciSequence, but finite so it terminates.
class Range implements Iterable<Integer> {

    private final int start;
    private final int end;

    Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new RangeIterator();
    }

    // Private inner class: it can see Range's start/end directly, and nobody
    // outside needs to know this class exists.
    private class RangeIterator implements Iterator<Integer> {
        private int current = start;

        @Override
        public boolean hasNext() {
            return current < end;
        }

        @Override
        public Integer next() {
            return current++;   // return current value, THEN advance
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
