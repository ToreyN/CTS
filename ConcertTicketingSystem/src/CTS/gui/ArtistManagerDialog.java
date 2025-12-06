package CTS.gui;

import CTS.event.Artist;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;
import java.util.List;

public class ArtistManagerDialog extends JDialog {

    public ArtistManagerDialog(Window parent) {
        super(parent, "Add Artist", ModalityType.APPLICATION_MODAL);

        setSize(300, 200);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(0,1));

        JTextField name = new JTextField();
        JTextField genre = new JTextField();

        add(new JLabel("Artist name:"));
        add(name);

        add(new JLabel("Genre:"));
        add(genre);

        JButton save = new JButton("Save");
        save.addActionListener(ev -> {
            try {
                int newId = generateArtistId();
                Artist a = new Artist(newId, name.getText(), genre.getText());

                var list = Artist.loadFromCsv(Paths.get("artists.csv"));
                list.add(a);
                Artist.saveToCsv(Paths.get("artists.csv"), list);

                dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error saving: " + e.getMessage());
            }
        });

        add(save);
        setVisible(true);
    }

    private int generateArtistId() {
        try {
            var list = Artist.loadFromCsv(Paths.get("artists.csv"));
            return list.stream().mapToInt(Artist::getArtistId).max().orElse(0) + 1;
        } catch (Exception e) {
            return 1;
        }
    }
}
