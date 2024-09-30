package util;

import javax.swing.*;
import java.awt.*;

public class CustomPopup extends JDialog {
    private JTextField nameField;
    private JTextField ageField;

    public CustomPopup(JFrame parent) {
        super(parent, "Thêm Nhân Khẩu", true);
        setUndecorated(false); // Bỏ viền của dialog
        setSize((int)(parent.getWidth() * 0.33), (int)(parent.getHeight() * 0.7)); // Chiếm 1/3 chiều rộng và 70% chiều cao
        setLocationRelativeTo(parent); // Center the dialog vertically

    }

    public void showWithBackgroundDim() {
        // Tạo layer mờ cho nền
        JPanel dimBackground = new JPanel();
        dimBackground.setBackground(new Color(0, 0, 0, 0.5f)); // Mờ đen
        dimBackground.setBounds(0, 0, getParent().getWidth(), getParent().getHeight());

        // Tạo layered pane
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(getParent().getWidth(), getParent().getHeight()));
        layeredPane.add(dimBackground, Integer.valueOf(0));
        layeredPane.add(this.getContentPane(), Integer.valueOf(1));

        // Hiển thị dialog
        setContentPane(layeredPane);
        setVisible(true);
    }

    private void saveData() {
        String name = nameField.getText();
        String age = ageField.getText();
        // Xử lý lưu dữ liệu (gọi hàm lưu vào database hoặc vào ArrayList)
        System.out.println("Tên: " + name + ", Tuổi: " + age);
    }
}
