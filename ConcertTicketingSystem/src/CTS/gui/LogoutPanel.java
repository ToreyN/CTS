package CTS.gui;

import CTS.user.userDatabase;

import javax.swing.*;
import java.awt.*;

public class LogoutPanel extends JPanel {

    public LogoutPanel(JFrame parent) {

        setLayout(new BorderLayout());

        JButton logoutBtn = new JButton("Logout");

        logoutBtn.addActionListener(e -> {
            parent.dispose();
            new MainMenuGUI(new userDatabase()).setVisible(true);
        });

        add(new JLabel("Click below to logout:", SwingConstants.CENTER), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.add(logoutBtn);
        add(bottom, BorderLayout.SOUTH);
    }
}
