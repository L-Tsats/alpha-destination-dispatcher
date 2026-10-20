/* PATTERN SYMBOLS CHEAT-SHEET (from the Spinellis material):
 *   ^   start of line        $   end of line        .   any single char
 *   \d  a digit              \D  a non-digit        \s  whitespace   \S non-ws
 *   \w  word char (letter/digit/_)                  [abc] one of a,b,c
 *   [a-z] a char in range    [^abc] any char EXCEPT a,b,c
 *   X*  zero or more X       X+  one or more X      X?  zero or one X
 *   X{n} exactly n           X{n,} n or more        X{n,m} between n and m
 *   A|B  A or B              (...) a group (rung 34)
*/

// EXERCISE 33 - Regex basics (reference solution)

import java.util.regex.Pattern;

public class RegexBasics {

    public static void main(String[] args) {
        // 1) matches() (whole string) vs find() (anywhere) with \d+
        Pattern digits = Pattern.compile("\\d+");
        System.out.println("matches on '12345': " + digits.matcher("12345").matches());  // true
        System.out.println("matches on 'abc123': " + digits.matcher("abc123").matches()); // false
        System.out.println("find    on 'abc123': " + digits.matcher("abc123").find());    // true

        // 2) exactly 5 digits
        Pattern five = Pattern.compile("\\d{5}");
        System.out.println("\n'80234' is 5 digits? " + five.matcher("80234").matches()); // true
        System.out.println("'8023' is 5 digits?  " + five.matcher("8023").matches());    // false

        // 3) ^ anchors to the start of the string
        Pattern abo = Pattern.compile("^abo"); // 
        System.out.println("\nfind ^abo in 'aboard': " + abo.matcher("aboard").find()); // true
        System.out.println("find ^abo in 'taboo':  " + abo.matcher("taboo").find());    // false

        // 4) 4+ consecutive NON-vowels (here: [^...] is: NOT these)
        Pattern cons = Pattern.compile("[^aeiou]{4,}");
        System.out.println("\n4+ non-vowels in 'corkscrew': "
                + cons.matcher("corkscrew").find()); // true (e.g. "rkscr")
    }
}
