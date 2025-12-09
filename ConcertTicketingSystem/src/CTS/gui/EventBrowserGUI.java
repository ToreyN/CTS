package CTS.gui;

import CTS.event.Event;
import CTS.enums.EventStatus;
import CTS.user.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class EventBrowserGUI extends JFrame {

    private final User currentUser;

    public EventBrowserGUI(User user) {
        this.currentUser = user;

        setTitle("Browse Events");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        loadUI();
    }

    private void loadUI() {

        List<Event> events = Event.getAllPublishedEvents();
        if (events.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No published events available.");
            dispose();
            return;
        }

        String[] names = events.stream()
                .map(Event::getName)
                .toArray(String[]::new);

        JList<String> list = new JList<>(names);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(list);

        JButton viewBtn = new JButton("View Details");
        viewBtn.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx < 0) {
                JOptionPane.showMessageDialog(this, "Select an event first.");
                return;
            }
            Event selected = events.get(idx);
            new EventDetailGUI(currentUser, selected).setVisible(true);
        });

        JPanel bottom = new JPanel();
        bottom.add(viewBtn);

        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }
}
