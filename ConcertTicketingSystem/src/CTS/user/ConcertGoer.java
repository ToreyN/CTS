


package CTS.user;
import CTS.event.Event;
import CTS.booking.Order;
import CTS.misc.RefundRequest; 

//import java.util.ArrayList;
import java.util.List;

//import CTS.event.Event;
import CTS.enums.EventStatus; 
import CTS.enums.RefundStatus; 
//import CTS.booking.Order;
//import CTS.misc.RefundRequest; 
import java.util.Date;
//import java.util.ArrayList;
//import java.util.List;
import java.util.stream.Collectors; // For filtering

public class ConcertGoer extends User {


   


//  Constructor for a BRAND NEW user (called by userDatabase.createUser)

    public ConcertGoer(int userId, String name, String email, String plainPassword) {
        super(userId, name, email, plainPassword, "USER"); 
    }
    

//  Constructor for LOADING an existing user (called by User.fromCsvRow)

    public ConcertGoer(int userId, String name, String email, String passwordHash, LoadFrom tag) {
        super(userId, name, email, passwordHash, "USER", tag);
    }

    

// Gets only PUBLISHED events from the main list.

    public List<Event> browseEvents(List<Event> allEvents) {
        System.out.println(getName() + " is browsing events...");
        
        // This is where the logic belongs
        return allEvents.stream()
                .filter(e -> e.getStatus() == EventStatus.PUBLISHED)
                .collect(Collectors.toList());
    }


// Gets this user's orders from the main list.

    public List<Order> viewBookingHistory(List<Order> allOrders) {
        System.out.println(getName() + " is viewing their booking history...");
        
        return allOrders.stream()
                .filter(order -> order.getUserId() == this.getUserId()) 
                .collect(Collectors.toList());
    }


//  Creates a new refund request and adds it to the main list.

    public RefundRequest requestRefund(Order order, String reason, List<RefundRequest> allRefundRequests, int newRefundId) {
        System.out.println(getName() + " is requesting a refund for order " + order.getOrderId());

        // The Main class is responsible for giving us a new, unique ID
        RefundRequest newRequest = new RefundRequest(newRefundId, order, new Date(), reason, RefundStatus.PENDING);
        
        allRefundRequests.add(newRequest); // Add it to the main list
        return newRequest; 
    }
}
