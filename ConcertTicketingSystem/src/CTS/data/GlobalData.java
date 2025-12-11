package CTS.data;

import CTS.event.Event;
import CTS.booking.Order;
import CTS.booking.Ticket;
import CTS.misc.PaymentTransaction;
import CTS.misc.RefundRequest;
import CTS.event.Artist;
import CTS.event.LineupEntry;
import java.util.List;
import java.util.ArrayList;

public class GlobalData {
    
    // Static lists initialized safely
    private static List<Event> masterEvents = new ArrayList<>();
    private static List<Order> masterOrders = new ArrayList<>();
    private static List<Ticket> masterTickets = new ArrayList<>(); 
    private static List<PaymentTransaction> masterPayments = new ArrayList<>();
    private static List<RefundRequest> masterRefunds = new ArrayList<>();
    private static List<Artist> masterArtists = new ArrayList<>();
    private static List<LineupEntry> masterLineupEntries = new ArrayList<>();


    // --- SETTERS (Used by Main.main to initialize data) ---
    public static void setAllEvents(List<Event> events) {
        masterEvents = events;
    }

    public static void setAllOrders(List<Order> orders) {
        masterOrders = orders;
    }

    public static void setAllTickets(List<Ticket> tickets) {
        masterTickets = tickets;
    }

    public static void setAllPayments(List<PaymentTransaction> payments) {
        masterPayments = payments;
    }

    public static void setAllRefunds(List<RefundRequest> refunds) {
        masterRefunds = refunds;
    }

    public static void setAllArtists(List<Artist> artists) {
        masterArtists = artists;
    }

    public static void setAllLineupEntries(List<LineupEntry> lineupEntries) {
        masterLineupEntries = lineupEntries;
    }


    // --- ACCESSOR METHODS (GETTERS - Used by GUI Helpers) ---
    public static List<Event> getAllEvents() {
        return masterEvents;
    }
    
    public static List<Order> getAllOrders() {
        return masterOrders;
    }

    public static List<Ticket> getAllTickets() {
        return masterTickets;
    }
    
    public static List<PaymentTransaction> getAllPayments() {
        return masterPayments;
    }

    public static List<RefundRequest> getAllRefunds() {
        return masterRefunds;
    }
    
    public static List<Artist> getAllArtists() {
        return masterArtists;
    }
    
    public static List<LineupEntry> getAllLineupEntries() {
        return masterLineupEntries;
    }
}