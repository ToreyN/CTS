import java.util.ArrayList;
import java.util.List;

package CTS.event;

public class Event {
    private int id;
    private String name;
    private String date;        
    private String description;
    private String venue;
    private EventStatus status = EventStatus.DRAFT;

    private List<Seat> seats = new ArrayList<>();

    public Event(int id, String name, String date, String description, String venue) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.description = description;
        this.venue = venue;
    }

    /**
     * Generate a grid of seats like rows A, B, C... with numeric seat numbers.
     */
    public void createDefaultSeating(int numRows, int seatsPerRow, double basePrice) {
        seats.clear();
        for (int r = 0; r < numRows; r++) {
            char rowChar = (char) ('A' + r);
            String row = String.valueOf(rowChar);
            for (int i = 1; i <= seatsPerRow; i++) {
                String seatId = row + i;
                Seat seat = new Seat(seatId, row, i, basePrice);
                seats.add(seat);
            }
        }
    }

    public Seat findSeatById(String seatId) {
        for (Seat s : seats) {
            if (s.getId().equalsIgnoreCase(seatId)) {
                return s;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getVenue() {
        return venue;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", venue='" + venue + '\'' +
                ", status=" + status +
                '}';
    }

}
