package CTS.booking;
import CTS.misc.*;
import CTS.enums.*;

class SeatHold {
	private int holdID;
	private Date createdAt;
	private Date expiresAt;
	private HoldStatus status;
	
	public boolean isExpired(Date currentTime) {
		
		return true;
	}
	
	public void expire() {
		
	}
	
	public Order convertToOrder() {
		
	}
}
