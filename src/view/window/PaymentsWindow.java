package view.window;

import javax.swing.*;
import java.awt.*;

public class PaymentsWindow extends JPanel {

    public PaymentsWindow() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Quản lý thu", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        add(label, BorderLayout.CENTER);
        // Additional components for residents management can be added here
    }
}
