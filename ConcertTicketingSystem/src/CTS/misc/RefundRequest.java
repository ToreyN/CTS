package CTS.misc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import CTS.user.VenueAdmin;
//import CTS.enums.PaymentType;
//import CTS.enums.PaymentStatus;
import CTS.enums.RefundStatus;
//import CTS.enums.OrderStatus;
import CTS.booking.Order;
/**
 * Refund request for an Order.
 * Matches the domain model:
 *  - refundId : int
 *  - reason : String
 *  - createdAt : Date
 *  - processedAt : Date
 *  - status : RefundStatus
 *
 */
public class RefundRequest {

    private int refundId;
    private String reason;
    private Date createdAt;
    private Date processedAt;
    private RefundStatus status;

    
    private Order order;
    private PaymentTransaction refundTxn;
    private VenueAdmin processedBy;
    
    private static int NEXT_ID = 1;

    public static int nextId() {
        return NEXT_ID++;
    }


    public RefundRequest(int refundId, Order order, Date createdAt, String reason, RefundStatus status) {
        this.refundId = refundId;
        this.reason = reason;
        this.createdAt = createdAt;
        this.status = status;
        this.order = order;
    }

    public int getRefundId() {
        return refundId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getProcessedAt() {
        return processedAt;
    }

    public RefundStatus getStatus() {
        return status;
    }

    public Order getOrder() {
        return order;
    }

    public PaymentTransaction getRefundTxn() {
        return refundTxn;
    }

    public void setRefundTxn(PaymentTransaction refundTxn) {
        this.refundTxn = refundTxn;
    }

    public VenueAdmin getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(VenueAdmin processedBy) {
        this.processedBy = processedBy;
    }


    public void approve(VenueAdmin admin) {
        this.status = RefundStatus.APPROVED;
        this.processedAt = new Date();
        this.processedBy = admin;
    }

    public void deny(VenueAdmin admin, String reason) {
        this.status = RefundStatus.DENIED;
        this.processedAt = new Date();
        this.processedBy = admin;
        this.reason = reason; // store denial reason
    }
    
    public void approve(User admin) {
        this.status = RefundStatus.APPROVED;
        this.adminUser = admin;
    }

    public void deny(User admin, String reason) {
        this.status = RefundStatus.DENIED;
        this.adminUser = admin;
        this.reason = reason;
    }


    // ===== CSV SUPPORT =====
    // CSV format:
    // refundId,orderId,reason,createdAtMillis,processedAtMillis,status,adminUserId,refundTxnId

    public String toCsvRow() {
        int orderId = (order != null) ? order.getOrderId() : -1;
        long createdMillis = createdAt != null ? createdAt.getTime() : 0L;
        long processedMillis = processedAt != null ? processedAt.getTime() : 0L;
        int adminId = (processedBy != null) ? processedBy.getUserId() : -1;
        int refundTxnId = (refundTxn != null) ? refundTxn.getPaymentId() : -1;

        return refundId + "," +
                orderId + "," +
                escape(reason) + "," +
                createdMillis + "," +
                processedMillis + "," +
                status.name() + "," +
                adminId + "," +
                refundTxnId;
    }

   
    public static class RawRefundRow {
        public final int refundId;
        public final int orderId;
        public final String reason;
        public final Date createdAt;
        public final Date processedAt;
        public final RefundStatus status;
        public final int adminUserId;
        public final int refundTxnId;

        public RawRefundRow(int refundId,
                            int orderId,
                            String reason,
                            Date createdAt,
                            Date processedAt,
                            RefundStatus status,
                            int adminUserId,
                            int refundTxnId) {
            this.refundId = refundId;
            this.orderId = orderId;
            this.reason = reason;
            this.createdAt = createdAt;
            this.processedAt = processedAt;
            this.status = status;
            this.adminUserId = adminUserId;
            this.refundTxnId = refundTxnId;
        }
    }
    
    public static List<RefundRequest> loadAll(Path path, List<Order> allOrders) throws IOException {
        List<RefundRequest> list = new ArrayList<>();
        List<RawRefundRow> raw = loadRawRows(path);

        for (RawRefundRow r : raw) {

            
            Order order = Order.findById(r.orderId, allOrders);

            RefundRequest rr = new RefundRequest(
                    r.refundId,
                    order,
                    r.createdAt,
                    r.reason,
                    r.status
            );

            // If adminUserId > 0, attach it
            if (r.adminUserId > 0 && order != null) {
                
            }

            list.add(rr);
        }

        return list;
    }
    
    public static void append(Path path, RefundRequest r) throws IOException {
        List<String> lines = new ArrayList<>();

        // If file exists, read current lines
        if (Files.exists(path)) {
            lines.addAll(Files.readAllLines(path));
        } else {
            // Add header if file is new
            lines.add("# refundId,orderId,createdAtMillis,reason,status,adminUserId,refundTxnId");
        }

        long millis = (r.createdAt != null) ? r.createdAt.getTime() : 0L;
        int adminId = (r.adminUser != null) ? r.adminUser.getUserId() : -1;
        int refundTxnId = (r.refundTxn != null) ? r.refundTxn.getPaymentId() : -1;

        String row = r.refundId + "," +
                r.order.getOrderId() + "," +
                millis + "," +
                escape(r.reason) + "," +
                r.status.name() + "," +
                adminId + "," +
                refundTxnId;

        lines.add(row);
        Files.write(path, lines);
    }
    
    public static void saveAll(Path path, List<RefundRequest> list) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# refundId,orderId,createdAtMillis,reason,status,adminUserId,refundTxnId");

        for (RefundRequest r : list) {
            long millis = (r.createdAt != null) ? r.createdAt.getTime() : 0L;
            int adminId = (r.adminUser != null) ? r.adminUser.getUserId() : -1;
            int refundTxnId = (r.refundTxn != null) ? r.refundTxn.getPaymentId() : -1;

            lines.add(
                    r.refundId + "," +
                    r.order.getOrderId() + "," +
                    millis + "," +
                    escape(r.reason) + "," +
                    r.status.name() + "," +
                    adminId + "," +
                    refundTxnId
            );
        }

        Files.write(path, lines);
    }





