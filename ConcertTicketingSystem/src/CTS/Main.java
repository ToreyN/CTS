package CTS;

import CTS.user.*;
import CTS.event.*;
import CTS.misc.*;
import CTS.booking.*; 
import CTS.enums.PaymentType;
import CTS.enums.EventStatus;
import CTS.enums.PaymentStatus;
import CTS.enums.RefundStatus;
import CTS.user.userDatabase;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors; 

/**
 *
 * Uses:
 * - userDatabase  -> users.csv
 * - Event         -> events.csv
 * - Artist        -> artists.csv
 * - LineupEntry   -> lineup.csv
 * - Order         -> orders.csv
 * - Ticket        -> tickets.csv
 * - PaymentTransaction / RefundRequest
 */
public class Main {

    // --- FILE PATHS ---
    private static final Path EVENTS_FILE   = Paths.get("events.csv");
    private static final Path ARTISTS_FILE  = Paths.get("artists.csv");
    private static final Path LINEUP_FILE   = Paths.get("lineup.csv");
    private static final Path PAYMENTS_FILE = Paths.get("payments.csv");
    private static final Path REFUNDS_FILE  = Paths.get("refunds.csv");
    private static final Path ORDERS_FILE   = Paths.get("orders.csv");  // --- NEW ---
    private static final Path TICKETS_FILE  = Paths.get("tickets.csv"); // --- NEW ---

    private final Scanner in = new Scanner(System.in);

    // --- ENGINE COMPONENTS / COLLECTIONS ---
    private final userDatabase userDb = new userDatabase();
    private final List<Event> events = new ArrayList<>();
    private final List<Artist> artists = new ArrayList<>();
    private final List<LineupEntry> lineupEntries = new ArrayList<>();
    private final List<PaymentTransaction> payments = new ArrayList<>();
    private final List<RefundRequest> refunds = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();     // --- NEW ---
    private final List<Ticket> tickets = new ArrayList<>();   // --- NEW ---

