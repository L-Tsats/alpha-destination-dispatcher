// EXERCISE 26 - Custom Iterable (reference solution)

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CustomIterable {

    public static void main(String[] args) {
        Playlist playlist = new Playlist();
        playlist.add("Enter Sandman");
        playlist.add("Master of Puppets");
        playlist.add("One");

        // Works because Playlist implements Iterable<String>
        for (String song : playlist) {
            System.out.println(song);
        }
    }
}

// A Playlist "has-a" List of songs. By implementing Iterable<String> and
// delegating to the list's iterator, a Playlist can be used in a for-each
// exactly like a built-in collection.
class Playlist implements Iterable<String> {

    private final List<String> songs = new ArrayList<>();

    public void add(String song) {
        songs.add(song);
    }

    @Override
    public Iterator<String> iterator() {
        return songs.iterator();   // delegate to the backing list
    }
}
