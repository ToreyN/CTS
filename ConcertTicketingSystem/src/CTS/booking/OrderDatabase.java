package CTS.booking;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import CTS.misc.PaymentTransaction;

public class OrderDatabase {

    private static final Path ORDERS_FILE  = Paths.get("orders.csv");
    private static final Path TICKETS_FILE = Paths.get("tickets.csv");

    private static List<Order> orders = new ArrayList<>();
    private static List<Ticket> tickets = new ArrayList<>();

    private static int nextOrderId = 1;
    private static int nextTicketId = 1;

    // Load once at startup
    static {
        try {
            orders = Order.loadFromCsv(ORDERS_FILE);
            for (Order o : orders) {
                nextOrderId = Math.max(nextOrderId, o.getOrderId() + 1);
            }
        } catch (IOException e) {
            orders = new ArrayList<>();
        }

        try {
            tickets = Ticket.loadFromCsv(TICKETS_FILE);
            for (Ticket t : tickets) {
                nextTicketId = Math.max(nextTicketId, t.getTicketId() + 1);
            }
        } catch (IOException e) {
            tickets = new ArrayList<>();
        }

        // Relink tickets to orders
        rebuild();
    }

    private static void rebuild() {
        for (Order o : orders) {
            o.getTickets().clear();
        }
        for (Ticket t : tickets) {
            Order parent = getOrderById(t.getOrderId());
            if (parent != null) {
                parent.addTicket(t);
            }
        }
    }

    public static Order getOrderById(int id) {
        for (Order o : orders) {
            if (o.getOrderId() == id) return o;
        }
        return null;
    }

    /** Creates a new order and generates tickets for the given seat numbers. */
    public static Order createOrder(int userId, int eventId, List<Integer> seatNumbers) {

        Order order = new Order(nextOrderId++, userId);

        for (int seatNum : seatNumbers) {
            Ticket t = new Ticket(
                nextTicketId++,
                order.getOrderId(),
                eventId,
                order.getTotalAmount(), // Price updated later
                "Seat " + seatNum
            );
            order.addTicket(t);
            tickets.add(t);
        }

        orders.add(order);

        return order;
    }

    /** Link a payment transaction to an order */
    public static void attachPayment(Order order, PaymentTransaction txn) {
        order.setPayment(txn);
    }

    /** Saves all CSV files (orders + tickets + payments) */
    public static void saveAll() {
        try {
            Order.saveToCsv(ORDERS_FILE, orders);
        } catch (IOException e) {}

        try {
            Ticket.saveToCsv(TICKETS_FILE, tickets);
        } catch (IOException e) {}
    }
}
