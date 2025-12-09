package CTS.gui;

import CTS.event.Event;
import CTS.event.LineupEntry;
import CTS.seating.Seating;
import CTS.seating.SeatingManager;
import CTS.user.User;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class EventDetailGUI extends JFrame {

    private final User currentUser;
    private final Event event;

    public EventDetailGUI(User user, Event event) {
        this.currentUser = user;
        this.event = event;

        setTitle("Event Details - " + event.getName());
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        buildUI();
    }

    private void buildUI() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy hh:mm a");

        panel.add(makeLabel("Event: " + event.getName()));
        panel.add(makeLabel("Venue: " + event.getVenueName()));
        panel.add(makeLabel("Date: " + (event.getStartDateTime() != null
                ? df.format(event.getStartDateTime())
                : "TBA")));

        panel.add(makeLabel("Description:"));
        panel.add(wrapText(event.getDescription()));

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(makeLabel("Ticket Price: " + event.getBasePrice()));
        panel.add(makeLabel("Available Seats: " + event.getAvailableSeats()));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Lineup
        panel.add(makeLabel("Lineup:"));
        if (event.getLineup().isEmpty()) {
            panel.add(makeLabel("No lineup assigned yet."));
        } else {
            for (LineupEntry entry : event.getLineup()) {
                panel.add(makeLabel(
                    " #" + entry.getPosition() + "  " +
                    entry.getArtist().getStageName() +
                    "  (" + entry.getNotes() + ")"
                ));
            }
        }

        JButton seatBtn = new JButton("Select Seats");
        seatBtn.addActionListener(e -> openSeating());

        JPanel bottom = new JPanel();
        bottom.add(seatBtn);

        add(new JScrollPane(panel), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    /* ---------- Helpers ---------- */

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextArea wrapText(String text) {
        JTextArea area = new JTextArea(text);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setBackground(null);
        area.setAlignmentX(Component.LEFT_ALIGNMENT);
        return area;
    }

    private void openSeating() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Please login before booking.");
            return;
        }

        Seating seating = SeatingManager.loadOrCreate(event);

        GUIApp app = new GUIApp(currentUser);

        SeatingScreen screen = new SeatingScreen(app, event, seating);

        JFrame frame = new JFrame("Select Seats - " + event.getName());
        frame.setContentPane(screen);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
