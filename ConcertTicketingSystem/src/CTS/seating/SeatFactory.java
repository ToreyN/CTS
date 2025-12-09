package CTS.seating;

import CTS.misc.Money;
import CTS.event.Event;
import java.util.ArrayList;
import java.util.List;

public class SeatFactory {

    /**
     * Generates a simple flat seating layout:
     * - All seats in section 1
     * - Row labels: A, B, C... (increments every 10 seats)
     * - seatNumber = 1..capacity
     * - seatId = seatNumber
     * - price = event.getBasePrice()
     */
    public static List<Seat> generate(Event event) {

        int capacity = event.getCapacity();
        Money price = event.getBasePrice();

        List<Seat> seats = new ArrayList<>(capacity);

        int seatsPerRow = 10;   // Matches the current GUI grid columns
        int sectionId = 1;

        for (int i = 0; i < capacity; i++) {

            int seatNumber = i + 1;
            int seatId = seatNumber;

            int rowIndex = i / seatsPerRow;  // 0=A, 1=B, etc.
            String rowLabel = getRowLabel(rowIndex);

            Seat seat = new Seat(
                    event.getEventId(),  // eventId
                    seatId,              // seatId
                    sectionId,           // sectionId
                    rowLabel,            // rowLabel (A, B, C…)
                    seatNumber,          // seatNumber (1..capacity)
                    new Money(price.getAmount(), price.getCurrency()) // copy price
            );

            seats.add(seat);
        }

        return seats;
    }

    /** Converts 0 -> A, 1 -> B, ... 25 -> Z, 26 -> AA, 27 -> AB, etc. */
    private static String getRowLabel(int index) {
        StringBuilder sb = new StringBuilder();
        while (index >= 0) {
            sb.insert(0, (char) ('A' + index % 26));
            index = (index / 26) - 1;
        }
        return sb.toString();
    }
}
