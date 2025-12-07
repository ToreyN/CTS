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
            event = new Event(
                    generateId(),
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

                // Load all events
                List<Event> list = Event.loadFromCsv(Paths.get("events.csv"));

                // Replace existing event
                list.removeIf(e -> e.getEventId() == finalEvent.getEventId());
                list.add(finalEvent);

                // Save back to CSV
                Event.saveToCsv(Paths.get("events.csv"), list);

                onSave.run();
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving event: " + ex.getMessage());
            }
        });

        add(save);

        setVisible(true);
    }

    private int generateId() {
        try {
            var events = Event.loadFromCsv(Paths.get("events.csv"));
            return events.stream().mapToInt(Event::getEventId).max().orElse(0) + 1;
        } catch (Exception e) {
            return 1;
        }
    }
}
