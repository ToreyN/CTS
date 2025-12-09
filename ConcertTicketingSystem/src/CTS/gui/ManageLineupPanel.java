package CTS.gui;

import CTS.event.Event;
import CTS.event.LineupEntry;
import CTS.event.Artist;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ManageLineupPanel extends JPanel {

    private final List<Event> events;
    private final List<LineupEntry> lineupEntries;

    public ManageLineupPanel(List<Event> events, List<LineupEntry> lineupEntries) {
        this.events = events;
        this.lineupEntries = lineupEntries;

        // Link lineup entries into event objects 
        rebuildEventLineups();

        buildUI();
    }

    /** Attach lineup entries to their correct events */
    private void rebuildEventLineups() {
        // Clear existing lineups
        for (Event e : events) {
            e.getLineup().clear();
        }

        // Reassign lineup entries
        for (LineupEntry le : lineupEntries) {
            for (Event e : events) {
                if (e.getEventId() == le.getEventId()) {
                    e.addLineupEntry(le);
                }
            }
        }
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        for (Event e : events) {
            list.add(buildEventCard(e));
        }

        add(new JScrollPane(list), BorderLayout.CENTER);
    }

    private JPanel buildEventCard(Event event) {
        JPanel card = new JPanel(new GridLayout(0, 1));
        card.setBorder(BorderFactory.createTitledBorder(
                event.getName() + " (ID " + event.getEventId() + ")"));

        card.add(new JLabel("Venue: " + event.getVenueName()));
        card.add(new JLabel("Lineup entries: " + event.getLineup().size()));

        JButton open = new JButton("Manage Lineup");
        open.addActionListener(ev -> openLineupEditor(event));

        card.add(open);
        return card;
    }

    private void openLineupEditor(Event event) {
        new LineupEditorPanel(
                SwingUtilities.getWindowAncestor(this),
                event,
                events,
                lineupEntries
        );
    }
}