    // --- ID GENERATORS ---
    private int nextEventId = 1;
    private int nextArtistId = 1;
    private int nextPaymentId = 1;
    private int nextRefundId = 1;
    private int nextOrderId = 1;    // --- NEW ---
    private int nextTicketId = 1;   // --- NEW ---

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
                case 3 -> listEvents(events, false); // false = not for booking
                case 0 -> done = true;
                default -> System.out.println("Invalid choice.");
            }
        }

        saveAll();
        System.out.println("Goodbye.");
    }
    
    // =============================================
    //  LOAD / SAVE 
    // =============================================

    private void loadAll() {
        // --- Load Events ---
        try {
            events.addAll(Event.loadFromCsv(EVENTS_FILE));
        } catch (IOException e) {
            System.err.println("Warning: could not load events.csv: " + e.getMessage());
        }
        for (Event e : events) {
            nextEventId = Math.max(nextEventId, e.getEventId() + 1);
        }

        // --- Load Artists ---
        try {
            artists.addAll(Artist.loadFromCsv(ARTISTS_FILE));
        } catch (IOException e) {
            System.err.println("Warning: could not load artists.csv: " + e.getMessage());
        }
        for (Artist a : artists) {
            nextArtistId = Math.max(nextArtistId, a.getArtistId() + 1);
        }

        // --- Load Lineup ---
        try {
            List<LineupEntry.RawLineupRow> rawRows = LineupEntry.loadRawRows(LINEUP_FILE);
            rebuildLineupFromRaw(rawRows);
        } catch (IOException e) {
            System.err.println("Warning: could not load lineup.csv: " + e.getMessage());
        }

        // --- Load Payments ---
        try {
            List<PaymentTransaction.RawPaymentRow> raw = PaymentTransaction.loadRawRows(PAYMENTS_FILE);
            for (PaymentTransaction.RawPaymentRow r : raw) {
                payments.add(new PaymentTransaction(
                        r.paymentId, r.gatewayRef, r.type, r.amount, r.timestamp, r.status, null));
                nextPaymentId = Math.max(nextPaymentId, r.paymentId + 1);
            }
        } catch (IOException e) {
            System.err.println("Warning: could not load payments.csv: " + e.getMessage());
        }

        // --- Load Refunds ---
        try {
            List<RefundRequest.RawRefundRow> raw = RefundRequest.loadRawRows(REFUNDS_FILE);
            for (RefundRequest.RawRefundRow r : raw) {
                RefundRequest rr = new RefundRequest( r.refundId, null, r.createdAt, r.reason, r.status );
                rr.setProcessedBy(null);
                rr.setRefundTxn(null);
                refunds.add(rr);
                nextRefundId = Math.max(nextRefundId, r.refundId + 1);
            }
        } catch (IOException e) {
            System.err.println("Warning: could not load refunds.csv: " + e.getMessage());
        }
        
        // --- LOAD ORDERS & TICKETS ---
        try {
            orders.addAll(Order.loadFromCsv(ORDERS_FILE));
        } catch (IOException e) {
            System.err.println("Warning: could not load orders.csv: " + e.getMessage());
        }
        for (Order o : orders) {
            nextOrderId = Math.max(nextOrderId, o.getOrderId() + 1);
        }
        
        try {
            tickets.addAll(Ticket.loadFromCsv(TICKETS_FILE));
        } catch (IOException e) {
            System.err.println("Warning: could not load tickets.csv: " + e.getMessage());
        }
        for (Ticket t : tickets) {
            nextTicketId = Math.max(nextTicketId, t.getTicketId() + 1);
        }
        
        // --- Re-link objects (Orders, Tickets, etc.) ---
        rebuildOrdersAndTickets();
    }

    private void saveAll() {
        try {
            Event.saveToCsv(EVENTS_FILE, events);
        } catch (IOException e) { System.err.println("Error saving events.csv: " + e.getMessage()); }

        try {
            Artist.saveToCsv(ARTISTS_FILE, artists);
        } catch (IOException e) { System.err.println("Error saving artists.csv: " + e.getMessage()); }

        try {
            LineupEntry.saveToCsv(LINEUP_FILE, lineupEntries);
        } catch (IOException e) { System.err.println("Error saving lineup.csv: " + e.getMessage()); }

        try {
            PaymentTransaction.saveToCsv(PAYMENTS_FILE, payments);
        } catch (IOException e) { System.err.println("Error saving payments.csv: " + e.getMessage()); }

        try {
            RefundRequest.saveToCsv(REFUNDS_FILE, refunds);
        } catch (IOException e) { System.err.println("Error saving refunds.csv: " + e.getMessage()); }

        // --- SAVE ORDERS & TICKETS ---
        try {
            Order.saveToCsv(ORDERS_FILE, orders);
        } catch (IOException e) { System.err.println("Error saving orders.csv: " + e.getMessage()); }
        
        try {
            Ticket.saveToCsv(TICKETS_FILE, tickets);
        } catch (IOException e) { System.err.println("Error saving tickets.csv: " + e.getMessage()); }

        // Users are saved by userDatabase
        userDb.saveToFile();
    }

    private void rebuildLineupFromRaw(List<LineupEntry.RawLineupRow> rawRows) {
        lineupEntries.clear();
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
            if (event == null || artist == null) continue;
            LineupEntry entry = new LineupEntry(row.eventId, row.position, row.notes, artist);
            lineupEntries.add(entry);
            event.addLineupEntry(entry);
        }
    }

    /**
     * Links all loaded tickets to their parent orders after loading from CSV.
     */
    private void rebuildOrdersAndTickets() {
        // Create a lookup map for fast access
        Map<Integer, Order> orderById = new HashMap<>();
        for (Order o : orders) {
            orderById.put(o.getOrderId(), o);
        }
        
        for (Ticket t : tickets) {
            Order parentOrder = orderById.get(t.getOrderId());
            if (parentOrder != null) {
                // Add the ticket to its order's shopping cart
                parentOrder.addTicket(t);
            } else {
                System.err.println("Warning: Ticket " + t.getTicketId() + " references missing order " + t.getOrderId());
            }
        }
    }
    
    // =============================================
    //  USER FLOWS
    // =============================================

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
        User user = userDb.registerUser(name, email, password, role);
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

    // =============================================
    //  ADMIN MENU
    // =============================================

    private void adminMenu(VenueAdmin admin) {
        boolean done = false;
        while (!done) {
            System.out.println();
            System.out.println("Admin Menu (" + admin.getName() + ")");
            System.out.println("1) List events");
            System.out.println("2) Create new event");
            System.out.println("3) Publish / cancel event");
            System.out.println("4) Manage artists and lineup");
            // TODO: Add "Process Refunds" option
            System.out.println("0) Logout");

            int choice = readInt("Choose option: ");
            switch (choice) {
                case 1 -> listEvents(events, false); // false = not for booking
                case 2 -> createEvent();
                case 3 -> updateEventStatus();
                case 4 -> manageLineup();
                case 0 -> done = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // =============================================
    //  GOER MENU
    // =============================================

    private void goerMenu(ConcertGoer goer) {
        boolean done = false;
        while (!done) {
            System.out.println();
            System.out.println("ConcertGoer Menu (" + goer.getName() + ")");
            System.out.println("1) Browse / Book Tickets");
            System.out.println("2) View My Bookings");
            // TODO: Add "Request Refund" option
            System.out.println("0) Logout");

            int choice = readInt("Choose option: ");
            switch (choice) {
                case 1 -> bookTicketFlow(goer);
                case 2 -> viewMyBookings(goer);
                case 0 -> done = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }
    
    // =============================================
    //  BOOKING FLOW
    // =============================================
    
    private void bookTicketFlow(ConcertGoer goer) {
        System.out.println("--- Browse Events ---");
        
        // 1. Filter for PUBLISHED events
        List<Event> published = new ArrayList<>();
        for (Event e : events) {
            if (e.getStatus() == EventStatus.PUBLISHED) {
                published.add(e);
            }
        }
        
        if (published.isEmpty()) {
            System.out.println("Sorry, there are no events to book right now.");
            return;
        }

        // 2. Show events *for booking* (with price and availability)
        listEvents(published, true);
        
        // 3. Get user choice
        int eventId = readInt("Enter Event ID to book (or 0 to cancel): ");
        
        // --- NEW --- Check for cancel option
        if (eventId == 0) {
            System.out.println("Canceling booking.");
            return; // Go back to the goerMenu
        }
        
        Event event = findEventById(eventId);
        if (event == null || event.getStatus() != EventStatus.PUBLISHED) {
            System.out.println("Invalid Event ID.");
            return;
        }

        // 4. Check availability
        if (event.getAvailableSeats() <= 0) {
            System.out.println("Sorry, this event is SOLD OUT.");
            return;
        }
        
        System.out.println("Booking for: " + event.getName());
        System.out.println("Price per ticket: " + event.getBasePrice());
        System.out.println("Tickets available: " + event.getAvailableSeats());
        
        int numTickets = readInt("How many tickets would you like to buy? (0 to cancel) ");
        
        // --- NEW --- Check for cancel option
        if (numTickets <= 0) {
            System.out.println("Booking canceled.");
            return;
        }
        
        if (numTickets > event.getAvailableSeats()) {
            System.out.println("Sorry, only " + event.getAvailableSeats() + " tickets are available.");
            return;
        }

        // 5. Create Order
        Order newOrder = new Order(nextOrderId++, goer.getUserId());
        
        // 6. Create Tickets and add to Order
        for (int i = 0; i < numTickets; i++) {
            // In a real system, we'd find a specific seat. Here, we just fake it.
            String seatLabel = "General Admission, Seat " + (event.getTicketsSold() + 1);
            
            Ticket newTicket = new Ticket(
                nextTicketId++,
                newOrder.getOrderId(),
                event.getEventId(),
                event.getBasePrice(), // Use the event's base price
                seatLabel
            );
            
            event.sellTicket(); // This increments the event's 'ticketsSold' counter
            newOrder.addTicket(newTicket);
            tickets.add(newTicket); // Add to master ticket list
        }
        
        orders.add(newOrder); // Add to master order list
        
        // 7. Success!
        System.out.println("\n--- Booking Confirmed! ---");
        System.out.println("Order ID: " + newOrder.getOrderId());
        System.out.println("Total Cost: " + newOrder.getTotalAmount());
        System.out.println("You booked " + numTickets + " ticket(s) for " + event.getName() + ".");
    }
    
    private void viewMyBookings(ConcertGoer goer) {
        System.out.println("--- My Bookings ---");
        
        // Use Java Streams to filter the master list
        List<Order> myOrders = orders.stream()
            .filter(order -> order.getUserId() == goer.getUserId())
            .collect(Collectors.toList());
            
        if (myOrders.isEmpty()) {
            System.out.println("You have no bookings.");
            return;
        }
        
        for (Order order : myOrders) {
            System.out.println("---------------------------------");
            System.out.println("Order ID: " + order.getOrderId());
            System.out.println("Order Date: " + order.getCreatedAt());
            System.out.println("Total Cost: " + order.getTotalAmount());
            System.out.println("Status: " + order.getStatus());
            System.out.println("Tickets in this order:");
            
            // Check if tickets list is null or empty, which can happen
            if (order.getTickets() == null || order.getTickets().isEmpty()) {
                System.out.println("  (Error: Tickets not loaded correctly)");
            } else {
                for (Ticket ticket : order.getTickets()) {
                    // Find the event for this ticket
                    Event event = findEventById(ticket.getEventId());
                    String eventName = (event != null) ? event.getName() : "Unknown Event";
                    
                    System.out.println("  - Ticket #" + ticket.getTicketId() + 
                                       " for " + eventName + 
                                       " (" + ticket.getSeatLabel() + ")");
                }
            }
        }
        System.out.println("---------------------------------");
    }

    // =============================================
    //  EVENT / ARTIST / LINEUP HELPERS
    // =============================================

    private void listEvents(List<Event> list, boolean forBooking) {
        if (list.isEmpty()) {
            System.out.println("No events found.");
            return;
        }
        System.out.println("--- Events ---");
        for (Event e : list) {
            String details = "[" + e.getEventId() + "] " + e.getName()
                    + " @ " + e.getVenueName();
            
            if (forBooking) {
                // Show booking-related info
                String availability;
                if (e.getAvailableSeats() <= 0) {
                    availability = "SOLD OUT";
                } else {
                    availability = e.getAvailableSeats() + "/" + e.getCapacity() + " available";
                }
                System.out.println(details + " | Price: " + e.getBasePrice() + " | " + availability);
            } else {
                // Show admin-related info
                System.out.println(details + " | status=" + e.getStatus()
                    + " | capacity=" + e.getCapacity());
            }
        }
    }

    private void createEvent() {
        System.out.println("--- Create Event ---");
        String name = readLine("Event name: ");
        String venue = readLine("Venue name: ");
        String desc = readLine("Description: ");
        int capacity = readInt("Capacity: ");
        
        // --- NEW ---
        double price = readDouble("Base ticket price (e.g., 49.99): ");
        Money basePrice = new Money(price, "USD"); // Defaulting to USD

        Date startDate = null;
        String dateStr = readLine("Start date/time (yyyy-MM-dd HH:mm, blank for null): ");
        if (!dateStr.isBlank()) {
            try {
                startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateStr);
            } catch (ParseException e) {
                System.out.println("Could not parse date; startDateTime will be null.");
            }
        }

        // --- CONSTRUCTOR CALL UPDATED ---
        Event event = new Event(
                nextEventId++,
                name,
                startDate,
                venue,
                desc,
                capacity,
                EventStatus.DRAFT,
                basePrice // <-- Pass the new basePrice
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

    // =============================================
    //  INPUT HELPERS
    // =============================================

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
    
    // --- NEW HELPER METHOD ---
    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = in.nextLine();
            try {
                return Double.parseDouble(line.trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number (e.g., 49.99).");
            }
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return in.nextLine();
    }
}