package CTS.gui;

import CTS.event.Event;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;
import java.util.List;

/**
 * Admin panel for selecting an event and editing its lineup.
 */
public class ManageLineupPanel extends JPanel {

    public ManageLineupPanel() {
        setLayout(new BorderLayout());

        List<Event> events;
        try {
            events = Event.loadFromCsv(Paths.get("events.csv"));
        } catch (Exception e) {
            events = List.of();
        }

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        for (Event e : events) {
            list.add(buildEventCard(e));
        }

        add(new JScrollPane(list), BorderLayout.CENTER);
    }

    private JPanel buildEventCard(Event event) {
        JPanel card = new JPanel(new GridLayout(0,1));
        card.setBorder(BorderFactory.createTitledBorder(event.getName() + " (ID " + event.getEventId() + ")"));

        card.add(new JLabel("Venue: " + event.getVenueName()));
        card.add(new JLabel("Lineup entries: " + event.getLineup().size()));

        JButton open = new JButton("Manage Lineup");
        open.addActionListener(ev -> openLineupEditor(event));

        card.add(open);
        return card;
    }

    private void openLineupEditor(Event e) {
        new LineupEditorPanel(
                SwingUtilities.getWindowAncestor(this),
                e
        );
    }
}
