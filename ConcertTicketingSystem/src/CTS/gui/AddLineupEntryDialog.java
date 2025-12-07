package CTS.gui;

import CTS.event.Artist;
import CTS.event.Event;
import CTS.event.LineupEntry;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AddLineupEntryDialog extends JDialog {

    public AddLineupEntryDialog(Window parent, Event event, Runnable onSave) {
        super(parent, "Add Lineup Entry", ModalityType.APPLICATION_MODAL);

        setSize(320, 220);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(0,1));

        /* ---------------- LOAD ARTISTS ---------------- */
        final List<Artist> artists;
        try {
            artists = Artist.loadFromCsv(Paths.get("artists.csv"));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to load artists.csv\n" + e.getMessage());
            dispose();
            return;
        }

        if (artists.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No artists found. Create artists first.");
            dispose();
            return;
        }

        /* ---------------- UI CONTROLS ---------------- */
        JComboBox<String> artistSelect = new JComboBox<>(
                artists.stream().map(Artist::getStageName).toArray(String[]::new)
        );

        JTextField position = new JTextField("" + (event.getLineup().size() + 1));
        JTextField notes = new JTextField("");

        add(new JLabel("Artist:"));
        add(artistSelect);

        add(new JLabel("Position:"));
        add(position);

        add(new JLabel("Notes:"));
        add(notes);

        /* ---------------- SAVE BUTTON ---------------- */
        JButton save = new JButton("Add Entry");
        save.addActionListener(ev -> {
            try {
                Artist a = artists.get(artistSelect.getSelectedIndex());
                int pos = Integer.parseInt(position.getText());

                LineupEntry entry = new LineupEntry(
                        event.getEventId(),
                        pos,
                        notes.getText(),
                        a
                );

                event.getLineup().add(entry);

                if (onSave != null) onSave.run();
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        add(save);
        setVisible(true);
    }
}
