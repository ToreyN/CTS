package CTS.gui;

import CTS.misc.RefundRequest;
import CTS.misc.PaymentTransaction;
import CTS.enums.RefundStatus;
import CTS.user.User;
import CTS.booking.Order;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;
import java.util.List;

public class RefundProcessPanel extends JPanel {

    private final User adminUser;

    public RefundProcessPanel(User admin) {
        this.adminUser = admin;

        setLayout(new BorderLayout());

        List<RefundRequest> refunds;
        try {
            refunds = RefundRequest.loadAll(Paths.get("refunds.csv"), GUIState.orders);
        } catch (Exception e) {
            refunds = List.of();
        }

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        for (RefundRequest r : refunds) {
            if (r.getStatus() != RefundStatus.PENDING)
                continue;

            listPanel.add(buildRefundCard(r));
        }

        add(new JScrollPane(listPanel), BorderLayout.CENTER);
    }

    private JPanel buildRefundCard(RefundRequest r) {
        JPanel card = new JPanel(new GridLayout(0,1));
        card.setBorder(BorderFactory.createTitledBorder("Refund ID: " + r.getRefundId()));

        Order o = r.getOrder();
        card.add(new JLabel("Order ID: " + o.getOrderId()));
        card.add(new JLabel("Amount: " + o.getTotalAmount()));
        card.add(new JLabel("Reason: " + r.getReason()));

        JButton approve = new JButton("Approve");
        JButton deny = new JButton("Deny");

        approve.addActionListener(ev -> approveRefund(r));
        deny.addActionListener(ev -> denyRefund(r));

        JPanel buttons = new JPanel();
        buttons.add(approve);
        buttons.add(deny);

        card.add(buttons);
        return card;
    }

    private void approveRefund(RefundRequest r) {
        r.approve(adminUser);
        r.getOrder().markRefunded();

        try {
            RefundRequest.saveAll(Paths.get("refunds.csv"), GUIState.refunds);
            JOptionPane.showMessageDialog(this, "Refund approved.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving refund.");
        }
    }

    private void denyRefund(RefundRequest r) {
        String reason = JOptionPane.showInputDialog("Reason for denial:");
        if (reason == null || reason.isBlank()) return;

        r.deny(adminUser, reason);

        try {
            RefundRequest.saveAll(Paths.get("refunds.csv"), GUIState.refunds);
            JOptionPane.showMessageDialog(this, "Refund denied.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving refund.");
        }
    }
}
