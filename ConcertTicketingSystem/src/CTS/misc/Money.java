package CTS.misc;

import java.util.Objects;

public class Money {
    private double amount;
    private String currency;

    public Money() {
        // Safe initialization for no-arg constructor
        this.amount = 0.0; 
        this.currency = "USD"; 
    }

    public Money(double amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String cur) {
        this.currency = cur;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    
     // Inline serialization for CSV columns,  "50.0:USD"
     
    public String toInlineString() {
        // Use Double.toString for locale-safe amount output!
        String amountStr = Double.toString(amount);
        
        // Clean the currency string of separators
        String cur = (currency == null) ? "" : currency.replace(":", "").replace(",", ""); 
        
        return amountStr + ":" + cur;
    }

    
    public static Money fromInlineString(String s) {
        if (s == null || s.isEmpty()) {
            return new Money(0.0, "");
        }
        String[] parts = s.split(":", 2);
        double amt = 0.0;
        try {
            if (parts.length > 0) {
                amt = Double.parseDouble(parts[0]);
            }
        } catch (NumberFormatException ignored) {
            
        }
        String cur = parts.length > 1 ? parts[1] : "";
        return new Money(amt, cur);
    }

    
    
    @Override  // need to override because Object class uses equals()
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Money money = (Money) o;
        
        // Use Double.compare for safe comparison of floating-point numbers
        boolean amountMatches = Double.compare(money.amount, amount) == 0;
        // Use Objects.equals for safe comparison of Strings 
        boolean currencyMatches = Objects.equals(currency, money.currency);
        
        return amountMatches && currencyMatches;
    }

    @Override   // need to override because Object class uses hashCode()
    public int hashCode() {
        // Generates a hash code based on the two core fields
        return Objects.hash(amount, currency);
    }
    
    // ===============================================

    @Override
    public String toString() {
        return amount + " " + (currency == null ? "" : currency);
    }
}