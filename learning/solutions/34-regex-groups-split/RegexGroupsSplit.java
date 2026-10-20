// EXERCISE 34 - Regex groups & split (reference solution)

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexGroupsSplit {

    public static void main(String[] args) {
        // 1) Capturing groups on a d/m/y date
        Pattern date = Pattern.compile("(\\d+)/(\\d+)/(\\d+)");
        Matcher m = date.matcher("26/01/2004");
        if (m.matches()) {
            System.out.println("group(0) whole = " + m.group(0)); // 26/01/2004
            System.out.println("group(1) day   = " + m.group(1)); // 26
            System.out.println("group(2) month = " + m.group(2)); // 01
            System.out.println("group(3) year  = " + m.group(3)); // 2004

            // 4) BONUS: where was the day group found?
            System.out.println("day group spans index " + m.start(1) + ".." + m.end(1));
        }

        // 2) Groups via find(), inside a larger string
        Pattern email = Pattern.compile("(\\w+)@(\\w+)");
        Matcher em = email.matcher("contact malan@harvard for info");
        if (em.find()) {
            System.out.println("\nuser = " + em.group(1) + ", host = " + em.group(2));
        }

        // 3) Split on runs of non-letters
        Pattern sep = Pattern.compile("[^a-zA-Z]+");
        String[] words = sep.split("one, two3four\tfive!");
        System.out.println("\nwords:");
        for (String w : words) {
            System.out.println("  " + w);
        }
    }
}
