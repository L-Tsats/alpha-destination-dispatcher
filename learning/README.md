# Java Learning Exercises

A ladder of small Java exercises, from "hello world" up to generics, threads,
lambdas, streams, and HTTP. Work through them in order — each builds on the last.

## How to use
- Each folder in `exercises/` has a `.java` file containing ONLY an instruction
  comment: a description, the idea, and numbered TASKS. You write all the code
  yourself (including the class declaration).
- Some exercises ship data files (`.txt`, `.xml`, `.xsd`, ...) they read — keep
  them next to the `.java` file.
- When you think you're done, compare with the matching folder in `solutions/`.
  Try first, THEN peek.

## How to run (VS Code)
1. Install the "Extension Pack for Java" (Microsoft) in VS Code.
2. Open a `.java` file. Click the ▷ Run link above `main`, or press Ctrl+F5
   (Run Without Debugging) for clean output.
   (Or from a terminal in the file's folder:  javac File.java   then   java File )

## The ladder (rough order)
- 01-20: basics — printing, variables, input, if/else, loops, classes, methods,
  recursion, arrays, constructors/encapsulation, inheritance, overriding.
- 21-28: collections & iterators — var, List, Set, Map, Iterator, Iterable,
  generics/wildcards, custom iterators.
- 29-32: files — reading/writing text, reading bytes, the File class.
- 33-38: data representation — regex, regex groups/split, JSON (Gson), XML (DOM),
  nested XML, XML Schema (XSD).
- 39-43: generics (classes), threads, lambdas, streams, a streams capstone.
- 44-45: HTTP client & server (the bridge to building REST services).

## Notes
- 35-json-gson uses the Gson library; 44/45 use Java's built-in networking.
  These need the library on the classpath if run outside a Maven/Gradle project
  — ask if you hit a "package does not exist" error.
- Thread exercises (40) print in a non-deterministic order — that's correct.
- Start at 01 and don't skip; the later rungs assume the earlier ones.
