package CTS.gui;

import CTS.event.Artist;
import CTS.event.Event;
import CTS.event.LineupEntry;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer; // Added for callback

public class AddLineupEntryDialog extends JDialog {

    // Helper method to reload artists and update the JComboBox model
    private List<Artist> loadArtistsAndSetModel(JComboBox<String> artistSelect) {
        try {
            List<Artist> refreshedArtists = Artist.loadFromCsv(Paths.get("artists.csv"));
            
            // Update the JComboBox model dynamically
            String[] newArtistNames = refreshedArtists.stream()
                                                     .map(Artist::getStageName)
                                                     .toArray(String[]::new);
            
            artistSelect.setModel(new DefaultComboBoxModel<>(newArtistNames));
            
            return refreshedArtists;

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to reload artist list: " + ex.getMessage());
            return new ArrayList<>();
        }
    }

    // FIX: Constructor uses Consumer<LineupEntry> for the callback
    public AddLineupEntryDialog(Window parent, Event event, Consumer<LineupEntry> onSave) {
        super(parent, "Add Lineup Entry", ModalityType.APPLICATION_MODAL);

        setSize(320, 220);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(0,1));

        /* ---------------- LOAD ARTISTS & JCOMBOBOX SETUP ---------------- */
        
        final JComboBox<String> artistSelect;
        final List<Artist>[] artistListHolder = new List[1]; 
        
        // Initial Load
        try {
            artistListHolder[0] = Artist.loadFromCsv(Paths.get("artists.csv"));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load artists.csv\n" + e.getMessage());
            dispose();
            return;
        }

        // Show warning but continue, allowing the user to click "New".
        if (artistListHolder[0].isEmpty()) {
            JOptionPane.showMessageDialog(this, "No artists found. Please use the 'New' button to create one.");
        }

        artistSelect = new JComboBox<>(
            artistListHolder[0].stream().map(Artist::getStageName).toArray(String[]::new)
        );
        
        /* ---------------- UI CONTROLS ---------------- */

        JTextField position = new JTextField("" + (event.getLineup().size() + 1));
        JTextField notes = new JTextField("");

        // Setup Panel for JComboBox and "New" Button
        JPanel artistRow = new JPanel(new BorderLayout());
        artistRow.add(artistSelect, BorderLayout.CENTER);
        
        JButton createNew = new JButton("New");
        
        // --- "NEW" BUTTON ACTION LISTENER ---
        createNew.addActionListener(e -> {
            // 1. Open the dedicated Artist Manager dialog
            new ArtistManagerDialog(SwingUtilities.getWindowAncestor(this)).setVisible(true);

            // 2. Refresh the JComboBox after the manager closes
            List<Artist> refreshedArtists = loadArtistsAndSetModel(artistSelect);
            
            // 3. Update the mutable list holder
            artistListHolder[0] = refreshedArtists;
            
            // 4. Select the new artist
            if (!refreshedArtists.isEmpty()) {
                artistSelect.setSelectedIndex(0);
            }
        });
        // --- END ACTION LISTENER ---

        artistRow.add(createNew, BorderLayout.EAST);

        add(new JLabel("Artist:"));
        add(artistRow); 

        add(new JLabel("Position:"));
        add(position);

        add(new JLabel("Notes:"));
        add(notes);

        /* ---------------- SAVE BUTTON ---------------- */
        JButton save = new JButton("Add Entry");
        save.addActionListener(ev -> {
            try {
                // Check if any artist is actually available/selected
                if (artistListHolder[0].isEmpty() || artistSelect.getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(this, "Please select or create an artist before saving.");
                    return;
                }
                
                // CRITICAL: Use the updated list holder to retrieve the Artist
                Artist a = artistListHolder[0].get(artistSelect.getSelectedIndex());
                int pos = Integer.parseInt(position.getText());

                LineupEntry entry = new LineupEntry(
                        event.getEventId(),
                        pos,
                        notes.getText(),
                        a
                );

                event.getLineup().add(entry);

                // FIX: Pass the new entry back to the LineupEditorPanel for global saving
                if (onSave != null) onSave.accept(entry); 
                dispose();

            } catch (NumberFormatException e) {
                 JOptionPane.showMessageDialog(this, "Error: Position must be a valid number.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        add(save);
        setVisible(true);
    }
}