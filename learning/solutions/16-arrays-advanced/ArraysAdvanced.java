/*
 * EXERCISE 16: Arrays in methods, 2D arrays, Arrays class & ArrayList
 * ===================================================================
 * (Material: Chapter 7 Part B/C — passing arrays to methods,
 *            pass-by-value vs reference, 2D arrays, varargs,
 *            the Arrays utility class, ArrayList<T>)
 *
 * KEY CONCEPTS:
 *
 * PASSING ARRAYS TO METHODS (pass-by-value of a reference):
 * - Java always passes a COPY of the argument's value.
 * - For a PRIMITIVE (int, double...), the method gets a copy of the value, so
 *   changes inside the method do NOT affect the caller's variable.
 * - For an ARRAY (a reference), the method gets a copy of the REFERENCE, which
 *   still points at the SAME array object. So modifying elements inside the
 *   method DOES change the caller's array.
 *
 * VARARGS:
 * - "Type... name" lets a method accept any number of arguments; inside, name
 *   behaves like an array.  static int sum(int... values)
 *
 * 2D ARRAYS:
 * - A table of rows and columns. In Java it is an "array of arrays".
 *       int[][] grid = new int[3][4];        // 3 rows, 4 columns
 *       int[][] g = { {1, 2}, {3, 4, 5} };   // rows may differ in length
 * - Nested loops walk it:  grid[row][col]. grid.length = rows,
 *   grid[row].length = columns in that row.
 *
 * Arrays UTILITY CLASS (java.util.Arrays):
 * - Arrays.sort(a), Arrays.toString(a), Arrays.fill(a, v),
 *   Arrays.binarySearch(sortedArray, key).
 *
 * ArrayList<T> (java.util):
 * - A resizable list (an array can't grow). Holds OBJECTS, not primitives, so
 *   use the wrapper type Integer for ints (autoboxing converts automatically).
 *   Methods: add, get, set, remove, size, contains.
 *
 * TRY:
 * 1. Call doubleAll on an array and show the caller's array changed.
 * 2. Call sum(1,2,3,4,5) and sum(10,20) using the same varargs method.
 * 3. Add another row to the 2D grid and print row totals.
 * 4. Build an ArrayList<String>, add 3 names, remove one, print it.
 */

import java.util.ArrayList;
import java.util.Arrays;

public class ArraysAdvanced {

    public static void main(String[] args) {
        // Pass-by-reference effect: the method changes the caller's array
        int[] data = { 1, 2, 3, 4 };
        System.out.println("Before doubleAll: " + Arrays.toString(data));
        doubleAll(data);
        System.out.println("After  doubleAll: " + Arrays.toString(data));

        // Varargs: one method, any number of arguments
        System.out.println("\nsum(1,2,3,4,5) = " + sum(1, 2, 3, 4, 5));
        System.out.println("sum(10,20)     = " + sum(10, 20));

        // 2D array: a small grade table (rows = students, cols = exams)
        System.out.println("\n=== 2D array (grades) ===");
        int[][] grades = {
            { 87, 96, 70 },
            { 68, 87, 90 },
            { 94, 100, 90 }
        };
        for (int student = 0; student < grades.length; student++) {
            int rowTotal = 0;
            for (int exam = 0; exam < grades[student].length; exam++) {
                System.out.printf("%4d", grades[student][exam]);
                rowTotal += grades[student][exam];
            }
            System.out.printf("   | avg = %.1f%n",
                    (double) rowTotal / grades[student].length);
        }

        // Arrays utility class
        System.out.println("\n=== Arrays class ===");
        int[] values = { 5, 2, 9, 1, 7 };
        Arrays.sort(values);
        System.out.println("Sorted: " + Arrays.toString(values));
        int pos = Arrays.binarySearch(values, 9);
        System.out.println("Index of 9 (after sort): " + pos);

        // ArrayList: a resizable list of objects
        System.out.println("\n=== ArrayList ===");
        ArrayList<String> names = new ArrayList<>();
        names.add("Tassos");
        names.add("Maria");
        names.add("Nikos");
        names.remove("Maria");          // remove by value
        names.add(1, "Eleni");          // insert at index 1
        System.out.println("Names: " + names);
        System.out.println("Size:  " + names.size());
        System.out.println("First: " + names.get(0));
        System.out.println("Contains Nikos? " + names.contains("Nikos"));
    }

    // Doubles every element. Because arrays are references, this changes the
    // caller's array in place.
    static void doubleAll(int[] a) {
        for (int i = 0; i < a.length; i++) {
            a[i] *= 2;
        }
    }

    // Varargs: 'values' acts like an int[] inside the method
    static int sum(int... values) {
        int total = 0;
        for (int v : values) {
            total += v;
        }
        return total;
    }
}
