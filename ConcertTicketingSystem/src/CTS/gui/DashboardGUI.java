package CTS.gui;

import CTS.event.Event;
import CTS.user.ConcertGoer;
import CTS.user.User;
import CTS.user.VenueAdmin;

import javax.swing.*;
import java.awt.*;

public class DashboardGUI extends JFrame {

    private final User loggedUser;

    public DashboardGUI(User user) {
        this.loggedUser = user;

        setTitle("CTS Dashboard - Welcome " + user.getName());
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 10, 10));

        JLabel title = new JLabel("Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        panel.add(title);

        /* ---------------- GOER MENU ---------------- */
        if (loggedUser instanceof ConcertGoer goer) {

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
        new BookingHistoryGUI(loggedUser).setVisible(true);
    }

    private void openRefundRequest() {
        new RefundRequestGUI(loggedUser).setVisible(true);
    }

    /* =========================== ADMIN ACTIONS =========================== */

    private void openEventManager() {
        new ManageEventsGUI((VenueAdmin) loggedUser).setVisible(true);
    }

    private void openLineupManager() {
        new ManageLineupGUI((VenueAdmin) loggedUser).setVisible(true);
    }

    private void openRefundProcessor() {
        new RefundProcessorGUI((VenueAdmin) loggedUser).setVisible(true);
    }

    /* =========================== COMMON =========================== */

    private void logout() {
        JOptionPane.showMessageDialog(this, "You have been logged out.");
        dispose();
        new MainMenuGUI(new CTS.user.userDatabase()).setVisible(true);
    }
}
