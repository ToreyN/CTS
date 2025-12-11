package CTS.gui;

import CTS.event.Event;
import CTS.seating.*;
import CTS.enums.SeatStatus;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SeatingScreen extends BaseScreen {

    private final Event event;
    private final Seating seating;

    public SeatingScreen(GUIApp app, Event event, Seating seating) {
        super(app);
        this.event = event;
        this.seating = seating;

        add(topBar(event.getName()), BorderLayout.NORTH);
        add(buildGrid(), BorderLayout.CENTER);
        add(buildActions(), BorderLayout.SOUTH);
    }
    private JPanel buildGrid() {
        JPanel grid = new JPanel();

        int cols = 10;
        int total = seating.getSeats().size();
        int rows = (int) Math.ceil(total / (double) cols);

        grid.setLayout(new GridLayout(rows, cols, 5, 5));

        for (Seat seat : seating.getSeats()) {
            JButton b = new JButton("" + seat.getSeatNumber());
            b.setOpaque(true);
            b.setBorderPainted(false);

            refreshColor(b, seat);

            
            b.addActionListener(e -> {
                switch (seat.getStatus()) {

                    case AVAILABLE -> {
                        seat.markHeld();
                        refreshColor(b, seat);
                    }

                    case HELD -> {
                        seat.markAvailable();
                        refreshColor(b, seat);
                    }

                    case ADMIN_HELD, SOLD -> {
                        // Do nothing, these seats are locked
                    }
                }
            });

            grid.add(b);
        }

        return grid;
    }

    private void refreshColor(JButton b, Seat seat) {
        switch (seat.getStatus()) {
            case AVAILABLE -> b.setBackground(Color.GREEN);
            case HELD -> b.setBackground(Color.YELLOW);
            case SOLD -> {
                b.setBackground(Color.RED);
                b.setEnabled(false);
            }
            case ADMIN_HELD -> {
                b.setBackground(new Color(80, 80, 255));
                b.setEnabled(false);
            }
        }
    }

    private JPanel buildActions() {
        JPanel p = new JPanel();
        JButton purchase = new JButton("Purchase Selected Seats");

        purchase.addActionListener(e -> {
            List<Integer> nums = new ArrayList<>();
            for (Seat s : seating.getHeldSeats()) {
                nums.add(s.getSeatNumber());
            }
            if (nums.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No seats selected.");
                return;
            }

            app.beginPurchase(event, seating, nums);
        });

        p.add(purchase);
        return p;
    }
}
