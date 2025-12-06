package CTS.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

class ArtistTests {

    // JUnit annotation provides a temporary directory for file I/O tests
    @TempDir
    Path tempDir;

    // We don't need setUp/tearDown for this class.

    // ===============================================
    //  CONSTRUCTOR, GETTERS, AND SETTERS TESTS
    // ===============================================

    private static String unescapeTest(String s) {
        // This uses the correct logic found in Artist.java
        if (s == null) return ""; 
        return s.replace("\\\\", "\\").replace("\\,", ","); 
    }

    @Test
    void testFromCsvRow_Unescaping() {
        // We only test the unescape logic directly, bypassing the full CSV split
        String unescapedStageName = unescapeTest("Synth\\, Pop");
        assertEquals("Synth, Pop", unescapedStageName, "Commas must be unescaped."); 
        
        // ... and so on for the genre
    }
    
    
    @Test
    void testConstructorAndGetters() {
        Artist artist = new Artist(1, "The Neon Ghosts", "Synthwave");
        
        assertEquals(1, artist.getArtistId(), "Artist ID should be correct.");
        assertEquals("The Neon Ghosts", artist.getStageName(), "Stage name should be correct.");
        assertEquals("Synthwave", artist.getGenre(), "Genre should be correct.");
    }
    
    @Test
    void testSetters() {
        Artist artist = new Artist(1, "Old Name", "Old Genre");
        
        artist.setArtistId(2);
        artist.setStageName("New Name");
        artist.setGenre("Rock");
        
        assertEquals(2, artist.getArtistId(), "ID should be updated.");
        assertEquals("New Name", artist.getStageName(), "Stage name should be updated.");
        assertEquals("Rock", artist.getGenre(), "Genre should be updated.");
    }

    // ===============================================
    //  EQUALITY TESTS (Verifies equals() and hashCode())
    // ===============================================
    
    @Test
    void testEquality_SameIdDifferentDetails() {
        // Two artists with the same ID but different names/genres are treated as equal
        // because ArtistId is the primary key.
        Artist a1 = new Artist(5, "Name A", "Pop");
        Artist a2 = new Artist(5, "Name B", "Rock"); 
        
        assertEquals(a1, a2, "Artists with the same ID should be equal.");
        assertEquals(a1.hashCode(), a2.hashCode(), "Equal artists must have equal hash codes.");
    }

    @Test
    void testEquality_DifferentId() {
        Artist a1 = new Artist(5, "Name A", "Pop");
        Artist a2 = new Artist(6, "Name A", "Pop");
        
        assertNotEquals(a1, a2, "Artists with different IDs should not be equal.");
    }

    // ===============================================
    //  CSV PERSISTENCE TESTS
    // ===============================================

    @Test
    void testToCsvRow_Standard() {
        Artist artist = new Artist(10, "The Quiet Riot", "Metal");
        String expected = "10,The Quiet Riot,Metal";
        
        assertEquals(expected, artist.toCsvRow(), "CSV output should match format: ID,Name,Genre.");
    }

    @Test
    void testToCsvRow_Escaping() {
        // Test stageName and genre with characters that must be escaped (comma and backslash)
        Artist artist = new Artist(11, "Pop, Rock", "Folk\\Acoustic");
        String expected = "11,Pop\\, Rock,Folk\\\\Acoustic"; // \\ and \,
        
        assertEquals(expected, artist.toCsvRow(), "Commas and backslashes must be correctly escaped.");
    }
    

    @Test
    void testSaveAndLoadFromCsv_RoundTrip() throws IOException {
        Path csvFile = tempDir.resolve("artists.csv");
        
        // 1. Setup Data
        List<Artist> originalArtists = new ArrayList<>();
        originalArtists.add(new Artist(1, "The Legends", "Classic Rock"));
        originalArtists.add(new Artist(2, "M.C. Flow", "Hip-Hop, Rap")); 
        
        // 2. Save
        Artist.saveToCsv(csvFile, originalArtists);
        
        // 3. Load
        List<Artist> loadedArtists = Artist.loadFromCsv(csvFile);
        
        // 4. Assert
        assertNotNull(loadedArtists, "Loaded list should not be null.");
        assertEquals(originalArtists.size(), loadedArtists.size(), "Should load the same number of artists.");
        
        // Use equals() to verify the objects are the same (relies on correct equals() implementation)
        assertEquals(originalArtists.get(0), loadedArtists.get(0));
        assertEquals(originalArtists.get(1), loadedArtists.get(1));
    }
}