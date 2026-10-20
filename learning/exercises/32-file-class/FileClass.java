/*
 * EXERCISE 32: The File class — inspecting the filesystem  (FILE I/O rung 4 of 4)
 * ===============================================================================
 * The last three rungs read/wrote the CONTENTS of files. This one is about the
 * files THEMSELVES — their names, sizes, whether they exist, what's in a folder.
 * No streams, no loops-until-(-1); just questions about the filesystem.
 *
 * THE IDEA
 * --------
 * A java.io.File object represents a PATH (to a file or folder). Creating one
 * does NOT open or create anything — it's just a handle you ask questions:
 *
 *       File f = new File("sample.txt");
 *       f.exists()        // true if the path exists
 *       f.length()        // size in bytes (for a file)
 *       f.isDirectory()   // is it a folder?
 *       f.isFile()        // is it a regular file?
 *       f.getName()       // just the name
 *       f.lastModified()  // last-change time (millis since 1970; wrap in new Date(...))
 *
 * For a FOLDER, list its contents:
 *       File dir = new File(".");          // "." = the current folder
 *       File[] entries = dir.listFiles();  // array of File objects inside
 *       for (File e : entries) { ... }
 *
 * Other handy ones (be careful with delete/mkdir — they change the disk):
 *       dir.mkdir()       // create a new folder
 *       f.renameTo(other) // rename/move
 *       f.delete()        // delete  (destructive — don't aim it at real files!)
 *
 * No IOException to catch for these query methods — they just return
 * booleans/numbers. (The methods that change the disk return a boolean telling
 * you if they succeeded.)
 *
 * Imports:
 *       import java.io.File;
 *       import java.util.Date;   // only if you format lastModified()
 *
 * YOUR TASK
 * ---------
 * 1. Make a File for the CURRENT folder:  new File(".")  and confirm
 *    exists() and isDirectory() are true.
 * 2. List everything in it with listFiles(); for each entry, print its
 *    getName(), whether it isDirectory(), and (for files) its length().
 *    Tip: for a File[] you can loop  for (File e : entries) { ... }
 * 3. Make a File for a path that does NOT exist (e.g. "nope.txt") and show
 *    that exists() is false — proving a File is just a handle, not the data.
 * 4. BONUS (safe): create a new folder with mkdir() (e.g. "scratch"), check it
 *    now exists(), then delete() it. Only touch names you created!
 *
 */
