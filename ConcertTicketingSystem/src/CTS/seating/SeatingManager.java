package CTS.seating;

import CTS.event.Event;
import CTS.enums.SeatStatus;
import CTS.misc.Money;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SeatingManager {

    private static final String PREFIX = "seats_event_";
    private static final String EXT = ".csv";

    /**
     * Loads seating for an event.
     * If no file exists, generates a new layout using SeatFactory.
     */
    public static Seating loadOrCreate(Event event) {
        int eventId = event.getEventId();
        File file = new File(PREFIX + eventId + EXT);

        if (!file.exists()) {
            // Create fresh seating
            return new Seating(eventId, SeatFactory.generate(event));
        }

        return load(event);
    }

    /**
     * Loads seats from CSV for this event.
     */
    private static Seating load(Event event) {
        int eventId = event.getEventId();
        int capacity = event.getCapacity();
        List<Seat> seats = new ArrayList<>();

        File file = new File(PREFIX + eventId + EXT);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty() || line.startsWith("#"))
                    continue;

                String[] p = line.split(",", -1);

                int seatId     = Integer.parseInt(p[0]);
                String rowLabel = p[1];
                int seatNumber = Integer.parseInt(p[2]);
                int sectionId = Integer.parseInt(p[3]);
                SeatStatus status = SeatStatus.valueOf(p[4]);
                Money price = Money.fromInlineString(p[5]);

                Seat s = new Seat(
                        eventId,
                        seatId,
                        sectionId,
                        rowLabel,
                        seatNumber,
                        price
                );

                // Apply state
                switch (status) {
                    case AVAILABLE -> s.markAvailable();
                    case HELD -> s.markHeld();
                    case SOLD -> { s.markHeld(); s.markSold(); }
                    case ADMIN_HELD -> s.markAdminHeld();
                }

                seats.add(s);
            }

        } catch (Exception e) {
            System.err.println("Error loading seat CSV — regenerating seating: " + e.getMessage());
            return new Seating(eventId, SeatFactory.generate(event));
        }

        // Ensure we have full capacity
        while (seats.size() < capacity) {
            // Fill missing seats
            seats.addAll(SeatFactory.generate(event).subList(seats.size(), capacity));
        }

        return new Seating(eventId, seats);
    }

    /**
     * Saves seating to CSV.
     */
    public static void save(Seating seating) {
        File file = new File(PREFIX + seating.getEventId() + EXT);

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {

            pw.println("# seatId,rowLabel,seatNumber,sectionId,status,priceInline");

            for (Seat s : seating.getSeats()) {
                pw.println(
                        s.getSeatId() + "," +
                        s.getRowLabel() + "," +
                        s.getSeatNumber() + "," +
                        s.getSectionId() + "," +
                        s.getStatus().name() + "," +
                        s.getCurrentPrice().toInlineString()
                );
            }

        } catch (IOException ignored) { }

    } 

} 
