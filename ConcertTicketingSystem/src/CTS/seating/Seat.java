package CTS.seating;

import CTS.enums.SeatStatus;
import CTS.misc.Money; 

public class Seat {

    // --- Attributes ---
    private final int seatId; // Unique ID
    private final String rowLabel;
    private final int seatNumber;
    private SeatStatus status;
    private Money currentPrice;
    
    // Relationship IDs
    private final int sectionId;
    private final int eventId;

    /**
     * Constructor for a Seat object.
     */
    public Seat(int eventId, int seatId, int sectionId, String rowLabel, int seatNumber, Money price) {
        this.eventId = eventId;
        this.seatId = seatId;
        this.sectionId = sectionId;
        this.rowLabel = rowLabel;
        this.seatNumber = seatNumber;
        this.currentPrice = price;
        this.status = SeatStatus.AVAILABLE; // Initial state
    }

    
    // Marks the seat as AVAILABLE.
    public boolean markAvailable() {
        if (this.status != SeatStatus.AVAILABLE) {
            this.status = SeatStatus.AVAILABLE;
            return true;
        }
        return false;
    }

    // Marks the seat as HELD (AVAILABLE -> HELD).
    public boolean markHeld() {
        if (this.status == SeatStatus.AVAILABLE) { // Can only hold if available
            this.status = SeatStatus.HELD;
            return true;
        }
        return false;
    }

    // Marks the seat as SOLD (HELD -> SOLD).
    public boolean markSold() {
        if (this.status == SeatStatus.HELD) { // Can only sell if held
            this.status = SeatStatus.SOLD;
            return true;
        }
        return false;
    }

    // Marks the seat as ADMIN_HELD (reserved by venue).
    public boolean markAdminHeld() {
        if (this.status == SeatStatus.AVAILABLE) {
            this.status = SeatStatus.ADMIN_HELD;
            return true;
        }
        return false;
    }


    // --- Getters & Setters ---

    public int getSeatId() { return seatId; }
    public String getRowLabel() { return rowLabel; }
    public int getSeatNumber() { return seatNumber; }
    public SeatStatus getStatus() { return status; }
    public Money getCurrentPrice() { return currentPrice; }
    public int getSectionId() { return sectionId; }
    public int getEventId() { return eventId; }

    // Allows admin to change price (UC-12)
    public void setCurrentPrice(Money newPrice) { 
        this.currentPrice = newPrice; 
    }

    @Override
    public String toString() {
        return String.format("%s%d (Sec: %d) - %s - Price: %s", 
            rowLabel, seatNumber, sectionId, status.toString(), currentPrice.toString());
    }
}