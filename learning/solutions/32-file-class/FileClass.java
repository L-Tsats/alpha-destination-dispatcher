// EXERCISE 32 - The File class: inspecting the filesystem (reference solution)

import java.io.File;
import java.util.Date;

public class FileClass {

    public static void main(String[] args) {
        // "." is the current working directory (the module folder when run via Maven)
        File here = new File(".");
        System.out.println("exists?      " + here.exists());
        System.out.println("isDirectory? " + here.isDirectory());

        System.out.println("\n=== contents of current folder ===");
        File[] entries = here.listFiles();
        if (entries != null) {
            for (File e : entries) {
                if (e.isDirectory()) {
                    System.out.printf("[dir]  %s%n", e.getName());
                } else {
                    System.out.printf("[file] %-20s %d bytes  (modified %s)%n",
                            e.getName(), e.length(), new Date(e.lastModified()));
                }
            }
        }

        // A File is just a handle to a PATH; the path need not exist.
        File missing = new File("nope.txt");
        System.out.println("\nnope.txt exists? " + missing.exists());

        // BONUS: create and remove a folder we own (safe, destructive methods
        // return a boolean telling you whether they succeeded).
        File scratch = new File("scratch");
        System.out.println("\nmkdir scratch:  " + scratch.mkdir());
        System.out.println("scratch exists? " + scratch.exists());
        System.out.println("delete scratch: " + scratch.delete());
    }
}
