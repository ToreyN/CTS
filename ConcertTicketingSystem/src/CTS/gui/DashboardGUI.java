package CTS.gui;

import CTS.booking.Order;
import CTS.event.Event;
import CTS.misc.RefundRequest;
import CTS.misc.PaymentTransaction;
import CTS.user.ConcertGoer;
import CTS.user.User;
import CTS.user.VenueAdmin;
import CTS.event.LineupEntry;
import CTS.event.Artist;
import CTS.gui.ArtistManagerDialog;


import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DashboardGUI extends JFrame {

    private final User loggedUser;
    
 // Admin data
    private List<Event> events;
    private List<LineupEntry> lineup;


    // Local storage
    private List<Order> orders;
    private List<RefundRequest> refunds;
    private List<PaymentTransaction> payments;

    public DashboardGUI(User user) {
        this.loggedUser = user;

        // --------------------------
        // LOAD DATA FOR ADMIN TOOLS
        // --------------------------
        loadOrders();
        loadRefunds();
        loadPayments();
        loadEvents();
        loadLineup();

        setTitle("CTS Dashboard - Welcome " + user.getName());
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
    }

    /* ==========================================
       DATA LOADING
       ========================================== */

    private void loadOrders() {
        try {
            orders = Order.loadFromCsv(Paths.get("orders.csv"));
        } catch (Exception e) {
            orders = new ArrayList<>();
        }
    }
    
    private void loadEvents() {
        try {
            events = Event.loadFromCsv(Paths.get("events.csv"));
        } catch (Exception e) {
            events = new ArrayList<>();
        }
    }


    private void loadRefunds() {
        try {
            var raw = RefundRequest.loadRawRows(Paths.get("refunds.csv"));
            refunds = new ArrayList<>();

            for (var r : raw) {
                Order o = Order.findById(r.orderId, orders);
                refunds.add(new RefundRequest(
                        r.refundId,
                        o,
                        r.createdAt,
                        r.reason,
                        r.status
                ));
            }
        } catch (Exception e) {
            refunds = new ArrayList<>();
        }
    }

    private void loadPayments() {
        try {
        	payments = PaymentTransaction.loadAll(
        	        Paths.get("payments.csv"),
        	        orders   // required to link each payment to its order
        	);

        } catch (Exception e) {
            payments = new ArrayList<>();
        }
    }

    /* ==========================================
       UI BUILDING
       ========================================== */

    private void initUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 10, 10));

        JLabel title = new JLabel("Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        panel.add(title);

        /* ---------------- GOER MENU ---------------- */
        if (loggedUser instanceof ConcertGoer) {

            JButton browseEvents = new JButton("Browse & Book Events");
            JButton viewBookings = new JButton("View My Bookings");
            JButton requestRefund = new JButton("Request Refund");
            JButton logout = new JButton("Logout");

            browseEvents.addActionListener(e -> openBrowseEvents());
            viewBookings.addActionListener(e -> openMyBookings());
            requestRefund.addActionListener(e -> openRefundRequest());
            logout.addActionListener(e -> logout());

            panel.add(browseEvents);
            panel.add(viewBookings);
            panel.add(requestRefund);
            panel.add(logout);
        }

        /* ---------------- ADMIN MENU ---------------- */
        else if (loggedUser instanceof VenueAdmin admin) {

            JButton manageEvents = new JButton("Manage Events");
            JButton manageLineup = new JButton("Manage Lineup");
            JButton processRefunds = new JButton("Process Refund Requests");
            JButton logout = new JButton("Logout");

            manageEvents.addActionListener(e -> openEventManager());
            manageLineup.addActionListener(e -> openLineupManager());
            processRefunds.addActionListener(e -> openRefundProcessor());
            logout.addActionListener(e -> logout());

            panel.add(manageEvents);
            panel.add(manageLineup);
            panel.add(processRefunds);
            panel.add(logout);
        }

        add(panel);
    }

    /* =========================== GOER ACTIONS =========================== */

    private void openBrowseEvents() {
        new EventBrowserGUI(loggedUser).setVisible(true);
    }

    private void openMyBookings() {
        new BookingHistoryPanel(loggedUser).setVisible(true);
    }

    private void openRefundRequest() {
        new RefundRequestPanel(loggedUser).setVisible(true);
    }

    /* =========================== ADMIN ACTIONS =========================== */

    private void openEventManager() {
        JFrame f = new JFrame("Manage Events");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setSize(650, 550);
        f.add(new EventManagerPanel());
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private void openLineupManager() {
        JFrame f = new JFrame("Manage Lineup");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setSize(650, 550);
        f.add(new ManageLineupPanel(events, lineup));
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    
    private void loadLineup() {
        try {
            var raw = LineupEntry.loadRawRows(Paths.get("lineup.csv"));
            lineup = new ArrayList<>();

            for (var r : raw) {
                Artist a = ArtistManagerDialog.findArtistById(r.artistId);
                lineup.add(new LineupEntry(r.eventId, r.position, r.notes, a));
            }
        } catch (Exception e) {
            lineup = new ArrayList<>();
        }
    }



    private void openRefundProcessor() {

        JFrame f = new JFrame("Process Refund Requests");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setSize(650, 550);

        RefundProcessPanel panel = new RefundProcessPanel(
                (VenueAdmin) loggedUser,
                orders,
                refunds,
                payments
        );

        f.add(panel);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    /* =========================== COMMON =========================== */

    private void logout() {
        JOptionPane.showMessageDialog(this, "You have been logged out.");
        dispose();
        new MainMenuGUI(new CTS.user.userDatabase()).setVisible(true);
    }
}

