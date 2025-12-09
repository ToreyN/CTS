package CTS.gui;

import CTS.misc.RefundRequest;
import CTS.booking.Order;

import java.nio.file.Paths;
import java.util.List;

public class RefundLookupHelper {

    public static RefundRequest findRefundForOrder(Order order) {
        try {
            List<RefundRequest> list = RefundRequest.loadAll(
                    Paths.get("refunds.csv"),
                    UserOrderHelper.getAllOrders()

            );

            for (RefundRequest r : list) {
                if (r.getOrder().getOrderId() == order.getOrderId()) {
                    return r;
                }
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }
}
