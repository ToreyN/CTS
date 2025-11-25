package CTS.event;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Artist {
   private int artistId;
    private String stageName;
    private String genre;

    public Artist(int artistId, String stageName, String genre) {
        this.artistId = artistId;
        this.stageName = stageName;
        this.genre = genre;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    // ================= CSV SUPPORT =================

    /**
     * CSV format:
     * artistId,stageName,genre
     */
    public String toCsvRow() {
        return artistId + "," +
                escape(stageName) + "," +
                escape(genre);
    }

    public static Artist fromCsvRow(String line) {
        String[] parts = line.split(",", -1);
        int id = Integer.parseInt(parts[0]);
        String stageName = unescape(parts[1]);
        String genre = unescape(parts[2]);
        return new Artist(id, stageName, genre);
    }

    public static List<Artist> loadFromCsv(Path path) throws IOException {
        List<Artist> result = new ArrayList<>();
        if (!Files.exists(path)) {
            return result;
        }
        for (String line : Files.readAllLines(path)) {
            if (line.trim().isEmpty() || line.startsWith("#")) {
                continue;
            }
            result.add(fromCsvRow(line));
        }
        return result;
    }

    public static void saveToCsv(Path path, List<Artist> artists) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# artistId,stageName,genre");
        for (Artist a : artists) {
            lines.add(a.toCsvRow());
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
        return "Artist{" +
                "artistId=" + artistId +
                ", stageName='" + stageName + '\'' +
                ", genre='" + genre + '\'' +
                '}';
    }

}
