package CTS.misc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RefundRequest {
    private int refundId;
    private String reason;
    private Date createdAt;
    private Date processedAt;
    private RefundStatus status;

    
    private Order order;
    private PaymentTransaction refundTxn;
    private VenueAdmin processedBy;

    public RefundRequest(int refundId,
                         String reason,
                         Date createdAt,
                         RefundStatus status,
                         Order order) {
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

    public VenueAdmin getProcessedBy() {
        return processedBy;
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
        this.reason = reason;
    }

    // ===== CSV support =====
    // Format:
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

        public RawRefundRow(int refundId, int orderId, String reason,
                            Date createdAt, Date processedAt,
                            RefundStatus status, int adminUserId, int refundTxnId) {
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

    public static List<RawRefundRow> loadRawRows(Path path) throws IOException {
        List<RawRefundRow> result = new ArrayList<>();
        if (!Files.exists(path)) {
            return result;
        }
        for (String line : Files.readAllLines(path)) {
            if (line.trim().isEmpty() || line.startsWith("#")) continue;
            String[] parts = line.split(",", 8);
            int refundId = Integer.parseInt(parts[0]);
            int orderId = Integer.parseInt(parts[1]);
            String reason = unescape(parts[2]);
            long createdMillis = Long.parseLong(parts[3]);
            long processedMillis = Long.parseLong(parts[4]);
            RefundStatus status = RefundStatus.valueOf(parts[5]);
            int adminUserId = Integer.parseInt(parts[6]);
            int refundTxnId = Integer.parseInt(parts[7]);

            Date createdAt = createdMillis == 0L ? null : new Date(createdMillis);
            Date processedAt = processedMillis == 0L ? null : new Date(processedMillis);

            result.add(new RawRefundRow(refundId, orderId, reason,
                    createdAt, processedAt, status, adminUserId, refundTxnId));
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
