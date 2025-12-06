package CTS.gui;

import CTS.event.Artist;
import CTS.event.Event;
import CTS.event.LineupEntry;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;

public class AddLineupEntryDialog extends JDialog {

    public AddLineupEntryDialog(Window parent, Event event, Runnable onSave) {
        super(parent, "Add Lineup Entry", ModalityType.APPLICATION_MODAL);

        setSize(350, 250);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(0,1));

        List<Artist> artists;
        try {
            artists = Artist.loadFromCsv(Paths.get("artists.csv"));
        } catch (Exception e) {
            artists = List.of();
        }

        JComboBox<String> artistSelect = new JComboBox<>(
                artists.stream().map(Artist::getStageName).toArray(String[]::new)
        );

        JTextField position = new JTextField("1");
        JTextField notes = new JTextField("");

        add(new JLabel("Artist:"));
        add(artistSelect);

        add(new JLabel("Position:"));
        add(position);

        add(new JLabel("Notes:"));
        add(notes);

        JButton save = new JButton("Add");
        save.addActionListener(ev -> {
            try {
                Artist a = artists.get(artistSelect.getSelectedIndex());
                int pos = Integer.parseInt(position.getText());

                LineupEntry entry = new LineupEntry(event.getEventId(), pos, notes.getText(), a);
                event.getLineup().add(entry);

                onSave.run();
                dispose();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        });

        add(save);
        setVisible(true);
    }
}
