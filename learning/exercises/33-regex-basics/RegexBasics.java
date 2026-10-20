/*
 * EXERCISE 33: Regular expressions — basics  (DATA-REP rung 1 of 4)
 * ================================================================
 * A regular expression (regex) describes a TEXT PATTERN. You then ask "does
 * this string match the pattern?" or "does the pattern occur anywhere in it?".
 * You already met regex in CS50P (numb3rs, um, watch) — this is the same idea
 * in Java, which exposes it through two classes: Pattern and Matcher.
 *
 * THE JAVA DANCE (3 steps):
 *   1. COMPILE the pattern once:   Pattern p = Pattern.compile("\\d+");
 *   2. Make a MATCHER for a string: Matcher m = p.matcher(someString);
 *   3. Ask a question:
 *        m.matches()  -> does the WHOLE string match, start to end?
 *        m.find()     -> does the pattern occur SOMEWHERE in the string?
 *      Both return a boolean.
 *   (Python's re hides the Pattern/Matcher split; Java makes it explicit.)
 *
 * BACKSLASHES — the one gotcha: in a Java string literal, '\' must be written
 * '\\'. So the regex  \d  (a digit) is written  "\\d"  in Java source. The
 * regex  \.  (a literal dot) is  "\\."  . This trips everyone up at first.
 *
 * PATTERN SYMBOLS CHEAT-SHEET (from the Spinellis material):
 *   ^   start of line        $   end of line        .   any single char
 *   \d  a digit              \D  a non-digit        \s  whitespace   \S non-ws
 *   \w  word char (letter/digit/_)                  [abc] one of a,b,c
 *   [a-z] a char in range    [^abc] any char EXCEPT a,b,c
 *   X*  zero or more X       X+  one or more X      X?  zero or one X
 *   X{n} exactly n           X{n,} n or more        X{n,m} between n and m
 *   A|B  A or B              (...) a group (rung 34)
 *
 * Imports:  import java.util.regex.Pattern;  import java.util.regex.Matcher;
 *           (or just: import java.util.regex.*;)
 *
 * YOUR TASK (print each result so you can see true/false):
 * 1. Compile a pattern for "one or more digits":  "\\d+"  (remember the \\).
 *    - Use matches() on "12345" (whole string is digits -> true)
 *    - Use matches() on "abc123" (whole string is NOT all digits -> false)
 *    - Use find()    on "abc123" (digits occur somewhere -> true)
 *    This contrast between matches() and find() is the key lesson.
 * 2. Compile a pattern that checks a string is a 5-digit number: "\\d{5}"
 *    and test matches() on "80234" and on "8023".
 * 3. Compile "^abo" and use find() on "aboard" and "taboo" — ^ anchors to the
 *    start, so only "aboard" should find it.
 * 4. Compile "[^aeiou]{4,}" (4+ consecutive non-vowels) and find() it in a
 *    word like "corkscrew".
 *
 */
