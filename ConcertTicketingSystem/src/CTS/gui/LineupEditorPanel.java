package CTS.gui;

import CTS.event.Event;
import CTS.event.LineupEntry;
import CTS.event.Artist; // ADDED: Import Artist for the buildEntryCard robustness

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer; // ADDED: Import Consumer for the callback fix

public class LineupEditorPanel extends JDialog {

    private final Event event;
    private final List<Event> events;
    private final List<LineupEntry> lineupEntries;

    public LineupEditorPanel(Window parent,
                             Event event,
                             List<Event> events,
                             List<LineupEntry> lineupEntries) {

        super(parent, "Edit Lineup – " + event.getName(), ModalityType.APPLICATION_MODAL);

        this.event = event;
        this.events = events;
        this.lineupEntries = lineupEntries;

        setSize(500, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        rebuildUI();
        setVisible(true);
    }

    private void rebuildUI() {
        getContentPane().removeAll();

        event.sortLineupByPosition();

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        for (LineupEntry entry : event.getLineup()) {
            list.add(buildEntryCard(entry));
            list.add(Box.createVerticalStrut(10));
        }

        add(new JScrollPane(list), BorderLayout.CENTER);

        JButton addBtn = new JButton("Add Lineup Entry");
        // CRITICAL FIX START: Change from this::saveAndRefresh (Runnable)
        // to a lambda that handles the new LineupEntry object (Consumer)
        addBtn.addActionListener(ev ->
            new AddLineupEntryDialog(this, event, (newEntry) -> { 
                if (newEntry != null) {
                    // 1. Add the new entry to the global list managed by this panel
                    this.lineupEntries.add(newEntry); 
                    // 2. Call the save/refresh method
                    this.saveAndRefresh(); 
                }
            })
        );
        // CRITICAL FIX END

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(addBtn);
        add(bottom, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private JPanel buildEntryCard(LineupEntry entry) {
        JPanel card = new JPanel(new GridLayout(0,1));
        card.setBorder(BorderFactory.createTitledBorder("Position " + entry.getPosition()));

        // CRITICAL: Add null check for robustness
        Artist artist = entry.getArtist();
        String artistName = (artist != null) ? artist.getStageName() : "Unknown Artist / Missing Data";
        String artistGenre = (artist != null) ? artist.getGenre() : "N/A";
        
        card.add(new JLabel("Artist: " + artistName));
        card.add(new JLabel("Genre: " + artistGenre));
        // OLD: card.add(new JLabel("Artist: " + entry.getArtist().getStageName()));
        // OLD: card.add(new JLabel("Genre: " + entry.getArtist().getGenre()));
        
        card.add(new JLabel("Notes: " + entry.getNotes()));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton up = new JButton("↑");
        JButton down = new JButton("↓");
        JButton edit = new JButton("Edit Notes");
        JButton remove = new JButton("Remove");

        up.addActionListener(e -> move(entry, -1));
        down.addActionListener(e -> move(entry, +1));
        edit.addActionListener(e -> editNotes(entry));
        remove.addActionListener(e -> removeEntry(entry));

        actions.add(up);
        actions.add(down);
        actions.add(edit);
        actions.add(remove);

        card.add(actions);

        return card;
    }

    private void move(LineupEntry entry, int delta) {
        entry.reorder(entry.getPosition() + delta);
        saveAndRefresh();
    }

    private void editNotes(LineupEntry entry) {
        String newNotes = JOptionPane.showInputDialog("Edit Notes:", entry.getNotes());
        if (newNotes != null) {
            entry.setNotes(newNotes);
            saveAndRefresh();
        }
    }

    private void removeEntry(LineupEntry entry) {
        // Remove from the Event's local lineup
        event.getLineup().remove(entry);
        // Remove from the global lineup list managed by the panel
        lineupEntries.remove(entry); 
        saveAndRefresh();
    }

    private void saveAndRefresh() {
        try {
            LineupEntry.saveToCsv(Paths.get("lineup.csv"), lineupEntries);
            Event.saveToCsv(Paths.get("events.csv"), events);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Save failed: " + e.getMessage());
        }

        rebuildUI();
    }
}