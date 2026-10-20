/*
 * EXERCISE 17: this, constructors, set/get & encapsulation
 * ========================================================
 * (Material: Chapter 8 — encapsulation & data hiding, the "this" reference,
 *            overloaded constructors, this(...) constructor call,
 *            set/get mutators & accessors, data validation)
 *
 * KEY CONCEPTS:
 *
 * ENCAPSULATION & DATA HIDING:
 * - Mark instance variables "private" so outside code can't touch them directly.
 * - Expose controlled access through "public" methods. This keeps an object in
 *   a consistent state. (A Time object should never hold hour = 99.)
 *
 * set/get METHODS:
 * - A SETTER (mutator) changes a field and can VALIDATE the new value first.
 * - A GETTER (accessor) returns a field's value.
 *   A validating setter is why private + get/set is better than a public field.
 *
 * THE "this" REFERENCE:
 * - Inside a non-static method, "this" refers to the current object.
 * - Its most common use: when a parameter has the SAME NAME as a field,
 *   "this.hour" means the field and "hour" means the parameter.
 *       this.hour = hour;   // field = parameter
 *
 * OVERLOADED CONSTRUCTORS:
 * - A class can have several constructors with different parameter lists, to
 *   allow creating objects in different ways (no args, partial, full).
 * - this(...) as the FIRST statement in a constructor calls ANOTHER constructor
 *   of the same class, so you don't repeat initialization code.
 *
 * TRY:
 * 1. Create a Time with new Time() (defaults 0:0:0) and print it.
 * 2. Set an invalid hour (e.g. 50) and confirm the setter rejects it.
 * 3. Add a Time(int hour, int minute) constructor that delegates with this(...).
 * 4. Add a getSecond()/setSecond() pair following the same pattern.
 */
public class ThisConstructorsEncapsulation {

    public static void main(String[] args) {
        // No-arg constructor -> all zeros
        Time t1 = new Time();
        System.out.println("t1 (default): " + t1.toUniversalString());

        // Full constructor
        Time t2 = new Time(13, 27, 6);
        System.out.println("t2:           " + t2.toUniversalString());

        // Validation in action: 50 is not a valid hour, so it is rejected
        System.out.println("\nTrying to set hour = 50 on t2...");
        boolean ok = t2.setHour(50);
        System.out.println("Accepted? " + ok + "  ->  " + t2.toUniversalString());

        // A valid change
        t2.setHour(9);
        System.out.println("After setHour(9): " + t2.toUniversalString());
    }
}

/*
 * Time keeps the time of day in 24-hour form. All fields are private; the only
 * way in is through validating setters, so a Time is always consistent.
 */
class Time {

    private int hour;    // 0 - 23
    private int minute;  // 0 - 59
    private int second;  // 0 - 59

    // No-argument constructor: delegates to the full one with this(...)
    public Time() {
        this(0, 0, 0);   // must be the first statement
    }

    // Full constructor
    public Time(int hour, int minute, int second) {
        // "this.hour" is the field; "hour" is the parameter (same name)
        this.setHour(hour);
        this.setMinute(minute);
        this.setSecond(second);
    }

    // Setters return true if the value was valid and applied, false otherwise
    public boolean setHour(int hour) {
        if (hour >= 0 && hour < 24) {
            this.hour = hour;
            return true;
        }
        return false; // reject invalid value, leave field unchanged
    }

    public boolean setMinute(int minute) {
        if (minute >= 0 && minute < 60) {
            this.minute = minute;
            return true;
        }
        return false;
    }

    public boolean setSecond(int second) {
        if (second >= 0 && second < 60) {
            this.second = second;
            return true;
        }
        return false;
    }

    // Getters
    public int getHour()   { return hour; }
    public int getMinute() { return minute; }
    public int getSecond() { return second; }

    // Produce "HH:MM:SS"
    public String toUniversalString() {
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }
}
