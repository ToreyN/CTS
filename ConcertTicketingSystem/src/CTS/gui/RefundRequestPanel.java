package CTS.gui;

import CTS.booking.Order;
import CTS.enums.OrderStatus;
import CTS.enums.RefundStatus;
import CTS.misc.RefundRequest;
import CTS.user.User;
import CTS.gui.UserOrderHelper;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

public class RefundRequestPanel extends JPanel {

    private final User user;

    public RefundRequestPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {

        removeAll();

        List<Order> myOrders = UserOrderHelper.getOrdersFor(user);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        if (myOrders.isEmpty()) {
            add(new JLabel("You have no orders to request refunds for.", SwingConstants.CENTER));
            return;
        }

        for (Order order : myOrders) {
            listPanel.add(buildOrderCard(order));
            listPanel.add(Box.createVerticalStrut(15));
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        add(scroll, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private JPanel buildOrderCard(Order order) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(new Color(250, 250, 250));

        JLabel title = new JLabel("Order #" + order.getOrderId());
        title.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel status = new JLabel("Status: " + order.getStatus());
        JLabel date = new JLabel("Date: " + order.getCreatedAt());
        JLabel total = new JLabel("Total: " + order.getTotalAmount());

        JPanel header = new JPanel(new GridLayout(3, 1));
        header.setOpaque(false);
        header.add(date);
        header.add(total);
        header.add(status);

        card.add(title);
        card.add(Box.createVerticalStrut(5));
        card.add(header);
        card.add(Box.createVerticalStrut(10));

        JButton requestRefund = new JButton("Request Refund");
        requestRefund.addActionListener(e -> attemptRefund(order));

        // Disable when not allowed
        if (order.getStatus() == OrderStatus.CANCELED ||
            order.getStatus() == OrderStatus.REFUNDED ||
            hasPendingRefund(order)) {

            requestRefund.setEnabled(false);
        }

        JPanel bottom = new JPanel();
        bottom.add(requestRefund);
        card.add(bottom);

        return card;
    }

    private void attemptRefund(Order order) {

        if (hasPendingRefund(order)) {
            JOptionPane.showMessageDialog(this,
                    "A refund request for this order is already pending.");
            return;
        }

        if (order.getStatus() == OrderStatus.CANCELED) {
            JOptionPane.showMessageDialog(this,
                    "This order was canceled and cannot be refunded.");
            return;
        }

        if (order.getStatus() == OrderStatus.REFUNDED) {
            JOptionPane.showMessageDialog(this,
                    "This order has already been refunded.");
            return;
        }

        String reason = JOptionPane.showInputDialog(
                this,
                "Enter reason for refund:",
                "Refund Request",
                JOptionPane.PLAIN_MESSAGE
        );

        if (reason == null || reason.isBlank()) {
            JOptionPane.showMessageDialog(this, "Refund request canceled.");
            return;
        }

        submitRefund(order, reason);
    }

    private void submitRefund(Order order, String reason) {
        try {
            int nextId = RefundRequest.nextId();

            RefundRequest rr = new RefundRequest(
                    nextId,
                    order,
                    new Date(),
                    reason,
                    RefundStatus.PENDING
            );

            // Save new refund request to CSV
            RefundRequest.append(Paths.get("refunds.csv"), rr);

            JOptionPane.showMessageDialog(this,
                    "Refund request submitted!\nRefund ID: " + nextId);

            // Refresh UI so button becomes disabled
            buildUI();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error submitting refund: " + ex.getMessage());
        }
    }

    private boolean hasPendingRefund(Order order) {
        try {
            // Load ALL orders directly from CSV
            List<Order> allOrders = Order.loadFromCsv(Paths.get("orders.csv"));

            // Load all refunds, linking each to its Order
            List<RefundRequest> all =
                    RefundRequest.loadAll(
                            Paths.get("refunds.csv"),
                            allOrders
                    );

            for (RefundRequest rr : all) {
                if (rr.getOrder() != null &&
                    rr.getOrder().getOrderId() == order.getOrderId() &&
                    rr.getStatus() == RefundStatus.PENDING) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }

        return false;
    }

}
