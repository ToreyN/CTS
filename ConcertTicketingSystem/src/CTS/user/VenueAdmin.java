package CTS.user;
import CTS.event.Event;
import CTS.seating.PricingTier;
import CTS.seating.Section;
import CTS.misc.RefundRequest;

import java.util.ArrayList;

public class VenueAdmin extends User {

	public VenueAdmin(int userId, String name, String email, String plainPassword) {
        // "super()" runs the constructor of the parent (User) class
        super(userId, name, email, plainPassword);
    }
	
	public Event addEvent(Event eventData) {
        System.out.println("Admin " + getName() + " is adding event: " + eventData.getName());
        // Your real code:
        // EventDatabase eventDB = new EventDatabase();
        // return eventDB.createEvent(eventData);
        return null;
    }

    public void editEvent(Event event) {
        System.out.println("Admin " + getName() + " is editing event: " + event.getName());
        // final code:
        // EventDatabase eventDB = new EventDatabase();
        // eventDB.updateEvent(event);
    }
    
    public void setPricing(Event event, ArrayList<PricingTier> tiers) {
        System.out.println("Admin " + getName() + " is setting pricing for " + event.getName());
        // final code:
        // SeatingDatabase seatingDB = new SeatingDatabase();
        // seatingDB.setPricingForEvent(event, tiers);
    }

    public void reserveSeatBlocks(Event event, Section section, ArrayList<String> rows, String reason) {
        System.out.println("Admin " + getName() + " is reserving seat blocks...");
        // final code:
        // SeatingDatabase seatingDB = new SeatingDatabase();
        // seatingDB.reserveSeats(event, section, rows, reason);
    }
    
    public void processRefundRequest(RefundRequest request, boolean approved, String reason) {
        System.out.println("Admin " + getName() + " is processing refund " + request.getRefundId());
        // final code:
        // RefundDatabase refundDB = new RefundDatabase();
        // refundDB.processRequest(request, this, approved, reason);
    }
    
    public ArrayList<RefundRequest> viewAllRefundRequests() {
        System.out.println("Admin " + getName() + " is viewing refund requests...");
        // final code:
        // RefundDatabase refundDB = new RefundDatabase();
        // return refundDB.getAllPendingRequests();
        return new ArrayList<RefundRequest>();
    }
}
