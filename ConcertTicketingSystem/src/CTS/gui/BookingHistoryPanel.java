package CTS.gui;

import CTS.booking.Order;
import CTS.booking.Ticket;
import CTS.event.Event;
import CTS.user.User;
import CTS.misc.RefundRequest;

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
    
    private void requestRefund(Order order) {
        String reason = JOptionPane.showInputDialog("Reason for refund:");
        if (reason == null || reason.isBlank())
            return;

        try {
            int nextId = RefundRequest.nextId();

            RefundRequest rr = new RefundRequest(
                    nextId,
                    order,
                    new java.util.Date(),
                    reason,
                    CTS.enums.RefundStatus.PENDING
            );

            // Append to CSV
            RefundRequest.append(java.nio.file.Paths.get("refunds.csv"), rr);

            JOptionPane.showMessageDialog(this, "Refund request submitted.");

            // Refresh UI
            removeAll();
            buildUI();
            revalidate();
            repaint();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error submitting refund: " + ex.getMessage());
        }
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
        JLabel total = new JLabel("Total: " + order.getTotalAmount().toString());
        JLabel status = new JLabel("Status: " + order.getStatus());

        card.add(title);
        card.add(Box.createVerticalStrut(5));
        card.add(eventName);
        card.add(orderDate);
        card.add(total);
        card.add(status);
        card.add(Box.createVerticalStrut(10));

        // --- TICKETS ---
        JLabel ticketsLabel = new JLabel("Tickets:");
        ticketsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        card.add(ticketsLabel);

        List<Ticket> tickets = order.getTickets();
        if (tickets.isEmpty()) {
            card.add(new JLabel("  (No tickets found in this order)"));
        } else {
            for (Ticket t : tickets) {
                JPanel tPanel = new JPanel(new GridLayout(1, 2));
                tPanel.add(new JLabel("  " + t.getSeatLabel()));
                tPanel.add(new JLabel("Price: " + t.getPrice().toString()));
                tPanel.setBackground(new Color(245, 245, 245));
                card.add(tPanel);
            }
        }

        card.add(Box.createVerticalStrut(10));

        // ===========================
        // REFUND STATUS + BUTTON ROW
        // ===========================

        JPanel refundPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        refundPanel.setBackground(new Color(245,245,245));

        RefundRequest existing = RefundLookupHelper.findRefundForOrder(order);

        if (order.getStatus().toString().equals("REFUNDED")) {
            refundPanel.add(new JLabel("Refund: APPROVED"));
        }
        else if (existing != null) {
            switch (existing.getStatus()) {
                case PENDING -> refundPanel.add(new JLabel("Refund: PENDING"));
                case APPROVED -> refundPanel.add(new JLabel("Refund: APPROVED"));
                case DENIED -> refundPanel.add(new JLabel("Refund: DENIED"));
            }
        }
        else {
            JButton req = new JButton("Request Refund");
            req.addActionListener(ev -> requestRefund(order));
            refundPanel.add(req);
        }

        card.add(refundPanel);

        return card;
    }

}

        
