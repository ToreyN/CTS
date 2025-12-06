package CTS.gui;

import CTS.event.Event;
import CTS.user.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EventBrowserPanel extends JPanel {

    public EventBrowserPanel(User user) {
        setLayout(new BorderLayout());

        List<Event> events = Event.getAllPublishedEvents();

        DefaultListModel<Event> model = new DefaultListModel<>();
        for (Event e : events) model.addElement(e);

        JList<Event> list = new JList<>(model);
        list.setCellRenderer(new EventListRenderer());

        JButton view = new JButton("View Event Details");

        view.addActionListener(e -> {
            Event selected = list.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Select an event first.");
                return;
            }
            new EventDetailGUI(user, selected).setVisible(true);
        });

        add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.add(view);
        add(bottom, BorderLayout.SOUTH);
    }

    private static class EventListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Event e) {
                setText(e.getName() + " @ " + e.getVenueName());
            }
            return this;
        }
    }
}
