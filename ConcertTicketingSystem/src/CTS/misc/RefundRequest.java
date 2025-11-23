package CTS.misc;

public class RefundRequest {
    private int id;
    private Order order;
    private String reason;
    private RefundStatus status = RefundStatus.PENDING;

    public RefundRequest(int id, Order order, String reason) {
        this.id = id;
        this.order = order;
        this.reason = reason;
    }

    public int getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public String getReason() {
        return reason;
    }

    public RefundStatus getStatus() {
        return status;
    }

    public void setStatus(RefundStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "RefundRequest{" +
                "id=" + id +
                ", orderId=" + order.getId() +
                ", user=" + order.getUser().getName() +
                ", status=" + status +
                ", reason='" + reason + '\'' +
                '}';
    }

}
