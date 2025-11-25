package CTS.booking;

import java.util.ArrayList;
import java.util.Date;

import CTS.misc.Money;
import CTS.misc.OrderStatus;

public class Order {
    private int orderID;
    private Date createdAt;
    private Money totalAmount;
    private OrderStatus status;

    private ArrayList<Ticket> shoppingCart;

    public Order() {
        this.shoppingCart = new ArrayList<>();
        this.createdAt = new Date();
        this.status = OrderStatus.PENDING;
        this.totalAmount = new Money(0.0, "USD");
    }

    public Order(int orderId) {
        this();
        this.orderID = orderId;
    }

    public int getOrderId() {
        return orderID;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public double calculateTotal() {
        double sum = 0.0;
        for (Ticket t : shoppingCart) {
            if (t != null && t.getPrice() != null) {
                sum += t.getPrice().getAmount();
            }
        }
        totalAmount.setAmount(sum);
        return sum;
    }

    public void addTicket(Ticket ticket) {
        if (ticket != null) {
            shoppingCart.add(ticket);
        }
    }

    public void markRefunded() {
        this.status = OrderStatus.REFUNDED;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELED;
    }
}
