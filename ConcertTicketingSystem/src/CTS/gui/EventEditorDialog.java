package CTS.gui;

import CTS.event.Event;
import CTS.enums.EventStatus;
import CTS.misc.Money;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EventEditorDialog extends JDialog {

    public EventEditorDialog(Window parent, Event event, Runnable onSave) {
        super(parent, "Event Editor", ModalityType.APPLICATION_MODAL);
        setSize(400, 450);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(0, 1));

        boolean creating = (event == null);
        if (creating) {
            // CRITICAL FIX 1: Use the robust, static ID generator
            event = new Event(
                    CTS.event.Event.nextId(), 
                    "",
                    null,
                    "",
                    "",
                    0,
                    EventStatus.DRAFT,
                    new Money(0.0, "USD")
            );
        }

       
        final Event finalEvent = event;
        // --------------------------------------------------------------------

        JTextField name = new JTextField(finalEvent.getName());
        JTextField venue = new JTextField(finalEvent.getVenueName());
        JTextField desc = new JTextField(finalEvent.getDescription());
        JTextField capacity = new JTextField("" + finalEvent.getCapacity());
        
        // Ensure price display uses only the amount
        JTextField price = new JTextField("" + finalEvent.getBasePrice().getAmount());

        JTextField dateField = new JTextField(
                finalEvent.getStartDateTime() == null ? "" :
                        new SimpleDateFormat("yyyy-MM-dd HH:mm").format(finalEvent.getStartDateTime())
        );

        add(new JLabel("Name:"));
        add(name);
        add(new JLabel("Venue:"));
        add(venue);
        add(new JLabel("Description:"));
        add(desc);
        add(new JLabel("Capacity:"));
        add(capacity);
        add(new JLabel("Price (USD):"));
        add(price);
        add(new JLabel("Start Date (yyyy-MM-dd HH:mm):"));
        add(dateField);

        JButton save = new JButton("Save Event");

        save.addActionListener(ev -> {
            try {
                
                // 1. Update the in-memory event object
                finalEvent.setName(name.getText());
                finalEvent.setVenue(venue.getText());
                finalEvent.updateDescription(desc.getText());
                finalEvent.setCapacity(Integer.parseInt(capacity.getText()));
                finalEvent.setPrice(new Money(Double.parseDouble(price.getText()), "USD"));

                String d = dateField.getText().trim();
                if (!d.isBlank()) {
                    finalEvent.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(d));
                }
                // ----------------------------------------------------------------

                // CRITICAL FIX 2: REMOVE ALL FILE I/O HERE. 
                // We rely on EventManagerPanel to manage the list and save it.
                
                // The onSave runnable (which is EventManagerPanel::saveChanges)
                // is responsible for checking if this is a NEW event and adding it
                // to its internal list before saving.
                
                onSave.run(); // Calls the save and refresh logic in EventManagerPanel
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving event: " + ex.getMessage());
            }
        });

        add(save);

        setVisible(true);
    }
    
    // CRITICAL FIX 3: DELETE THE generateId() METHOD.
}