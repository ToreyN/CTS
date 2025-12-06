package CTS.gui;

import CTS.event.Event;
import CTS.enums.EventStatus;
import CTS.misc.Money;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

public class EventEditorDialog extends JDialog {

    public EventEditorDialog(Window parent, Event event, Runnable onSave) {
        super(parent, "Event Editor", ModalityType.APPLICATION_MODAL);
        setSize(400, 450);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(0,1));

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

        JTextField name = new JTextField(event.getName());
        JTextField venue = new JTextField(event.getVenueName());
        JTextField desc = new JTextField(event.getDescription());
        JTextField capacity = new JTextField("" + event.getCapacity());
        JTextField price = new JTextField("" + event.getBasePrice().getAmount());

        JTextField dateField = new JTextField(
                event.getStartDateTime() == null ? "" :
                new SimpleDateFormat("yyyy-MM-dd HH:mm").format(event.getStartDateTime())
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
                event.updateDescription(desc.getText());
                event.setCapacity(Integer.parseInt(capacity.getText()));
                event.setEventId(event.getEventId());
                
                event.setPrice(new Money(Double.parseDouble(price.getText()), "USD"));
                event.setVenue(venue.getText());
                event.setName(name.getText());

                String d = dateField.getText().trim();
                if (!d.isBlank()) {
                    event.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(d));
                }

                // Save the event back to CSV
                var list = Event.loadFromCsv(Paths.get("events.csv"));
                var existing = list.stream().filter(e -> e.getEventId() == event.getEventId()).findFirst();

                if (existing.isPresent()) {
                    list.remove(existing.get());
                }
                list.add(event);

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
