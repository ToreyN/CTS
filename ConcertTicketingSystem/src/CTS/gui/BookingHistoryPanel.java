package CTS.gui;

import CTS.booking.Order;
import CTS.booking.Ticket;
import CTS.event.Event;
import CTS.user.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class BookingHistoryPanel extends JPanel {

    private final User user;

    public BookingHistoryPanel(User user) {
        this.user = user;

        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {

        List<Order> myOrders = UserOrderHelper.getOrdersFor(user);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        if (myOrders.isEmpty()) {
            JLabel empty = new JLabel("You have no bookings yet.");
            empty.setHorizontalAlignment(SwingConstants.CENTER);
            add(empty, BorderLayout.CENTER);
            return;
        }

        for (Order o : myOrders) {
            listPanel.add(buildOrderCard(o));
            listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        add(scroll, BorderLayout.CENTER);
    }


    private JPanel buildOrderCard(Order order) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(new Color(245, 245, 245));

        Event event = EventDetailsHelper.getEvent(order);

        JLabel title = new JLabel("Order #" + order.getOrderId());
        title.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel eventName = new JLabel("Event: " + (event != null ? event.getName() : "Unknown Event"));
        JLabel orderDate = new JLabel("Date: " + order.getCreatedAt());
        JLabel total = new JLabel("Total: " + order.getTotalAmount());
        JLabel status = new JLabel("Status: " + order.getStatus());

        card.add(title);
        card.add(Box.createVerticalStrut(5));
        card.add(eventName);
        card.add(orderDate);
        card.add(total);
        card.add(status);
        card.add(Box.createVerticalStrut(10));

        // Tickets header
        JLabel ticketsLabel = new JLabel("Tickets:");
        ticketsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        card.add(ticketsLabel);

        List<Ticket> tickets = order.getTickets();

        if (tickets.isEmpty()) {
            card.add(new JLabel("  (No tickets found in this order)"));
        } else {
            for (Ticket t : tickets) {
                JPanel tPanel = new JPanel(new GridLayout(1, 2));

                JLabel seatInfo = new JLabel("  " + t.getSeatLabel());
                JLabel price = new JLabel("Price: " + t.getPrice());

                tPanel.add(seatInfo);
                tPanel.add(price);
                tPanel.setBackground(new Color(245, 245, 245));

                card.add(tPanel);
            }
        }
        
        return card;
    }
}

        
