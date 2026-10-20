/*
 * EXERCISE 21: var — local type inference  (LADDER rung 1 of 8)
 * ============================================================
 * This is the smallest possible warm-up. One new idea. Don't overthink it.
 *
 * THE IDEA
 * --------
 * When you declare a LOCAL variable and immediately assign it, Java can figure
 * out the type for you. Instead of writing the type twice:
 *       String name = "Tassos";
 *       ArrayList<String> list = new ArrayList<String>();
 * you can write 'var' and let the compiler infer it from the right-hand side:
 *       var name = "Tassos";                 // compiler knows: String
 *       var list = new ArrayList<String>();  // compiler knows: ArrayList<String>
 *
 * IMPORTANT: this is NOT like Python/JS "any type". The variable still has ONE
 * fixed type, decided at compile time. 'var name = "Tassos";' makes name a
 * String forever — you could not later do name = 5.
 *
 * RULES (just so you know the edges):
 * - var works ONLY for local variables with an initializer (a value on the
 *   right). It can't be used for fields, method parameters, or return types,
 *   and not for 'var x;' with no value.
 *
 * YOUR TASK
 * ---------
 * 1. Declare an int the OLD way (e.g.  int a = 5;) and print it.
 * 2. Declare the same kind of value with var (e.g. var b = 10;) and print it.
 * 3. Declare a String with var and print it.
 * 4. Declare a double with var and print it.
 * That's it. The point is just to SEE that var behaves exactly like writing the
 * type yourself — the output is identical, var is only shorter to type.
 *
 */
