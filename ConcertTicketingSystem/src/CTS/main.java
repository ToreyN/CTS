package CTS;

import CTS.user.*;
import CTS.event.*;
import CTS.misc.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * Uses:
 *   - userDatabase  -> users.csv
 *   - Event         -> events.csv
 *   - Artist        -> artists.csv
 *   - LineupEntry   -> lineup.csv
 *   - PaymentTransaction / RefundRequest -> not wired into flows yet, but CSV helpers are ready
 */
public class Main {

    private static final Path EVENTS_FILE   = Paths.get("events.csv");
    private static final Path ARTISTS_FILE  = Paths.get("artists.csv");
    private static final Path LINEUP_FILE   = Paths.get("lineup.csv");
    private static final Path PAYMENTS_FILE = Paths.get("payments.csv");
    private static final Path REFUNDS_FILE  = Paths.get("refunds.csv");

    private final Scanner in = new Scanner(System.in);

    // Engine components / in-memory collections
    private final userDatabase userDb = new userDatabase();
    private final List<Event> events = new ArrayList<>();
    private final List<Artist> artists = new ArrayList<>();
    private final List<LineupEntry> lineupEntries = new ArrayList<>();
    private final List<PaymentTransaction> payments = new ArrayList<>();
    private final List<RefundRequest> refunds = new ArrayList<>();

    private int nextEventId = 1;
    private int nextArtistId = 1;
    private int nextPaymentId = 1;
    private int nextRefundId = 1;

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        loadAll();
        System.out.println("=== Concert Ticketing System (CTS) – CLI Prototype ===");

        boolean done = false;
        while (!done) {
            System.out.println();
            System.out.println("Main Menu");
            System.out.println("1) Register user");
            System.out.println("2) Login");
            System.out.println("3) List all events");
            System.out.println("0) Exit");
            int choice = readInt("Choose option: ");

            switch (choice) {
                case 1 -> registerUser();
                case 2 -> loginFlow();
                case 3 -> listEvents(events);
                case 0 -> done = true;
                default -> System.out.println("Invalid choice.");
            }
        }

