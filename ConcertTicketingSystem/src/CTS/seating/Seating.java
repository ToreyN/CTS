package CTS.seating;

import java.util.ArrayList;
import java.util.List;
import CTS.enums.SeatStatus;   

public class Seating {

    private final int eventId;
    private final List<Seat> seats;

    public Seating(int eventId, List<Seat> seats) {
        this.eventId = eventId;
        this.seats = seats;
    }

    public int getEventId() {
        return eventId;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public Seat getSeatByNumber(int num) {
        if (num <= 0 || num > seats.size()) return null;
        return seats.get(num - 1);
    }

    public List<Seat> getHeldSeats() {
        List<Seat> held = new ArrayList<>();
        for (Seat s : seats) {
            if (s.getStatus() == SeatStatus.HELD) {
                held.add(s);
            }
        }
        return held;
    }
}
