package CTS.event;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LineupEntry {
    private int position;
    private String notes;
    private Artist artist;

    
    private int eventId;   

    public LineupEntry(int eventId, int position, String notes, Artist artist) {
        this.eventId = eventId;
        this.position = position;
        this.notes = notes;
        this.artist = artist;
    }

    public int getEventId() {
        return eventId;
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

    // ===== CSV support =====
    // Format: eventId,position,artistId,notes

    public String toCsvRow() {
        int artistId = (artist != null) ? artist.getArtistId() : -1;
        return eventId + "," +
                position + "," +
                artistId + "," +
                escape(notes);
    }

    
    public static LineupEntry fromCsvRow(String line, Artist artist) {
        String[] parts = line.split(",", 4);
        int eventId = Integer.parseInt(parts[0]);
        int pos = Integer.parseInt(parts[1]);
        String notes = parts.length > 3 ? unescape(parts[3]) : "";
        return new LineupEntry(eventId, pos, notes, artist);
    }

    
    public static class RawLineupRow {
        public final int eventId;
        public final int position;
        public final int artistId;
        public final String notes;

        public RawLineupRow(int eventId, int position, int artistId, String notes) {
            this.eventId = eventId;
            this.position = position;
            this.artistId = artistId;
            this.notes = notes;
        }
    }

    public static List<RawLineupRow> loadRawRows(Path path) throws IOException {
        List<RawLineupRow> result = new ArrayList<>();
        if (!Files.exists(path)) {
            return result;
        }
        for (String line : Files.readAllLines(path)) {
            if (line.trim().isEmpty() || line.startsWith("#")) continue;
            String[] parts = line.split(",", 4);
            int eventId = Integer.parseInt(parts[0]);
            int pos = Integer.parseInt(parts[1]);
            int artistId = Integer.parseInt(parts[2]);
            String notes = parts.length > 3 ? unescape(parts[3]) : "";
            result.add(new RawLineupRow(eventId, pos, artistId, notes));
        }
        return result;
    }

    public static void saveToCsv(Path path, List<LineupEntry> entries) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# eventId,position,artistId,notes");
        for (LineupEntry e : entries) {
            lines.add(e.toCsvRow());
        }
        Files.write(path, lines);
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace(",", "\\,");
    }

    private static String unescape(String s) {
        return s.replace("\\,", ",").replace("\\\\", "\\");
    }

    @Override
    public String toString() {
        return "LineupEntry{" +
                "eventId=" + eventId +
                ", position=" + position +
                ", notes='" + notes + '\'' +
                ", artist=" + (artist != null ? artist.getStageName() : "null") +
                '}';
    }

}
