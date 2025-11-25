package CTS.booking;
import CTS.misc.Money;

public class Ticket {
    private int ticketId;
    private Money pricePaid;

    public Ticket() {
    }

    public Ticket(int ticketId, Money pricePaid) {
        this.ticketId = ticketId;
        this.pricePaid = pricePaid;
    }

    public void setId(int ID) {
        ticketId = ID;
    }

    public void setPrice(Money pricePaid) {
        this.pricePaid = pricePaid;
    }

    public int getId() {
        return ticketId;
    }

    public Money getPrice() {
        return pricePaid;
    }

    public String getSeatLabel() {
        return "TBD"; // placeholder until seating is implemented
    }
}
