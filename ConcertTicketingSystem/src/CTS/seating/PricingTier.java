package CTS.seating;

import CTS.misc.Money; 

public class PricingTier {

    // --- Attributes matching the UML Diagram ---
    private final int tierId;      // Unique ID
    private String label;          //  "VIP"
    private Money price;           // base price for thetier

    
    
    // --- Constructor 
    
    
     // Creates a new PricingTier object.
    
    public PricingTier(int tierId, String label, Money initialPrice) {
        this.tierId = tierId;
        this.label = label;
        this.price = initialPrice;
    }


    // Updates the base price for this tier.
    public void updatePrice(Money newPrice) {
        // Validate price before updating
        if (newPrice != null && newPrice.getAmount() >= 0) {
            this.price = newPrice;
            System.out.println("PricingTier " + this.label + " price updated to: " + this.price.toString());
        } else {
            System.err.println("Cannot update price: Invalid price provided.");
        }
    }


    // --- Getters & Setters 
    
    public int getTierId() {
        return tierId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Money getPrice() {
        return price;
    }
    
    // Setter for price is updatePrice
    
    @Override
    public String toString() {
        return String.format("[%d] %s: %s", tierId, label, price.toString());
    }
}