    public static List<RawRefundRow> loadRawRows(Path path) throws IOException {
        List<RawRefundRow> result = new ArrayList<>();
        if (!Files.exists(path)) {
            return result;
        }
        for (String line : Files.readAllLines(path)) {
            if (line.trim().isEmpty() || line.startsWith("#")) {
                continue;
            }
            String[] parts = line.split(",", 8);
            int refundId = Integer.parseInt(parts[0]);
            if (refundId >= NEXT_ID) {
                NEXT_ID = refundId + 1;
            }
            int orderId = Integer.parseInt(parts[1]);
            String reason = unescape(parts[2]);
            long createdMillis = Long.parseLong(parts[3]);
            long processedMillis = Long.parseLong(parts[4]);
            RefundStatus status = RefundStatus.valueOf(parts[5]);
            int adminUserId = Integer.parseInt(parts[6]);
            int refundTxnId = Integer.parseInt(parts[7]);

            Date createdAt = createdMillis == 0L ? null : new Date(createdMillis);
            Date processedAt = processedMillis == 0L ? null : new Date(processedMillis);

            result.add(new RawRefundRow(
                    refundId, orderId, reason,
                    createdAt, processedAt, status,
                    adminUserId, refundTxnId));
        }
        return result;
    }

    public static void saveToCsv(Path path, List<RefundRequest> requests) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# refundId,orderId,reason,createdAtMillis,processedAtMillis,status,adminUserId,refundTxnId");
        for (RefundRequest r : requests) {
            lines.add(r.toCsvRow());
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
        return "RefundRequest{" +
                "refundId=" + refundId +
                ", reason='" + reason + '\'' +
                ", createdAt=" + createdAt +
                ", processedAt=" + processedAt +
                ", status=" + status +
                '}';
    }

}
