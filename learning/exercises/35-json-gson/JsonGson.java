/*
 * EXERCISE 35: JSON with Gson  (DATA-REP rung 3 of 4)
 * ===================================================
 * JSON (JavaScript Object Notation) is a lightweight data format of key/value
 * pairs. Example:
 *       {"name": "Anna", "age": 25, "isStudent": false,
 *        "courses": ["Math", "Science"]}
 * Values can be: numbers, strings, booleans, null, arrays [...], objects {...}.
 *
 * JAVA + JSON: Java has no built-in JSON support, so you use a library. We use
 * Gson (by Google). Its whole philosophy: map JSON <-> ordinary Java objects.
 *       Gson gson = new Gson();
 *       MyType obj = gson.fromJson(jsonString, MyType.class);  // JSON  -> object
 *       String json = gson.toJson(obj);                        // object -> JSON
 * Gson matches JSON keys to your object's FIELD NAMES automatically.
 *
 * A 'record' is the tidiest way to declare the target type. A record is a
 * compact, immutable data holder — one line gives you fields, constructor,
 * getters, equals, and toString:
 *       record Person(String name, int age) {}
 * (A normal class with matching fields works too; record is just concise.)
 *
 * NOTE ON BUILD: this module declares Gson as a dependency (see its pom.xml).
 * It builds/runs under Maven (mvn21.cmd), which downloads Gson for you. It will
 * NOT run with a bare `javac` unless you put the Gson jar on the classpath.
 *
 * Imports:  import com.google.gson.Gson;
 *
 * YOUR TASK
 * ---------
 * 1. Below, declare a record to model the data, e.g.  record Person(String name, int age) {}
 *    (declare it at top level in this file, outside the class, like the material).
 * 2. In main: make a Gson object.
 * 3. PARSE: given a JSON string like  "{\"name\":\"Anna\", \"age\":25}"  call
 *    gson.fromJson(json, Person.class) and print the resulting object.
 * 4. SERIALIZE: take a Person you build in code and call gson.toJson(person);
 *    print the JSON string it produces.
 * 5. BONUS: parse a JSON array of people, e.g.
 *      "[{\"name\":\"A\",\"age\":1},{\"name\":\"B\",\"age\":2}]"
 *    into a  Person[]  with gson.fromJson(json, Person[].class) and loop it.
 *
 */
