package CTS.seating;
import CTS.misc.*;
import CTS.enums.*;

public class Seat {
	private int seatID;
	private String rowLabel;
	private int seatNumber;
	private SeatStatus status;
	private Money currentPrice;
	
	public void markAvailable() {
		status = SeatStatus.AVAILABLE;
	}
	
	public void markHeld() {
		status = SeatStatus.HELD;
	}
	
	public void markSold() {
		status = SeatStatus.SOLD;
	}
	
	public void markAdminHeld() {
		status = SeatStatus.ADMIN_HELD;
	}
	
	public void setID(int ID) {
		seatID = ID;
	}
	
	public void setRowLabel(String label) {
		rowLabel = label;
	}
	
	public void setSeatNumber(int num) {
		seatNumber = num;
	}
	
	public void setPrice(Money price) {
		currentPrice = price;
	}
	
	public int getId() {
		return seatID;
	}
	
	public String getRowLabel() {
		return rowLabel;
	}
	
	public int getSeatNumber() {
		return seatNumber;
	}
	
	public SeatStatus getStatus() {
		return status;
	}
	
	public Money getPrice() {
		return currentPrice;
	}
	
}