        saveAll();
        System.out.println("Goodbye.");
    }

    //  LOAD / SAVE 

    private void loadAll() {
        try {
            events.addAll(Event.loadFromCsv(EVENTS_FILE));
        } catch (IOException e) {
            System.err.println("Warning: could not load events.csv: " + e.getMessage());
        }
        for (Event e : events) {
            nextEventId = Math.max(nextEventId, e.getEventId() + 1);
        }

        try {
            artists.addAll(Artist.loadFromCsv(ARTISTS_FILE));
        } catch (IOException e) {
            System.err.println("Warning: could not load artists.csv: " + e.getMessage());
        }
        for (Artist a : artists) {
            nextArtistId = Math.max(nextArtistId, a.getArtistId() + 1);
        }

        try {
            List<LineupEntry.RawLineupRow> rawRows = LineupEntry.loadRawRows(LINEUP_FILE);
            rebuildLineupFromRaw(rawRows);
        } catch (IOException e) {
            System.err.println("Warning: could not load lineup.csv: " + e.getMessage());
        }

        try {
            List<PaymentTransaction.RawPaymentRow> raw = PaymentTransaction.loadRawRows(PAYMENTS_FILE);
            // For now we just keep them detached from Orders (orderId is present in raw rows)
            for (PaymentTransaction.RawPaymentRow r : raw) {
                payments.add(new PaymentTransaction(
                        r.paymentId, r.gatewayRef, r.type, r.amount, r.timestamp, r.status, null));
                nextPaymentId = Math.max(nextPaymentId, r.paymentId + 1);
            }
        } catch (IOException e) {
            System.err.println("Warning: could not load payments.csv: " + e.getMessage());
        }

        try {
            List<RefundRequest.RawRefundRow> raw = RefundRequest.loadRawRows(REFUNDS_FILE);
            // For now we do not rebuild full Order/Admin links – just create bare RefundRequest objects.
            for (RefundRequest.RawRefundRow r : raw) {
                RefundRequest rr = new RefundRequest(
                        r.refundId, r.reason, r.createdAt, r.status, null);
                rr.setProcessedBy(null);
                rr.setRefundTxn(null);
                refunds.add(rr);
                nextRefundId = Math.max(nextRefundId, r.refundId + 1);
            }
        } catch (IOException e) {
            System.err.println("Warning: could not load refunds.csv: " + e.getMessage());
        }
    }

    private void saveAll() {
        try {
            Event.saveToCsv(EVENTS_FILE, events);
        } catch (IOException e) {
            System.err.println("Error saving events.csv: " + e.getMessage());
        }

        try {
            Artist.saveToCsv(ARTISTS_FILE, artists);
        } catch (IOException e) {
            System.err.println("Error saving artists.csv: " + e.getMessage());
        }

        try {
            LineupEntry.saveToCsv(LINEUP_FILE, lineupEntries);
        } catch (IOException e) {
            System.err.println("Error saving lineup.csv: " + e.getMessage());
        }

        try {
            PaymentTransaction.saveToCsv(PAYMENTS_FILE, payments);
        } catch (IOException e) {
            System.err.println("Error saving payments.csv: " + e.getMessage());
        }

        try {
            RefundRequest.saveToCsv(REFUNDS_FILE, refunds);
        } catch (IOException e) {
            System.err.println("Error saving refunds.csv: " + e.getMessage());
        }

        // Users are saved by userDatabase
        userDb.saveToFile();
    }

    private void rebuildLineupFromRaw(List<LineupEntry.RawLineupRow> rawRows) {
        lineupEntries.clear();
        // Build lookup maps
        Map<Integer, Event> eventById = new HashMap<>();
        for (Event e : events) {
            eventById.put(e.getEventId(), e);
        }
        Map<Integer, Artist> artistById = new HashMap<>();
        for (Artist a : artists) {
            artistById.put(a.getArtistId(), a);
        }

        for (LineupEntry.RawLineupRow row : rawRows) {
            Event event = eventById.get(row.eventId);
            Artist artist = artistById.get(row.artistId);
            if (event == null || artist == null) {
                continue; // skip rows that reference missing data
            }
            LineupEntry entry = new LineupEntry(row.eventId, row.position, row.notes, artist);
            lineupEntries.add(entry);
            event.addLineupEntry(entry);
        }
    }

    //  USER REGISTRATION / LOGIN 

    private void registerUser() {
        System.out.println("--- Register User ---");
        String name = readLine("Name: ");
        String email = readLine("Email: ");
        String password = readLine("Password: ");

        System.out.println("Role:");
        System.out.println("1) ConcertGoer");
        System.out.println("2) VenueAdmin");
        int roleChoice = readInt("Choose role: ");

        String role = (roleChoice == 2) ? "ADMIN" : "USER";

        User user = userDb.createUser(name, email, password, role);
        if (user != null) {
            System.out.println("Registered user with id=" + user.getUserId() +
                    ", role=" + (user instanceof VenueAdmin ? "ADMIN" : "USER"));
        } else {
            System.out.println("Failed to create user (maybe email already in use?).");
        }
    }

    private void loginFlow() {
        System.out.println("--- Login ---");
        String email = readLine("Email: ");
        String password = readLine("Password: ");

        User user = userDb.login(email, password);
        if (user == null) {
            System.out.println("Login failed.");
            return;
        }

        System.out.println("Welcome, " + user.getName() + "!");

        if (user instanceof VenueAdmin admin) {
            adminMenu(admin);
        } else if (user instanceof ConcertGoer goer) {
            goerMenu(goer);
        } else {
            System.out.println("Unknown user type.");
        }
    }

    //  ADMIN MENU 

    private void adminMenu(VenueAdmin admin) {
        boolean done = false;
        while (!done) {
            System.out.println();
            System.out.println("Admin Menu (" + admin.getName() + ")");
            System.out.println("1) List events");
            System.out.println("2) Create new event");
            System.out.println("3) Publish / cancel event");
            System.out.println("4) Manage artists and lineup");
            System.out.println("0) Logout");

            int choice = readInt("Choose option: ");
            switch (choice) {
                case 1 -> listEvents(events);
                case 2 -> createEvent();
                case 3 -> updateEventStatus();
                case 4 -> manageLineup();
                case 0 -> done = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    //  GOER MENU 

    private void goerMenu(ConcertGoer goer) {
        boolean done = false;
        while (!done) {
            System.out.println();
            System.out.println("ConcertGoer Menu (" + goer.getName() + ")");
            System.out.println("1) List published events");
            System.out.println("0) Logout");

            int choice = readInt("Choose option: ");
            switch (choice) {
                case 1 -> {
                    List<Event> published = new ArrayList<>();
                    for (Event e : events) {
                        if (e.getStatus() == EventStatus.PUBLISHED) {
                            published.add(e);
                        }
                    }
                    listEvents(published);
                }
                case 0 -> done = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    //  EVENT / ARTIST / LINEUP HELPERS 

    private void listEvents(List<Event> list) {
        if (list.isEmpty()) {
            System.out.println("No events found.");
            return;
        }
        System.out.println("--- Events ---");
        for (Event e : list) {
            System.out.println("[" + e.getEventId() + "] " + e.getName()
                    + " @ " + e.getVenueName()
                    + " status=" + e.getStatus()
                    + " capacity=" + e.getCapacity());
        }
    }

    private void createEvent() {
        System.out.println("--- Create Event ---");
        String name = readLine("Event name: ");
        String venue = readLine("Venue name: ");
        String desc = readLine("Description: ");
        int capacity = readInt("Capacity: ");

        Date startDate = null;
        String dateStr = readLine("Start date/time (yyyy-MM-dd HH:mm, blank for null): ");
        if (!dateStr.isBlank()) {
            try {
                startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateStr);
            } catch (ParseException e) {
                System.out.println("Could not parse date; startDateTime will be null.");
            }
        }

        Event event = new Event(
                nextEventId++,
                name,
                startDate,
                venue,
                desc,
                capacity,
                EventStatus.DRAFT
        );
        events.add(event);
        System.out.println("Created event with id=" + event.getEventId());
    }

    private void updateEventStatus() {
        int id = readInt("Enter event id: ");
        Event event = findEventById(id);
        if (event == null) {
            System.out.println("Event not found.");
            return;
        }

        System.out.println("Current status: " + event.getStatus());
        System.out.println("1) Publish");
        System.out.println("2) Cancel");
        int choice = readInt("Choose: ");
        if (choice == 1) {
            event.publish();
            System.out.println("Event published.");
        } else if (choice == 2) {
            event.cancel();
            System.out.println("Event canceled.");
        } else {
            System.out.println("No change.");
        }
    }

    private Event findEventById(int id) {
        for (Event e : events) {
            if (e.getEventId() == id) return e;
        }
        return null;
    }

    private void manageLineup() {
        System.out.println("--- Lineup Management ---");
        int eventId = readInt("Event id: ");
        Event event = findEventById(eventId);
        if (event == null) {
            System.out.println("Event not found.");
            return;
        }

        boolean done = false;
        while (!done) {
            System.out.println();
            System.out.println("Lineup for event: " + event.getName());
            for (LineupEntry e : event.getLineup()) {
                System.out.println("  #" + e.getPosition() + " " +
                        (e.getArtist() != null ? e.getArtist().getStageName() : "<no artist>") +
                        " (" + e.getNotes() + ")");
            }
            System.out.println("1) Add artist");
            System.out.println("2) Add lineup entry");
            System.out.println("0) Back");
            int choice = readInt("Choose: ");

            switch (choice) {
                case 1 -> createArtist();
                case 2 -> addLineupEntry(event);
                case 0 -> done = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void createArtist() {
        System.out.println("--- Create Artist ---");
        String name = readLine("Stage name: ");
        String genre = readLine("Genre: ");
        Artist artist = new Artist(nextArtistId++, name, genre);
        artists.add(artist);
        System.out.println("Created artist id=" + artist.getArtistId());
    }

    private void addLineupEntry(Event event) {
        if (artists.isEmpty()) {
            System.out.println("No artists yet. Create an artist first.");
            return;
        }

        System.out.println("Available artists:");
        for (Artist a : artists) {
            System.out.println("[" + a.getArtistId() + "] " + a.getStageName() + " (" + a.getGenre() + ")");
        }
        int artistId = readInt("Artist id: ");
        Artist artist = null;
        for (Artist a : artists) {
            if (a.getArtistId() == artistId) {
                artist = a;
                break;
            }
        }
        if (artist == null) {
            System.out.println("Artist not found.");
            return;
        }

        int position = readInt("Running order position (1 = opening): ");
        String notes = readLine("Notes: ");

        LineupEntry entry = new LineupEntry(event.getEventId(), position, notes, artist);
        lineupEntries.add(entry);
        event.addLineupEntry(entry);
        System.out.println("Lineup entry added.");
    }

    //  INPUT HELPERS 

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = in.nextLine();
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return in.nextLine();
    }
}

