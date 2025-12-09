package CTS.gui;

import CTS.event.Event;
import CTS.booking.Order;
import CTS.booking.OrderDatabase;
import CTS.misc.Money;
import CTS.misc.PaymentTransaction;
import CTS.enums.PaymentType;
import CTS.enums.PaymentStatus;
import CTS.enums.SeatStatus; 
import CTS.seating.*;
import CTS.user.User;

import javax.swing.*;
import java.util.Date;
import java.util.List;

public class GUIBookingFlow {

    private final GUIApp app;

    public GUIBookingFlow(GUIApp app) {
        this.app = app;
    }

    public void purchaseSeats(Event event, Seating seating, List<Integer> seatNumbers, User user) {

        Money base = event.getBasePrice();
        double totalDouble = base.getAmount() * seatNumbers.size();
        Money total = new Money(totalDouble, base.getCurrency());

        String card = JOptionPane.showInputDialog("Enter fake 16-digit card number:");
        if (card == null) return;

        Order order = OrderDatabase.createOrder(
                user.getUserId(),
                event.getEventId(),
                seatNumbers
        );

        for (int num : seatNumbers) {
            Seat s = seating.getSeatByNumber(num);
            if (s != null) {
                s.markSold();  
            }
        }

        SeatingManager.save(seating);

        PaymentTransaction payment = new PaymentTransaction(
                PaymentTransaction.nextId(),
                "GUI-PAY-" + System.currentTimeMillis(),
                PaymentType.CHARGE,            // <-- your real enum value
                total,
                new Date(),
                PaymentStatus.SUCCESS,
                order
        );

        order.setPayment(payment);

        OrderDatabase.attachPayment(order, payment);
        OrderDatabase.saveAll();

        JOptionPane.showMessageDialog(null,
                "Purchase complete!\nSeats: " + seatNumbers);

        app.showMainMenu();
    }
}
