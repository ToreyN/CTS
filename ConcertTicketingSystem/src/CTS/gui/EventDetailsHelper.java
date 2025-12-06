package CTS.gui;

import CTS.booking.Order;
import CTS.event.Event;

import java.nio.file.Paths;
import java.util.List;

public class EventDetailsHelper {

    public static Event getEvent(Order order) {

        try {
            List<Event> all = Event.loadFromCsv(Paths.get("events.csv"));

            for (Event e : all) {
                if (e.getEventId() == order.getTickets().get(0).getEventId()) {
                    return e;
                }
            }
        } catch (Exception ignored) {}

        return null;
    }
}
