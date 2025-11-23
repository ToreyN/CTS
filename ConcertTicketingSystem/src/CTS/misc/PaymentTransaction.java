package CTS.misc;

public class PaymentTransaction {
    private int id;
    private Order order;
    private double amount;
    private PaymentStatus status;
    private String providerReference;  // fake transaction id from gateway
    private String failureReason;      // if FAILED
    private String createdAt;          // time stamp

    public PaymentTransaction(int id, Order order, double amount, String createdAt) {
        this.id = id;
        this.order = order;
        this.amount = amount;
        this.createdAt = createdAt;
        this.status = PaymentStatus.INITIATED;
    }

    public int getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getProviderReference() {
        return providerReference;
    }

    public void setProviderReference(String providerReference) {
        this.providerReference = providerReference;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "PaymentTransaction{" +
                "id=" + id +
                ", orderId=" + order.getId() +
                ", amount=" + amount +
                ", status=" + status +
                ", providerReference='" + providerReference + '\'' +
                ", failureReason='" + failureReason + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }

}
