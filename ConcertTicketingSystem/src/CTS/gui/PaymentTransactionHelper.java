// Create a new file: CTS.misc.PaymentTransactionHelper.java

package CTS.gui; 

import CTS.gui.UserOrderHelper; // Import the helper you just provided
import CTS.misc.PaymentTransaction;
import CTS.booking.Order;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;

public class PaymentTransactionHelper {
    
    public static List<PaymentTransaction> getAllPayments() {
        try {
            // 1. Get the list of all Orders (required to link payments)
            List<Order> allOrders = UserOrderHelper.getAllOrders();
            
            // 2. Load and link all transactions
            // NOTE: Assumes PaymentTransaction.loadAll(Path, List<Order>) exists and works
            return PaymentTransaction.loadAll(Paths.get("payments.csv"), allOrders); 
        } catch (Exception e) {
            System.err.println("Error loading all payments: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}