package CTS.gui;

import CTS.misc.RefundRequest;
import CTS.misc.PaymentTransaction;
import CTS.enums.RefundStatus;
import CTS.enums.PaymentStatus;
import CTS.enums.PaymentType;
import CTS.user.User;
import CTS.user.VenueAdmin;
import CTS.booking.Order;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

public class RefundProcessPanel extends JPanel {

    private final VenueAdmin adminUser;

    private final List<Order> orders;
    private final List<RefundRequest> refunds;
    private final List<PaymentTransaction> payments;

    public RefundProcessPanel(VenueAdmin admin,
                              List<Order> orders,
                              List<RefundRequest> refunds,
                              List<PaymentTransaction> payments) {

        this.adminUser = admin;
        this.orders = orders;
        this.refunds = refunds;
        this.payments = payments;

        setLayout(new BorderLayout());
        rebuildUI();
    }

    private void rebuildUI() {

        removeAll();

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        for (RefundRequest r : refunds) {
            if (r.getStatus() == RefundStatus.PENDING) {
                list.add(buildRefundCard(r));
                list.add(Box.createVerticalStrut(10));
            }
        }

        add(new JScrollPane(list), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel buildRefundCard(RefundRequest r) {

        JPanel card = new JPanel(new GridLayout(0, 1));
        card.setBorder(BorderFactory.createTitledBorder("Refund #" + r.getRefundId()));

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

        Order o = r.getOrder();

        // Create a refund transaction
        PaymentTransaction txn = new PaymentTransaction(
                PaymentTransaction.nextId(),
                "refund_" + r.getRefundId(),
                PaymentType.REFUND,
                o.getTotalAmount(),
                new Date(),
                PaymentStatus.SUCCESS,
                o
        );

        payments.add(txn);
        r.setRefundTxn(txn);

        r.approve(adminUser);
        o.markRefunded();

        saveRefunds();
        savePayments();
        rebuildUI();

        JOptionPane.showMessageDialog(this, "Refund approved.");
    }

    private void denyRefund(RefundRequest r) {

        String reason = JOptionPane.showInputDialog("Reason for denial:");
        if (reason == null || reason.isBlank())
            return;

        r.deny(adminUser, reason);

        saveRefunds();
        rebuildUI();

        JOptionPane.showMessageDialog(this, "Refund denied.");
    }

    private void saveRefunds() {
        try {
            RefundRequest.saveToCsv(Paths.get("refunds.csv"), refunds);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving refunds: " + e.getMessage());
        }
    }

    private void savePayments() {
        try {
            PaymentTransaction.saveToCsv(Paths.get("payments.csv"), payments);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving payments: " + e.getMessage());
        }
    }
}
