package CTS.booking;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import CTS.misc.Money;
import CTS.misc.PaymentTransaction;
import CTS.enums.OrderStatus;

public class Order {
    private int orderID;
    private int userId; 
    private Date createdAt;
    private Money totalAmount;
    private OrderStatus status;
    private PaymentTransaction payment;

    // This is a temporary field, not saved to CSV.
    // It gets rebuilt by Main.java's rebuildOrdersAndTickets() method.
    private ArrayList<Ticket> shoppingCart; 

    
     // Default constructor
     
    public Order() {
        this.shoppingCart = new ArrayList<>();
        this.createdAt = new Date();
        this.status = OrderStatus.PENDING; // Default status
        this.totalAmount = new Money(0.0, "USD");
    }

    public static Order findById(int id, List<Order> all) {
        for (Order o : all) {
            if (o.getOrderId() == id) return o;
        }
        return null;
    }

     // Constructor for a new order, linking it to a user.
     
    public Order(int orderId, int userId) { 
        this(); // Calls the default constructor above
        this.orderID = orderId;
        this.userId = userId; 
    }
    
     
     //Constructor used only by fromCsvRow when loading from file.
    
    private Order(int orderId, int userId, Date createdAt, OrderStatus status) {
        this.shoppingCart = new ArrayList<>(); // Initialize empty cart
        this.orderID = orderId;
        this.userId = userId;
        this.createdAt = createdAt;
        this.status = status;
        this.totalAmount = new Money(0.0, "USD"); // Will be recalculated
    }

    // --- Getters ---

    public int getOrderId() {
        return orderID;
    }

    public int getUserId() { 
        return userId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Money getTotalAmount() {
        // Recalculate just in case tickets were added
        calculateTotal(); 
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }
    
    public ArrayList<Ticket> getTickets() {
        return shoppingCart;
    }

    // --- Core Logic ---

    
     // Recalculates the total price of all tickets in the cart.
     
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

    public void setPayment(PaymentTransaction payment) {
        this.payment = payment;
        if (payment != null) {
            this.status = OrderStatus.CONFIRMED; // Update status on successful payment
        }
    }
    
     //Adds a ticket to the order and updates the total price.
     
    public void addTicket(Ticket ticket) {
        if (ticket != null) {
            shoppingCart.add(ticket);
            calculateTotal(); // Update total when ticket is added
        }
    }

    // --- Status Changers ---

    public void markRefunded() {
        this.status = OrderStatus.REFUNDED;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELED;
    }
    
    // =========================================================================
    //  CSV PERSISTENCE
    // =========================================================================

    /**
     * --- NEW METHOD ---
     * Format: orderId,userId,createdAtMillis,status
     */
    public String toCsvRow() {
        long millis = (createdAt != null) ? createdAt.getTime() : 0L;
        return orderID + "," +
               userId + "," +
               millis + "," +
               status.name();
    }

    /**
     * Rebuilds an Order object from a CSV row.
     */
    public static Order fromCsvRow(String line) {
        try {
            String[] parts = line.split(",", 4);
            int orderId = Integer.parseInt(parts[0]);
            int userId = Integer.parseInt(parts[1]);
            long millis = Long.parseLong(parts[2]);
            OrderStatus status = OrderStatus.valueOf(parts[3]);
            
            Date createdAt = (millis == 0L) ? null : new Date(millis);
            
            return new Order(orderId, userId, createdAt, status);
        } catch (Exception e) {
            System.err.println("Skipping malformed order line: " + line);
            return null;
        }
    }

    /**
     * Loads a list of all orders from orders.csv
     */
    public static List<Order> loadFromCsv(Path path) throws IOException {
        List<Order> orders = new ArrayList<>();
        if (!Files.exists(path)) {
            return orders; // Return empty list if no file
        }
        
        List<String> lines = Files.readAllLines(path);
        for (String line : lines) {
            if (line.trim().isEmpty() || line.startsWith("#")) {
                continue;
            }
            Order o = fromCsvRow(line);
            if (o != null) {
                orders.add(o);
            }
        }
        return orders;
    }

    /**
     * Saves a list of all orders to orders.csv
     */
    public static void saveToCsv(Path path, List<Order> orders) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# orderId,userId,createdAtMillis,status");
        for (Order o : orders) {
            lines.add(o.toCsvRow());
        }
        Files.write(path, lines);
    }
}