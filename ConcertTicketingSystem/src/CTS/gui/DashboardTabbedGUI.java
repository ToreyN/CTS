package CTS.gui;

import CTS.user.ConcertGoer;
import CTS.user.User;
import CTS.user.VenueAdmin;

import javax.swing.*;
import java.awt.*;

public class DashboardTabbedGUI extends JFrame {

    private final User currentUser;
    private final JTabbedPane tabs = new JTabbedPane();

    public DashboardTabbedGUI(User user) {
        this.currentUser = user;

        setTitle("CTS Dashboard — Welcome " + user.getName());
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        buildTabs();
        add(tabs);

        setVisible(true);
    }

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
            tabs.addTab("Manage Lineup", new ManageLineupPanel());
            tabs.addTab("Refund Processing", new RefundProcessPanel(admin));
            tabs.addTab("Profile / Logout", new LogoutPanel(this));
            tabs.addTab("Process Refunds", new RefundProcessPanel(currentUser));
            

        }
    }
}
