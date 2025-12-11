package CTS.booking;

import CTS.misc.Money;
//  for CSV saving
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths; 
import java.util.ArrayList;
import java.util.List;

public class Ticket {

    private int ticketId;
    private int orderId;  // <-- NEW: Links back to the Order --- ADDED --- 11/26
    private int eventId;  // <-- NEW: Links to the Event --- ADDED --- 11/26
    
    private Money price;
    private String seatLabel; //  "Section 101, Row A, Seat 5"


//  Constructor for creating a new Ticket.

    public Ticket(int ticketId, int orderId, int eventId, Money price, String seatLabel) {
        this.ticketId = ticketId;
        this.orderId = orderId;
        this.eventId = eventId;
        this.price = price;
        this.seatLabel = seatLabel;
    }

    // --- Getters ---
    
    public int getTicketId() {
        return ticketId;
    }
    
    public int getOrderId() {
        return orderId;
    }

    public int getEventId() {
        return eventId;
    }

    public Money getPrice() {
        return price;
    }

    public String getSeatLabel() {
        return seatLabel;
    }
    
    

 // =========================================================================
    //  CSV PERSISTENCE
    // =========================================================================

    /**
     * Format: ticketId,orderId,eventId,priceInline,seatLabel
     * FIX: Now uses single priceInline column for Money consistency.
     */
    public String toCsvRow() {
        return ticketId + "," +
               orderId + "," +
               eventId + "," +
               price.toInlineString() + "," + // <-- USE INLINE FORMAT
               escape(seatLabel);
    }


    /**
     * Rebuilds a Ticket object from a CSV row (now expects 5 parts).
     * FIX: Uses Money.fromInlineString to correctly parse the price.
     */
    public static Ticket fromCsvRow(String line) {
        try {
            // EXPECTS 5 parts: ticketId, orderId, eventId, priceInline, seatLabel
            String[] parts = line.split(",", 5); 
            
            if (parts.length < 5) {
                 // Throw an error or skip silently for safety
                 throw new IllegalArgumentException("Expected 5 columns, found " + parts.length);
            }
            
            return new Ticket(
                Integer.parseInt(parts[0]), // ticketId
                Integer.parseInt(parts[1]), // orderId
                Integer.parseInt(parts[2]), // eventId
                Money.fromInlineString(unescape(parts[3])), // <-- USE INLINE PARSER
                unescape(parts[4]) // seatLabel
            );
        } catch (Exception e) {
            System.err.println("Skipping malformed ticket line: " + line + " — " + e.getMessage());
            return null;
        }
    }


    /**
     * Saves a list of all tickets to tickets.csv
     */
    public static void saveToCsv(Path path, List<Ticket> tickets) throws IOException {
        List<String> lines = new ArrayList<>();
        // FIX: Updated HEADER to match the new 5-column format
        lines.add("# ticketId,orderId,eventId,priceInline,seatLabel"); 
        for (Ticket t : tickets) {
            lines.add(t.toCsvRow());
        }
        Files.write(path, lines);
    }

//  Loads a list of all tickets from tickets.csv

    public static List<Ticket> loadFromCsv(Path path) throws IOException {
        List<Ticket> tickets = new ArrayList<>();
        if (!Files.exists(path)) {
            return tickets; // Return empty list if no file
        }
        
        List<String> lines = Files.readAllLines(path);
        for (String line : lines) {
            if (line.trim().isEmpty() || line.startsWith("#")) {
                continue;
            }
            Ticket t = fromCsvRow(line);
            if (t != null) {
                tickets.add(t);
            }
        }
        return tickets;
    }

    // --- CSV Helpers ---
    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace(",", "\\,");
    }

    private static String unescape(String s) {
        if (s == null) return "";
        return s.replace("\\,", ",").replace("\\\\", "\\");
    }
}