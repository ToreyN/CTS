package CTS.gui;

import CTS.event.Event;
import CTS.event.EventDatabase;
import CTS.seating.Seating;
import CTS.seating.SeatingManager;
import CTS.user.User;

import javax.swing.*;
import java.util.List;

public class GUIApp {

    private JFrame frame;
    private User currentUser;

    public void start(User user) {
        this.currentUser = user;

        frame = new JFrame("Concert Ticketing System – Seat Selection");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLocationRelativeTo(null);

        showMainMenu();
        frame.setVisible(true);
    }
    
    public GUIApp(User user) {
        this.currentUser = user;
    }


    public void showMainMenu() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JButton browse = new JButton("Browse Events / Select Seats");
        browse.addActionListener(e -> showEventList());

        panel.add(browse);

        frame.setContentPane(panel);
        frame.revalidate();
    }

    private void showEventList() {
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        List<Event> events = EventDatabase.getAllPublishedEvents();

        for (Event e : events) {
            String label = e.getName() + " @ " + e.getVenueName();
            JButton btn = new JButton(label);

            btn.addActionListener(ev -> showSeating(e));

            list.add(btn);
        }

        frame.setContentPane(list);
        frame.revalidate();
    }

    private void showSeating(Event event) {
    	Seating seating = SeatingManager.loadOrCreate(event);
        SeatingScreen ss = new SeatingScreen(this, event, seating);
        frame.setContentPane(ss);
        frame.revalidate();
    }

    public void beginPurchase(Event event, Seating seating, List<Integer> seatNumbers) {
        new GUIBookingFlow(this).purchaseSeats(event, seating, seatNumbers, currentUser);
    }

}
