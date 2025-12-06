package CTS.event;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import CTS.enums.EventStatus;

public class EventDatabase {

    private static final Path EVENTS_FILE = Paths.get("events.csv");

    private static List<Event> events = new ArrayList<>();

    // Load events when class loads
    static {
        try {
            events = Event.loadFromCsv(EVENTS_FILE);
        } catch (IOException e) {
            System.err.println("Could not load events.csv: " + e.getMessage());
            events = new ArrayList<>();
        }
    }

    /** Returns ALL events */
    public static List<Event> getAllEvents() {
        return events;
    }

    /** Returns ONLY published events for GUIApp */
    public static List<Event> getAllPublishedEvents() {
        List<Event> published = new ArrayList<>();
        for (Event e : events) {
            if (e.getStatus() == EventStatus.PUBLISHED) {
                published.add(e);
            }
        }
        return published;
    }

}
