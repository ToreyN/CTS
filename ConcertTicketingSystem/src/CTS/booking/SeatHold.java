package CTS.booking;

import java.util.Date;

import CTS.enums.HoldStatus;

class SeatHold {
    private int holdID;
    private Date createdAt;
    private Date expiresAt;
    private HoldStatus status;

    public SeatHold(int holdID, Date createdAt, Date expiresAt) {
        this.holdID = holdID;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.status = HoldStatus.ACTIVE;
    }

    public boolean isExpired(Date currentTime) {
        if (expiresAt == null || currentTime == null) {
            return false;
        }
        return currentTime.after(expiresAt);
    }

    public void expire() {
        this.status = HoldStatus.EXPIRED;
    }

    public Order convertToOrder() {
        this.status = HoldStatus.CONVERTED;
        // For now, just creates a bare Order. In a fuller design we would attach seats, user, etc.
        return new Order();
    }
}
