// i have a lot of a few placeholders in the methods, 
// confused on the our method of storing user data, do we plan on just using ArrayLists 
// or are we going to write and read from a file?


package CTS.user;
import CTS.event.Event;
import CTS.booking.Order;
import CTS.misc.RefundRequest; // Needed for the requestRefund method

import java.util.ArrayList;
import java.util.List;

public class ConcertGoer extends User {

	public ConcertGoer(int userId, String name, String email, String plainPassword) {
        // "super()" runs the constructor of the parent (User) class
        super(userId, name, email, plainPassword);
    }
	
	
	public ArrayList<Event> browseEvents() {
        System.out.println(getName() + " is browsing events...");
        //final code:
        // EventDatabase eventDB = new EventDatabase();
        // return eventDB.getAllEvents();
        return new ArrayList<Event>();
    }

	public ArrayList<Order> viewBookingHistory() {
        System.out.println(getName() + " is viewing their booking history...");
        // final code:
        // BookingDatabase bookingDB = new BookingDatabase();
        // return bookingDB.getOrdersForUser(this.getUserId());
        return new ArrayList<Order>();
    }

	public RefundRequest requestRefund(Order order, String reason) {  // RefundRequest is a class in the refund package
        System.out.println(getName() + " is requesting a refund for order " + order.getOrderId()); // orderId is an attribute to Order class
        // final code:
        // RefundDatabase refundDB = new RefundDatabase();
        // return refundDB.createRefundRequest(order, this, reason); 
        // i'm thinking we will very likely need database classes where we can load/store user data. for example for refund requests.
        return null; 
    }
	
}
