package CTS.gui;

import CTS.event.Event;
import CTS.enums.EventStatus;
import CTS.seating.Seating;
import CTS.seating.SeatingManager;
import CTS.user.User;
import CTS.user.userDatabase;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class MainMenuGUI extends JFrame {

    private final userDatabase userDb;
    private User currentUser;

    public MainMenuGUI(userDatabase userDb) {
        this.userDb = userDb;

        setTitle("Concert Ticketing System - Main Menu");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setupMenu();
    }

    private void setupMenu() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 10, 10));

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        JButton browseBtn = new JButton("Browse Events");
        JButton exitBtn = new JButton("Exit");

        loginBtn.addActionListener(e -> showLogin());
        registerBtn.addActionListener(e -> showRegister());
        browseBtn.addActionListener(e -> showEvents());
        exitBtn.addActionListener(e -> System.exit(0));

        panel.add(loginBtn);
        panel.add(registerBtn);
        panel.add(browseBtn);
        panel.add(exitBtn);

        add(panel);
    }

    /* ----------------------- LOGIN ------------------------- */
    private void showLogin() {
        String email = JOptionPane.showInputDialog(this, "Email:");
        if (email == null) return;

        String pw = JOptionPane.showInputDialog(this, "Password:");
        if (pw == null) return;

        User user = userDb.login(email, pw);

        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid credentials.");
        } else {
            this.currentUser = user;
            JOptionPane.showMessageDialog(this, "Welcome, " + user.getName());
            dispose();  // Close main menu
            new DashboardTabbedGUI(user);
        }
    }

    /* ----------------------- REGISTER ------------------------- */
    private void showRegister() {
        String name = JOptionPane.showInputDialog(this, "Name:");
        if (name == null) return;

        String email = JOptionPane.showInputDialog(this, "Email:");
        if (email == null) return;

        String pw = JOptionPane.showInputDialog(this, "Password:");
        if (pw == null) return;

        String[] options = { "Concert Goer", "Venue Admin" };
        int roleChoice = JOptionPane.showOptionDialog(
                this,
                "Role:",
                "Choose Role",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        String role = (roleChoice == 1) ? "ADMIN" : "USER";

        User user = userDb.registerUser(name, email, pw, role);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Email already in use.");
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Account created!\nWelcome, " + user.getName());

        currentUser = user;
    }

    /* ----------------------- EVENTS ------------------------- */
    private void showEvents() {
        List<Event> events = Event.getAllPublishedEvents();

        if (events.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No events available.");
            return;
        }

        String[] names = events.stream()
                .map(Event::getName)
                .toArray(String[]::new);

        String chosen = (String) JOptionPane.showInputDialog(
                this,
                "Choose an event:",
                "Events",
                JOptionPane.PLAIN_MESSAGE,
                null,
                names,
                names[0]
        );

        if (chosen == null) return;

        Event selected = events.stream()
                .filter(e -> e.getName().equals(chosen))
                .findFirst()
                .orElse(null);

        if (selected == null) return;

        openEvent(selected);
    }

    /* ----------------------- SEATING ------------------------- */
    private void openEvent(Event event) {
        Seating seating = SeatingManager.loadOrCreate(event);

        GUIApp app = new GUIApp(currentUser);

        SeatingScreen screen = new SeatingScreen(
            app,
            event,
            seating
        );

        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Please login first.");
            return;
        }

        JFrame frame = new JFrame("Seating for " + event.getName());
        frame.setContentPane(screen);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
