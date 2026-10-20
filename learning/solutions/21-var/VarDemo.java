// EXERCISE 21 - var (reference solution)

public class VarDemo {

    public static void main(String[] args) {
        // old way
        int a = 5;
        System.out.println("a (int, old way)  = " + a);

        // var: compiler infers the type from the right-hand side
        var b = 10;          // inferred int
        System.out.println("b (var -> int)    = " + b);

        var name = "Tassos"; // inferred String
        System.out.println("name (var -> String) = " + name);

        var pi = 3.14;       // inferred double
        System.out.println("pi (var -> double) = " + pi);

        // b is still strongly typed: this would NOT compile ->  b = "hello";
    }
}
