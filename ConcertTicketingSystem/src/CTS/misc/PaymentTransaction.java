package CTS.misc;

import CTS.enums.PaymentType;
import CTS.enums.PaymentStatus;
//import CTS.enums.RefundStatus;
import CTS.booking.Order;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//import CTS.enums.OrderStatus;
/**
 * Payment transaction for an Order.
 * Matches the domain model:
 *  - paymentId : int
 *  - gatewayRef : String
 *  - type : PaymentType
 *  - amount : Money
 *  - timestamp : Date
 *  - status : PaymentStatus
 *
 */
public class PaymentTransaction {

    private int paymentId;
    
    private static int NEXT_ID = 1;

    public static int nextId() {
        return NEXT_ID++;
    }

    private String gatewayRef;
    private PaymentType type;
    private Money amount;
    private Date timestamp;
    private PaymentStatus status;
    
    

    
    private Order order;

    public PaymentTransaction(int paymentId,
                              String gatewayRef,
                              PaymentType type,
                              Money amount,
                              Date timestamp,
                              PaymentStatus status,
                              Order order) {
        this.paymentId = paymentId;
        this.gatewayRef = gatewayRef;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = status;
        this.order = order;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public String getGatewayRef() {
        return gatewayRef;
    }

    public void setGatewayRef(String gatewayRef) {
        this.gatewayRef = gatewayRef;
    }

    public PaymentType getType() {
        return type;
    }

    public void setType(PaymentType type) {
        this.type = type;
    }

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }


    public void markSuccess() {
        this.status = PaymentStatus.SUCCESS;
    }

    public void markFailed(String reason) {
        this.status = PaymentStatus.FAILED;
    }
    // ===== CSV SUPPORT =====
    // CSV format:
    // paymentId,orderId,gatewayRef,type,amountInline,timestampMillis,status

    public String toCsvRow() {
        long millis = timestamp != null ? timestamp.getTime() : 0L;
        int orderId = (order != null) ? order.getOrderId() : -1;
        return paymentId + "," +
                orderId + "," +
                escape(gatewayRef) + "," +
                type.name() + "," +
                amount.toInlineString() + "," +
                millis + "," +
                status.name();
    }

    
    public static class RawPaymentRow {
        public final int paymentId;
        public final int orderId;
        public final String gatewayRef;
        public final PaymentType type;
        public final Money amount;
        public final Date timestamp;
        public final PaymentStatus status;

        public RawPaymentRow(int paymentId,
                             int orderId,
                             String gatewayRef,
                             PaymentType type,
                             Money amount,
                             Date timestamp,
                             PaymentStatus status) {
            this.paymentId = paymentId;
            this.orderId = orderId;
            this.gatewayRef = gatewayRef;
            this.type = type;
            this.amount = amount;
            this.timestamp = timestamp;
            this.status = status;
        }
    }

    public static List<RawPaymentRow> loadRawRows(Path path) throws IOException {
        List<RawPaymentRow> result = new ArrayList<>();
        if (!Files.exists(path)) {
            return result;
        }
        for (String line : Files.readAllLines(path)) {
            if (line.trim().isEmpty() || line.startsWith("#")) {
                continue;
            }
            String[] parts = line.split(",", 7);
            int paymentId = Integer.parseInt(parts[0]);
            int orderId = Integer.parseInt(parts[1]);
            String gatewayRef = unescape(parts[2]);
            PaymentType type = PaymentType.valueOf(parts[3]);
            Money amount = Money.fromInlineString(parts[4]);
            long millis = Long.parseLong(parts[5]);
            Date timestamp = millis == 0L ? null : new Date(millis);
            PaymentStatus status = PaymentStatus.valueOf(parts[6]);

            // Create row object
            RawPaymentRow row = new RawPaymentRow(
                    paymentId, orderId, gatewayRef, type, amount, timestamp, status);

            result.add(row);

            // ⭐ UPDATE NEXT_ID HERE
            if (paymentId >= NEXT_ID) {
                NEXT_ID = paymentId + 1;
            }
        }
        return result;
    }

    public static void saveToCsv(Path path, List<PaymentTransaction> txns) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# paymentId,orderId,gatewayRef,type,amountInline,timestampMillis,status");
        for (PaymentTransaction t : txns) {
            lines.add(t.toCsvRow());
        }
        Files.write(path, lines);
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace(",", "\\,");
    }

    private static String unescape(String s) {
        return s.replace("\\,", ",").replace("\\\\", "\\");
    }

    @Override
    public String toString() {
        return "PaymentTransaction{" +
                "paymentId=" + paymentId +
                ", gatewayRef='" + gatewayRef + '\'' +
                ", type=" + type +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", status=" + status +
                '}';
    }

}
