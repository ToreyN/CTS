package CTS.event;

public class LineupEntry {
    private int position;     // Running order (1 = opener)
    private String notes;     // Optional notes (“acoustic set”, “guest feature”)

    private Artist artist;    // Each entry references one artist

    public LineupEntry(int position, String notes, Artist artist) {
        this.position = position;
        this.notes = notes;
        this.artist = artist;
    }

    public int getPosition() {
        return position;
    }

    public void reorder(int newPosition) {
        this.position = newPosition;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "LineupEntry{" +
                "position=" + position +
                ", notes='" + notes + '\'' +
                ", artist=" + (artist != null ? artist.getStageName() : "null") +
                '}';
    }

}
