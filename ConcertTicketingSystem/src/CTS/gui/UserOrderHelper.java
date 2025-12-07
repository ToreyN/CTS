package CTS.gui;

import CTS.booking.Order;
import CTS.user.User;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class UserOrderHelper {

    public static List<Order> getOrdersFor(User user) {

        List<Order> all = new ArrayList<>();
        try {
            all = Order.loadFromCsv(Paths.get("orders.csv"));
        } catch (Exception ignored) {}

        List<Order> mine = new ArrayList<>();

        for (Order o : all) {
            if (o.getUserId() == user.getUserId()) {
                mine.add(o);
            }
        }

        return mine;
    }
    
    public static List<Order> getAllOrders() {
        try {
        	return Order.loadFromCsv(Paths.get("orders.csv"));
        } catch (Exception e) {
            return java.util.List.of();
        }
    }

}
