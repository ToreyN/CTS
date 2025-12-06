package CTS.gui;

import javax.swing.*;
import java.awt.*;

public abstract class BaseScreen extends JPanel {

    protected final GUIApp app;

    public BaseScreen(GUIApp app) {
        this.app = app;
        setLayout(new BorderLayout());
    }

    protected JPanel topBar(String title) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(30, 30, 30));

        JLabel label = new JLabel("  " + title);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 22));

        JButton back = new JButton("Back");
        back.addActionListener(e -> app.showMainMenu());

        bar.add(label, BorderLayout.WEST);
        bar.add(back, BorderLayout.EAST);

        return bar;
    }
}
