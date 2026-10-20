/*
 * EXERCISE 16: Arrays in methods, 2D arrays, Arrays class & ArrayList
 * ===================================================================
 * (Material: Chapter 7 Part B/C — passing arrays to methods, pass-by-value vs
 *            reference, 2D arrays, varargs, the Arrays class, ArrayList<T>)
 *
 * KEY CONCEPTS:
 * - Java passes a COPY of the argument. For a primitive, the method can't change
 *   the caller's variable. For an ARRAY (a reference), the method shares the
 *   same object, so modifying elements DOES affect the caller.
 * - VARARGS:  static int sum(int... values)  accepts any number of ints.
 * - 2D arrays are arrays of arrays:  int[][] g = new int[3][4];  walk with
 *   nested loops; g.length = rows, g[r].length = columns.
 * - java.util.Arrays: sort, toString, fill, binarySearch.
 * - java.util.ArrayList<T>: a resizable list of OBJECTS (use Integer for ints):
 *       add, get, set, remove, size, contains.
 *
 * YOUR TASK:
 * 1. Write a method doubleAll(int[]) and show it changes the caller's array.
 * 2. Write a varargs sum(int...) and call it with different counts of args.
 * 3. Make a 2D int array (rows of grades) and print each row with its average.
 * 4. Use Arrays.sort + Arrays.toString + Arrays.binarySearch on an int array.
 * 5. Build an ArrayList<String>, add/insert/remove, and print size/contents.
 *
 */
