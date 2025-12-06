package CTS.event;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// No need for a separate MockArtist class now. We use the real Artist class.

class LineUpTest {

    // Use the real Artist class with its required 3-argument constructor
    private final Artist ARTIST_A = new Artist(101, "The Headliners", "Rock");
    private final Artist ARTIST_B = new Artist(102, "Opening Act", "Pop");
    private final int EVENT_ID = 5;

    // ===============================================
    //  CONSTRUCTOR & GETTERS TESTS
    // ===============================================

    @Test
    void testConstructorAndGetters() {
        LineupEntry entry = new LineupEntry(EVENT_ID, 1, "Short set", ARTIST_A);
        
        assertEquals(EVENT_ID, entry.getEventId(), "Event ID should match constructor value.");
        assertEquals(1, entry.getPosition(), "Position should match constructor value.");
        assertEquals("Short set", entry.getNotes(), "Notes should match constructor value.");
        assertEquals(ARTIST_A.getArtistId(), entry.getArtist().getArtistId(), "Artist object should be set and accessible.");
    }
    
    // ===============================================
    //  BUSINESS LOGIC & SETTER TESTS
    // ===============================================

    @Test
    void testReorder() {
        LineupEntry entry = new LineupEntry(EVENT_ID, 3, "Original position", ARTIST_A);
        
        entry.reorder(1); // Move to opening act
        
        assertEquals(1, entry.getPosition(), "Position must be updated by reorder method.");
    }
    
    @Test
    void testSetNotes() {
        LineupEntry entry = new LineupEntry(EVENT_ID, 1, "Old notes", ARTIST_A);
        entry.setNotes("New and improved notes");
        
        assertEquals("New and improved notes", entry.getNotes(), "Notes setter should update the field.");
    }
    
    @Test
    void testSetArtist() {
        LineupEntry entry = new LineupEntry(EVENT_ID, 1, "Notes", ARTIST_A);
        entry.setArtist(ARTIST_B);
        
        assertEquals(ARTIST_B.getArtistId(), entry.getArtist().getArtistId(), "Artist setter should update the artist object.");
    }

    // ===============================================
    //  CSV PERSISTENCE TESTS (toCsvRow)
    // ===============================================

    @Test
    void testToCsvRow_Standard() {
        LineupEntry entry = new LineupEntry(EVENT_ID, 2, "Extended Set", ARTIST_A);
        // Artist ID is 101
        String expected = "5,2,101,Extended Set";
        
        assertEquals(expected, entry.toCsvRow(), "CSV output should match format: eventId,position,artistId,notes");
    }
    
    @Test
    void testToCsvRow_NotesEscaping() {
        // Test notes with a comma and a backslash, which must be escaped by the escape() method
        LineupEntry entry = new LineupEntry(EVENT_ID, 1, "Setlist: Pop\\Rock, Acoustic", ARTIST_B);
        // Expected format: backslash becomes double backslash (\\ -> \\\\) and comma becomes escaped comma (\,).
        String expected = "5,1,102,Setlist: Pop\\\\Rock\\, Acoustic"; 
        
        assertEquals(expected, entry.toCsvRow(), "Notes with separators must be correctly escaped.");
    }
    
    @Test
    void testToCsvRow_NullArtistAndNotes() {
        // Null artist ID should be -1, null notes become empty string
        LineupEntry entry = new LineupEntry(EVENT_ID, 4, null, null);
        String expected = "5,4,-1,";
        
        assertEquals(expected, entry.toCsvRow(), "Null artist should result in -1 ID and null notes in empty string.");
    }

}