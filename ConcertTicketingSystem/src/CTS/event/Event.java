package CTS.event;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event {
    private int eventId;
    private String name;
    private Date startDateTime;
    private String venueName;
    private String description;
    private int capacity;
    private EventStatus status;

    private List<LineupEntry> lineup = new ArrayList<>();

    public Event(int eventId,
                 String name,
                 Date startDateTime,
                 String venueName,
                 String description,
                 int capacity,
                 EventStatus status) {
        this.eventId = eventId;
        this.name = name;
        this.startDateTime = startDateTime;
        this.venueName = venueName;
        this.description = description;
        this.capacity = capacity;
        this.status = status;
    }

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

    public void addLineupEntry(LineupEntry entry) {
        if (entry != null) {
            lineup.add(entry);
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

    
    public int getAvailableSeats() {
        return capacity;
    }

    // ================= CSV SUPPORT =================
    // CSV format:
    // eventId,name,startDateTimeMillis,venueName,description,capacity,status

    public String toCsvRow() {
        long millis = startDateTime != null ? startDateTime.getTime() : 0L;
        return eventId + "," +
                escape(name) + "," +
                millis + "," +
                escape(venueName) + "," +
                escape(description) + "," +
                capacity + "," +
                status.name();
    }

    public static Event fromCsvRow(String line) {
        String[] parts = line.split(",", 7);
        int id = Integer.parseInt(parts[0]);
        String name = unescape(parts[1]);
        long millis = Long.parseLong(parts[2]);
        String venue = unescape(parts[3]);
        String desc = unescape(parts[4]);
        int capacity = Integer.parseInt(parts[5]);
        EventStatus status = EventStatus.valueOf(parts[6]);
        Date date = millis == 0L ? null : new Date(millis);
        return new Event(id, name, date, venue, desc, capacity, status);
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
            result.add(fromCsvRow(line));
        }
        return result;
    }

    public static void saveToCsv(Path path, List<Event> events) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# eventId,name,startDateTimeMillis,venueName,description,capacity,status");
        for (Event e : events) {
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
        return "Event{" +
                "eventId=" + eventId +
                ", name='" + name + '\'' +
                ", startDateTime=" + startDateTime +
                ", venueName='" + venueName + '\'' +
                ", capacity=" + capacity +
                ", status=" + status +
                '}';
    }

    /**
     * Helper to sort lineup in-place by position (1, 2, 3, ...).
     */
    public void sortLineupByPosition() {
        lineup.sort(Comparator.comparingInt(LineupEntry::getPosition));
    }

}
