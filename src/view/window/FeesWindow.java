package view.window;

import javax.swing.*;
import java.awt.*;

public class FeesWindow extends JPanel {

    public FeesWindow() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Quản lý phí", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        add(label, BorderLayout.CENTER);
        // Additional components for residents management can be added here
    }
}
