package view.window;

import javax.swing.*;
import java.awt.*;

public class HouseholdsWindow extends JPanel {

    public HouseholdsWindow() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Quản lý hộ khẩu", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        add(label, BorderLayout.CENTER);
        // Additional components for residents management can be added here
    }
}
