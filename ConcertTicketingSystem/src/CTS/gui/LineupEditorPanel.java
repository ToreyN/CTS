package CTS.gui;

import CTS.event.Event;
import CTS.event.LineupEntry;
import CTS.event.Artist;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;
import java.util.List;

public class LineupEditorPanel extends JDialog {

    private final Event event;

    public LineupEditorPanel(Window parent, Event event) {
        super(parent, "Lineup Editor – " + event.getName(), ModalityType.APPLICATION_MODAL);
        this.event = event;

        setSize(500, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        rebuildUI();

        setVisible(true);
    }

    private void rebuildUI() {
        getContentPane().removeAll();

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        event.sortLineupByPosition();

        for (LineupEntry entry : event.getLineup()) {
            list.add(buildEntryCard(entry));
        }

        add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addArtist = new JButton("Add Artist");
        addArtist.addActionListener(ev -> new ArtistManagerDialog(this));

        JButton addEntry = new JButton("Add Lineup Entry");
        addEntry.addActionListener(ev -> new AddLineupEntryDialog(this, event, this::saveAndRefresh));

        bottom.add(addArtist);
        bottom.add(addEntry);

        add(bottom, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private JPanel buildEntryCard(LineupEntry entry) {
        JPanel card = new JPanel(new GridLayout(0,1));
        card.setBorder(BorderFactory.createTitledBorder("#" + entry.getPosition()));

        card.add(new JLabel("Artist: " + entry.getArtist().getStageName()));
        card.add(new JLabel("Genre: " + entry.getArtist().getGenre()));
        card.add(new JLabel("Notes: " + entry.getNotes()));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton up = new JButton("↑");
        JButton down = new JButton("↓");
        JButton edit = new JButton("Edit Notes");
        JButton remove = new JButton("Remove");

        up.addActionListener(ev -> moveEntry(entry, -1));
        down.addActionListener(ev -> moveEntry(entry, +1));

        edit.addActionListener(ev -> editNotes(entry));
        remove.addActionListener(ev -> removeEntry(entry));

        actions.add(up);
        actions.add(down);
        actions.add(edit);
        actions.add(remove);

        card.add(actions);

        return card;
    }

    private void moveEntry(LineupEntry entry, int delta) {
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
        event.getLineup().remove(entry);
        saveAndRefresh();
    }

    private void saveAndRefresh() {
        try {
            // Save lineup CSV
            CTS.event.LineupEntry.saveToCsv(Paths.get("lineup.csv"), CTS.gui.GUIState.lineupEntries);

            // Save event CSV also (lineup stored inside event)
            Event.saveToCsv(Paths.get("events.csv"), CTS.gui.GUIState.events);

            rebuildUI();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Saving failed: " + e.getMessage());
        }
    }
}
