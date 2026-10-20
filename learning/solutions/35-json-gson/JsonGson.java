// EXERCISE 35 - JSON with Gson (reference solution)
// Needs the Gson dependency (declared in this module's pom.xml).

import com.google.gson.Gson;

public class JsonGson {

    public static void main(String[] args) {
        Gson gson = new Gson();

        // 3) PARSE: JSON string -> Java object
        String json = "{\"name\":\"Anna\", \"age\":25}";
        Person person = gson.fromJson(json, Person.class);
        System.out.println("Parsed: " + person);          // Person[name=Anna, age=25]
        System.out.println("name = " + person.name() + ", age = " + person.age());

        // 4) SERIALIZE: Java object -> JSON string
        Person bob = new Person("Bob", 42);
        String out = gson.toJson(bob);
        System.out.println("\nSerialized: " + out);        // {"name":"Bob","age":42}

        // 5) BONUS: parse a JSON array into Person[]
        String arrayJson = "[{\"name\":\"A\",\"age\":1},{\"name\":\"B\",\"age\":2}]";
        Person[] people = gson.fromJson(arrayJson, Person[].class);
        System.out.println("\nArray:");
        for (Person p : people) {
            System.out.println("  " + p);
        }
    }
}

// Gson maps JSON keys to these field names automatically.
record Person(String name, int age) {}
