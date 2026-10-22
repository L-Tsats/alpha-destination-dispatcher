package dispatcher.sim.traffic;

import java.util.Random;

public enum TrafficType {
    // PART 1: the constants, each with its OWN method body
    UP_PEAK {
        @Override
        public int[] sampleFloors(int n, Random random) {
            return new int[] {1, random.nextInt(n - 1) + 2};
        }
    },
    DOWN_PEAK {
        @Override
        public int[] sampleFloors(int n, Random random) {
            return new int[] {random.nextInt(n - 1) + 2, 1};
        }
    },
    MIXED {
        @Override
        public int[] sampleFloors(int n, Random random) {
            int[] fromToFloors = {random.nextInt(n) + 1, random.nextInt(n) + 1};
            while (fromToFloors[0] == fromToFloors[1]) {
                fromToFloors[0] = random.nextInt(n) + 1;
                fromToFloors[1] = random.nextInt(n) + 1;
            }
            return fromToFloors;
        }
    };   // <-- PART 2: semicolon ends the constant list

    // PART 3: the abstract method every constant MUST implement
    public abstract int[] sampleFloors(int n, Random random);
}
