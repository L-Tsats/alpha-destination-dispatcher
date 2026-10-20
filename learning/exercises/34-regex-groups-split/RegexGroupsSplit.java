/*
 * EXERCISE 34: Regex — capturing groups & splitting  (DATA-REP rung 2 of 4)
 * =========================================================================
 * Matching tells you IF text fits a pattern (rung 33). This rung is about
 * EXTRACTING pieces out of it, and SPLITTING text on a pattern.
 *
 * CAPTURING GROUPS
 * ----------------
 * Parentheses (...) in a pattern mark a GROUP — a sub-part you want to pull
 * out. After a successful match you read the groups:
 *       Pattern p = Pattern.compile("(\\d+):(\\d+)");   // two groups
 *       Matcher m = p.matcher("09:30");
 *       if (m.matches()) {
 *           String hh = m.group(1);   // "09"  (first parenthesis)
 *           String mm = m.group(2);   // "30"  (second parenthesis)
 *       }
 *   - group(0) is the ENTIRE match; group(1), group(2), ... are the
 *     parenthesised parts, numbered left to right by their opening '('.
 *   - You must call find() or matches() FIRST; only then are the groups filled.
 *   - m.start(n) / m.end(n) give the index positions of group n in the string
 *     (that's how the Spinellis WebStats example slices substrings).
 *
 * SPLITTING
 * ---------
 * Pattern.split(text) breaks a string into a String[] wherever the pattern
 * occurs (the separators are consumed, not kept):
 *       Pattern sep = Pattern.compile("[^a-zA-Z]+");   // runs of non-letters
 *       String[] words = sep.split("Hello, world! 42 foo");
 *       // -> ["Hello", "world", "foo"]
 *   This is how you tokenise text when the delimiters vary (commas, spaces,
 *   tabs, digits...). (String also has a convenience  s.split(regex)  that does
 *   the same without compiling a Pattern first.)
 *
 * Imports:  import java.util.regex.*;
 *
 * YOUR TASK
 * ---------
 * 1. GROUPS: compile "(\\d+)/(\\d+)/(\\d+)" (a d/m/y date). matcher() it on
 *    "26/01/2004", call matches(), then print group(1), group(2), group(3)
 *    and group(0). Confirm group(0) is the whole thing and 1..3 are the parts.
 * 2. GROUPS via find: compile "(\\w+)@(\\w+)" , find() it inside
 *    "contact malan@harvard for info", and print the two groups (user, host).
 * 3. SPLIT: compile "[^a-zA-Z]+" and split a messy line like
 *    "one, two3four\tfive!" into words; loop and print each word.
 * 4. BONUS: use m.start(1)/m.end(1) from task 1 to print WHERE in the string
 *    the day group was found.
 *
 */
