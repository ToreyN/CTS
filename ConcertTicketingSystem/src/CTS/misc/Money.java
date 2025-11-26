package CTS.misc;

public class Money {
    private double amount;
    private String currency;

    public Money() {
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
        currency = cur;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    
     // Inline serialization for CSV columns, ex. "50.0:USD".
     
    public String toInlineString() {
        String cur = (currency == null) ? "" : currency.replace(":", "");
        return amount + ":" + cur;
    }

    
    public static Money fromInlineString(String s) {
        if (s == null || s.isEmpty()) {
            return new Money(0.0, "");
        }
        String[] parts = s.split(":", 2);
        double amt = 0.0;
        try {
            amt = Double.parseDouble(parts[0]);
        } catch (NumberFormatException ignored) {
        }
        String cur = parts.length > 1 ? parts[1] : "";
        return new Money(amt, cur);
    }

    @Override
    public String toString() {
        return amount + " " + (currency == null ? "" : currency);
    }
}
