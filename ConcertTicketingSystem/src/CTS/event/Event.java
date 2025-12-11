package CTS.event;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import CTS.enums.EventStatus;
import java.util.stream.Collectors;
import CTS.misc.Money; 

public class Event {
    private int eventId;
    private String name;
    private Date startDateTime;
    private String venueName;
    private String description;
    private int capacity;
    private EventStatus status;

    // --- NEW FIELDS ---
    private Money basePrice;   
    private int ticketsSold; 

    private List<LineupEntry> lineup = new ArrayList<>();

    // --- STATIC ID MANAGEMENT (CRITICAL FIX) ---
    private static int NEXT_ID = 1;

    public static int nextId() {
        return NEXT_ID++;
    }

    /**
     * Resets the NEXT_ID counter based on the highest ID loaded from disk.
     */
    public static void initializeID(List<Event> loadedEvents) {
        int maxId = 0;
        for (Event e : loadedEvents) {
            maxId = Math.max(maxId, e.getEventId());
        }
        NEXT_ID = maxId + 1;
    }
    // ---------------------------------------------


    /**
     * Constructor updated to include basePrice
     */
    public Event(int eventId,
                 String name,
                 Date startDateTime,
                 String venueName,
                 String description,
                 int capacity,
                 EventStatus status,
                 Money basePrice) { 
        this.eventId = eventId;
        this.name = name;
        this.startDateTime = startDateTime;
        this.venueName = venueName;
        this.description = description;
        this.capacity = capacity;
        this.status = status;
        this.basePrice = basePrice; 
        this.ticketsSold = 0;     
    }

    // --- Getters  ---
    public int getEventId() {
        return eventId;
    }
    
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public String getVenueName() {
        return venueName;
    }

    public String getDescription() {
        return description;
    }

    public int getCapacity() {
        return capacity;
    }

    public EventStatus getStatus() {
        return status;
    }

    public List<LineupEntry> getLineup() {
        return lineup;
    }
    
   
    
    public static List<Event> getAllPublishedEvents() {
        List<Event> list = new ArrayList<>();
        try {
            // Load all events from the default CSV file
            Path path = Paths.get("events.csv");
            List<Event> all = loadFromCsv(path);

            for (Event e : all) {
                if (e.getStatus() == EventStatus.PUBLISHED) {
                    list.add(e);
                }
            }

        } catch (Exception ex) {
            System.err.println("Error loading events for GUI: " + ex.getMessage());
        }

        return list;
    }


    public Money getBasePrice() {
        return basePrice;
    }


    public int getTicketsSold() {
        return ticketsSold;
    }
    

    private void setTicketsSold(int count) {
        this.ticketsSold = count;
    }


    public int getAvailableSeats() {
        return capacity - ticketsSold;
    }


    public boolean sellTicket() {
        if (ticketsSold < capacity) {
            ticketsSold++;
            return true;
        }
        return false; // Sold out!
    }

    // --- Other Methods  ---
    
    public void addLineupEntry(LineupEntry entry) {
        if (entry != null) {
            lineup.add(entry);
        }
    }
    
    public void setName(String name) { this.name = name; }
    public void setVenue(String venue) { this.venueName = venue; }
    public void setPrice(Money m) { this.basePrice = m; }
    public void setDate(Date d) { this.startDateTime = d; }


    public void unSellTicket() {
        if (ticketsSold > 0) {
            ticketsSold--;
        }
    }
    
    public void publish() {
        if (status == EventStatus.DRAFT) {
            status = EventStatus.PUBLISHED;
        }
    }

    public void cancel() {
        status = EventStatus.CANCELED;
    }

    public void updateDescription(String desc) {
        this.description = desc;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    // ================= CSV SUPPORT =================

    public String toCsvRow() {
        long millis = startDateTime != null ? startDateTime.getTime() : 0L;

        String priceString = (basePrice != null) ? basePrice.toInlineString() : "0.0:USD";
        
        return eventId + "," +
                escape(name) + "," +
                millis + "," +
                escape(venueName) + "," +
                escape(description) + "," +
                capacity + "," +
                status.name() + "," +
                priceString + "," +  
                ticketsSold;                
    }


    public static Event fromCsvRow(String line) {
        try {
        	String[] parts = line.split("(?<!\\\\),", -1);
        	if (parts.length < 9) {
        	    System.err.println("Skipping malformed event line: Not enough parts (" + parts.length + ")");
        	    return null;
        	}
            int id = Integer.parseInt(parts[0]);
            String name = unescape(parts[1]);
            long millis = Long.parseLong(parts[2]);
            String venue = unescape(parts[3]);
            String desc = unescape(parts[4]);
            int capacity = Integer.parseInt(parts[5]);
            EventStatus status = EventStatus.valueOf(parts[6]);
            Date date = millis == 0L ? null : new Date(millis);
            
            // --- LOAD PRICE AND SOLD COUNT ---
            Money price = Money.fromInlineString(unescape(parts[7]));
            int ticketsSold = Integer.parseInt(parts[8]);

            // Create the event
            Event event = new Event(id, name, date, venue, desc, capacity, status, price);
            event.setTicketsSold(ticketsSold); // Set the loaded sold count
            return event;
            
        } catch (Exception e) {
            System.err.println("Skipping malformed event line: " + line);
            return null;
        }
    }

    public static List<Event> loadFromCsv(Path path) throws IOException {
        List<Event> result = new ArrayList<>();
        if (!Files.exists(path)) {
            return result;
        }
        for (String line : Files.readAllLines(path)) {
            if (line.trim().isEmpty() || line.startsWith("#")) {
                continue;
            }
            Event e = fromCsvRow(line); 
            if (e != null) {            
                result.add(e);
            }
        }
        return result;
    }

    public static void saveToCsv(Path path, List<Event> events) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# eventId,name,startDateTimeMillis,venueName,description,capacity,status,priceInline,ticketsSold");
        for (Event e : events) {
            lines.add(e.toCsvRow());
        }
        Files.write(path, lines);
    }

    // --- CSV Helpers ---
    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace(",", "\\,");
    }

    private static String unescape(String s) {
        return s.replace("\\,", ",").replace("\\\\", "\\");
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", name='" + name + '\'' +
                ", startDateTime=" + startDateTime +
                ", venueName='" + venueName + '\'' +
                ", capacity=" + capacity +
                ", status=" + status +
                '}';
    }

    public void sortLineupByPosition() {
        lineup.sort(Comparator.comparingInt(LineupEntry::getPosition));
    }
}