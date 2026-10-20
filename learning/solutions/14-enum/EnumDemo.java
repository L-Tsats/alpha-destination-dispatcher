/*
 * EXERCISE 14: Enumerations (enum)
 * ================================
 * (Material: Chapter 6 Part II — enum type, Status.WON/LOST/CONTINUE example)
 *
 * KEY CONCEPTS:
 * - An ENUM declares a fixed set of named constants. It is a special kind of
 *   class introduced with the keyword "enum":
 *       enum Status { WON, LOST, CONTINUE }
 * - A variable of an enum type can only hold one of those declared constants —
 *   nothing else. This is safer and clearer than using "magic numbers"
 *   (0, 1, 2) whose meaning you have to remember.
 * - CONVENTION: enum constants are written in ALL CAPS, like other constants.
 * - Enums work beautifully with switch (you don't even write the enum name in
 *   the case labels):
 *       switch (status) { case WON: ...; }
 * - Useful built-in methods:
 *       values()    -> array of all constants (great for looping)
 *       name()      -> the constant's name as a String
 *       ordinal()   -> its position (0-based)
 *
 * TRY:
 * 1. Add a CONTINUE branch message in the switch.
 * 2. Loop over Day.values() and print each day's name and ordinal.
 * 3. Add a method isWeekend() idea: in main, print whether a Day is SAT/SUN.
 * 4. Read a number 1-7 and map it to the matching Day via values()[n-1].
 */
public class EnumDemo {

    // A simple enum describing the state of a game (as in the Craps example)
    enum Status {
        WON, LOST, CONTINUE
    }

    // Another enum: days of the week
    enum Day {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    public static void main(String[] args) {
        // Using an enum value with a switch
        Status status = Status.WON;
        System.out.print("Game result: ");
        switch (status) {
            case WON:
                System.out.println("You win!");
                break;
            case LOST:
                System.out.println("You lose.");
                break;
            case CONTINUE:
                System.out.println("Roll again...");
                break;
        }

        // values() gives all constants; name() and ordinal() describe each one
        System.out.println("\n=== Days of the week ===");
        for (Day d : Day.values()) {
            System.out.printf("%-10s (ordinal %d)%n", d.name(), d.ordinal());
        }

        // Map a number 1..7 to a Day via the values() array
        int dayNumber = 6; // 1 = Monday ... 7 = Sunday
        Day chosen = Day.values()[dayNumber - 1];
        boolean weekend = (chosen == Day.SATURDAY || chosen == Day.SUNDAY);
        System.out.printf("%nDay %d is %s — weekend? %b%n", dayNumber, chosen, weekend);
    }
}
