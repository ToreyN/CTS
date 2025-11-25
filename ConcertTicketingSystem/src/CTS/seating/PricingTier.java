package CTS.seating;
import CTS.misc.*;

public class PricingTier {
	
	private int tierID;
	private String label;
	private Money price;
	
	public void updatePrice(Money newPrice) {
		price = newPrice;
	}
	
	public void setTierID(int tierID) {
		this.tierID = tierID;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public int getID() {
		return tierID;
	}
	
	public String getLabel() {
		return label;
	}
	
	public Money getPrice() {
		return price;
	}
}
