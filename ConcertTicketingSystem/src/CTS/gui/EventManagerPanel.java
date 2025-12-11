package CTS.gui;

import CTS.event.Event;
import CTS.enums.EventStatus;
import CTS.misc.Money;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;

public class EventManagerPanel extends JPanel {

    private List<Event> events; 

    public EventManagerPanel() {
        setLayout(new BorderLayout());

        try {
            this.events = Event.loadFromCsv(Paths.get("events.csv"));
        } catch (Exception e) {
            this.events = new ArrayList<>(); 
        }

        buildUI();
    }
    
    private void buildUI() {
        this.removeAll(); 
        setLayout(new BorderLayout());

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        for (Event e : this.events) { 
            list.add(buildEventCard(e));
        }

        JScrollPane scroll = new JScrollPane(list);
        add(scroll, BorderLayout.CENTER);

        JButton createBtn = new JButton("Create New Event");
        createBtn.addActionListener(ev -> openEventEditor(null));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(createBtn);
        add(top, BorderLayout.NORTH);

        revalidate(); 
        repaint();    
    }


    private JPanel buildEventCard(Event event) {
        JPanel card = new JPanel(new GridLayout(0,1));
        card.setBorder(BorderFactory.createTitledBorder(event.getName() + " (ID " + event.getEventId() + ")"));

        card.add(new JLabel("Venue: " + event.getVenueName()));
        card.add(new JLabel("Capacity: " + event.getCapacity()));
        card.add(new JLabel("Status: " + event.getStatus()));
        card.add(new JLabel("Price: " + event.getBasePrice()));
        card.add(new JLabel("Tickets Sold: " + event.getTicketsSold()));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton edit = new JButton("Edit");
        edit.addActionListener(ev -> openEventEditor(event));

        JButton publish = new JButton("Publish");
        publish.setEnabled(event.getStatus() == EventStatus.DRAFT);
        publish.addActionListener(ev -> {
            event.publish();
            saveChanges(); 
        });

        JButton cancel = new JButton("Cancel Event");
        cancel.setEnabled(event.getStatus() == EventStatus.PUBLISHED);
        cancel.addActionListener(ev -> {
            event.cancel();
            saveChanges(); 
        });

        actions.add(edit);
        actions.add(publish);
        actions.add(cancel);

        card.add(actions);

        return card;
    }

    private void openEventEditor(Event event) {
        new EventEditorDialog(SwingUtilities.getWindowAncestor(this), event, this::saveChanges);
    }

    private void saveChanges() {
        try {
            // CRITICAL: Save the list held in memory (which contains the published/canceled change)
            CTS.event.Event.saveToCsv(Paths.get("events.csv"), this.events); 
            
            refreshUI(); 
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving events: " + ex.getMessage());
        }
    }
    
    private void refreshUI() {
        try {
            this.events = CTS.event.Event.loadFromCsv(Paths.get("events.csv"));
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "Warning: Failed to reload event data after save.", "Data Warning", JOptionPane.WARNING_MESSAGE);
        }
        
        buildUI();
    }
}