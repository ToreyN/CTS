package CTS.misc;

public class Money {
	private double amount;
	private String currency;
	
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
	
}
