package CTS.gui;

import CTS.user.ConcertGoer;
import CTS.user.User;
import CTS.user.VenueAdmin;

import CTS.booking.Order;
import CTS.event.Event;
import CTS.event.LineupEntry;
import CTS.misc.PaymentTransaction;
import CTS.misc.RefundRequest;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DashboardTabbedGUI extends JFrame {

    private final User currentUser;
    private final JTabbedPane tabs = new JTabbedPane();

    // Shared admin data
    private List<Event> events;
    private List<LineupEntry> lineup;
    private List<Order> orders;
    private List<RefundRequest> refunds;
    private List<PaymentTransaction> payments;

    public DashboardTabbedGUI(User user) {
        this.currentUser = user;

        setTitle("CTS Dashboard — Welcome " + user.getName());
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loadAllData();     // <-- load all CSVs

        buildTabs();
        add(tabs);

        setVisible(true);
    }

    /* ============================================================
       LOAD ALL DATA REQUIRED FOR ADMIN PANELS
       ============================================================ */
    private void loadAllData() {

        // ---------- Load events ----------
        try {
            events = Event.loadFromCsv(Paths.get("events.csv"));
        } catch (Exception e) {
            events = new ArrayList<>();
        }

        // ---------- Load lineup ----------
        try {
            var raw = LineupEntry.loadRawRows(Paths.get("lineup.csv"));
            lineup = new ArrayList<>();

            for (var r : raw) {
                var a = ArtistManagerDialog.findArtistById(r.artistId);
                lineup.add(new LineupEntry(r.eventId, r.position, r.notes, a));
            }

            // Attach lineup entries to their event objects
            for (Event e : events) {
                for (LineupEntry le : lineup) {
                    if (le.getEventId() == e.getEventId()) {
                        e.getLineup().add(le);
                    }
                }
            }

        } catch (Exception e) {
            lineup = new ArrayList<>();
        }

        // ---------- Load orders ----------
        try {
            orders = Order.loadFromCsv(Paths.get("orders.csv"));
        } catch (Exception e) {
            orders = new ArrayList<>();
        }

        // ---------- Load refund requests ----------
        try {
            refunds = RefundRequest.loadAll(Paths.get("refunds.csv"), orders);
        } catch (Exception e) {
            refunds = new ArrayList<>();
        }

        // ---------- Load payments ----------
        try {
            payments = PaymentTransaction.loadAll(Paths.get("payments.csv"), orders);
        } catch (Exception e) {
            payments = new ArrayList<>();
        }
    }


    /* ============================================================
       BUILD TABS
       ============================================================ */
    private void buildTabs() {

        /* ======================= CONCERT GOER ======================= */
        if (currentUser instanceof ConcertGoer goer) {

            tabs.addTab("Browse Events", new EventBrowserPanel(currentUser));
            tabs.addTab("My Bookings", new BookingHistoryPanel(currentUser));
            tabs.addTab("Request Refund", new RefundRequestPanel(currentUser));
            tabs.addTab("Profile / Logout", new LogoutPanel(this));
        }

        /* ========================= ADMIN ============================ */
        else if (currentUser instanceof VenueAdmin admin) {

            tabs.addTab("Manage Events", new EventManagerPanel());

            tabs.addTab("Manage Lineup",
                    new ManageLineupPanel(events, lineup));

            tabs.addTab("Process Refunds",
                    new RefundProcessPanel(admin, orders, refunds, payments));

            tabs.addTab("Profile / Logout", new LogoutPanel(this));
        }
    }
}
