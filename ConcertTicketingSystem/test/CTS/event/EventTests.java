package CTS.event;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import CTS.enums.EventStatus;
import CTS.misc.Money;

class EventTests {

    private Event testEvent;
    private final int INITIAL_CAPACITY = 100;
    private final Money TEST_PRICE = new Money(25.00, "USD");
    private final Path TEST_CSV_PATH = Path.of("test_event.csv");

    @BeforeEach
    void setUp() throws Exception {
        // Create a standard test event before each test
        testEvent = new Event(
            1, 
            "Rock Concert", 
            new Date(), 
            "Stadium A", 
            "A loud music event", 
            INITIAL_CAPACITY, 
            EventStatus.DRAFT, 
            TEST_PRICE
        );
    }

    @AfterEach
    void tearDown() throws Exception {
        // Clean up the temporary CSV file if it was created during testing
        Files.deleteIfExists(TEST_CSV_PATH);
    }

    // ===============================================
    //  TICKET SALES AND CAPACITY LOGIC TESTS
    // ===============================================

    @Test
    void testInitialStateAndGetters() {
        // Check initial numeric and object states
        assertEquals(INITIAL_CAPACITY, testEvent.getCapacity(), "Capacity should match constructor value.");
        assertEquals(0, testEvent.getTicketsSold(), "Initially, ticketsSold should be 0.");
        assertEquals(INITIAL_CAPACITY, testEvent.getAvailableSeats(), "Available seats should equal capacity initially.");
        assertEquals(TEST_PRICE, testEvent.getBasePrice(), "Base price should be set correctly.");
        assertEquals(EventStatus.DRAFT, testEvent.getStatus(), "Initial status should be DRAFT.");
    }

    @Test
    void testSellTicketSuccess() {
        // Check boolean result (expected true)
        assertEquals(true, testEvent.sellTicket(), "Should successfully sell a ticket when seats are available (returns true).");
        
        // Check state changes
        assertEquals(1, testEvent.getTicketsSold(), "Tickets sold count should increase by 1.");
        assertEquals(INITIAL_CAPACITY - 1, testEvent.getAvailableSeats(), "Available seats should decrease by 1.");
    }

    @Test
    void testSellTicketFailureWhenSoldOut() {
        // Sell out the event 
        for (int i = 0; i < INITIAL_CAPACITY; i++) {
            testEvent.sellTicket();
        }
        
        // Try to sell one more ticket (expected false)
        assertEquals(false, testEvent.sellTicket(), "Should return false when trying to sell past capacity.");
        
        // Check final state
        assertEquals(INITIAL_CAPACITY, testEvent.getTicketsSold(), "Tickets sold count should not exceed capacity.");
        assertEquals(0, testEvent.getAvailableSeats(), "Available seats should be 0 when sold out.");
    }
    
    @Test
    void testUnSellTicket() {
        // Sell a ticket first
        testEvent.sellTicket(); 
        
        // Unsell the ticket
        testEvent.unSellTicket();
        assertEquals(0, testEvent.getTicketsSold(), "Tickets sold count should decrease to 0.");
        
        // Ensure count does not go negative
        testEvent.unSellTicket();
        assertEquals(0, testEvent.getTicketsSold(), "Tickets sold count should not go below 0.");
    }

    // ===============================================
    //  STATUS CHANGE LOGIC TESTS
    // ===============================================

    @Test
    void testPublishEvent() {
        // Check status change to PUBLISHED
        testEvent.publish();
        assertEquals(EventStatus.PUBLISHED, testEvent.getStatus(), "Status should change from DRAFT to PUBLISHED.");
        
        // Test that publishing again does nothing
        testEvent.publish();
        assertEquals(EventStatus.PUBLISHED, testEvent.getStatus(), "Status should remain PUBLISHED.");
    }
    
    @Test
    void testCancelEvent() {
        testEvent.publish(); // First set to published
        testEvent.cancel();
        assertEquals(EventStatus.CANCELED, testEvent.getStatus(), "Status should change to CANCELED.");
    }

    // ===============================================
    //  CSV PERSISTENCE TESTS
    // ===============================================
    
    @Test
    void testToCsvRow_NewFieldsIncluded() {
        // scenario to test all fields
        testEvent.updateDescription("A huge, fantastic event.");
        testEvent.setCapacity(500);
        testEvent.sellTicket(); // ticketsSold = 1
        testEvent.publish();    // status = PUBLISHED
        
        String csvRow = testEvent.toCsvRow();
        String[] parts = csvRow.split(",");
        
        assertEquals(10, parts.length, "CSV row must now contain 10 parts");
        assertEquals("500", parts[6], "capacity part should be correct."); // <-- FIX 1

     // Subsequent fields must also be checked for index shift:
     // Status was parts[6], now parts[7]
     assertEquals("PUBLISHED", parts[7], "status part should be correct."); // <-- FIX 2

     // Price was parts[7], now parts[8]
     assertEquals("25.0:USD", parts[8], "price inline string should be correct.");

     // Tickets Sold was parts[8], now parts[9]
     assertEquals("1", parts[9], "tickets sold count should be correct."); // <-- FIX 4
    }

    @Test
    void testFromCsvRow_LoadsAllFieldsCorrectly() {
        // Setup a full CSV line 
        String csvLine = "2,Venue Show,1672531200000,Concert Hall,Amazing\\, event,250,CANCELED,120.00:EUR,15";
        
        // Execute the static loading method
        Event loadedEvent = Event.fromCsvRow(csvLine);
        
       
        
        // Check core properties
        assertEquals(2, loadedEvent.getEventId(), "ID should be correct.");
        assertEquals("Amazing, event", loadedEvent.getDescription(), "description should be unescaped correctly.");
        assertEquals(EventStatus.CANCELED, loadedEvent.getStatus(), "status should be CANCELED.");
        assertEquals(new Money(120.00, "EUR"), loadedEvent.getBasePrice(), "Base price should load correctly as Money object.");
        assertEquals(15, loadedEvent.getTicketsSold(), "tickets sold count should load correctly.");
        assertEquals(235, loadedEvent.getAvailableSeats(), "available seats should be correctly calculated after loading.");
    }
    
    @Test
    void testSaveAndLoadIntegration() throws IOException {
        // Prepare data (set non-default values)
        testEvent.sellTicket(); // Sold 1
        testEvent.publish();
        
        List<Event> originalList = new ArrayList<>();
        originalList.add(testEvent);
        
        // Save to CSV
        Event.saveToCsv(TEST_CSV_PATH, originalList);
        
        // Load from CSV
        List<Event> loadedList = Event.loadFromCsv(TEST_CSV_PATH);
        
        // Assertions
        assertEquals(1, loadedList.size(), "Should load exactly one event.");
        Event loadedEvent = loadedList.get(0);
        
        // Check core values
        assertEquals(testEvent.getEventId(), loadedEvent.getEventId(), "Event ID must persist.");
        assertEquals(testEvent.getTicketsSold(), loadedEvent.getTicketsSold(), "Sold count must persist through save/load.");
        assertEquals(testEvent.getBasePrice(), loadedEvent.getBasePrice(), "Price must persist through save/load.");
        assertEquals(testEvent.getAvailableSeats(), loadedEvent.getAvailableSeats(), "Available seats calculation must be consistent.");
    }
}