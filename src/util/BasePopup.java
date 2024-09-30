package util;

import javax.swing.*;
import java.awt.*;

public abstract class BasePopup extends JDialog {
    protected JTextField nameField;
    protected JTextField birthDateField;
    protected JTextField genderField;
    protected JTextField idCardField;

    public BasePopup(JFrame parent, String title) {
        super(parent, title, true);
        setUndecorated(true);
        setSize((int) (parent.getWidth() * 0.4), (int) (parent.getHeight() * 0.8));
        setLocationRelativeTo(parent);

        // Tạo panel cho popup
        JPanel popupPanel = new JPanel();
        popupPanel.setLayout(null);
        popupPanel.setBackground(Color.WHITE);
        popupPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(popupPanel);

        // Xây dựng nội dung cụ thể
        buildContent(popupPanel);
    }

    protected abstract void buildContent(JPanel panel);

    protected void createFooter(JPanel panel) {
        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new GridLayout(1, 2));
        footerPanel.setBackground(Color.LIGHT_GRAY);

        // Nút Lưu
        JButton continueButton = new JButton("Continue");
        continueButton.setPreferredSize(new Dimension(60, 40));
        continueButton.addActionListener(e -> {
            if (validateFields()) {
                save();
                dispose();
            }
        });
        footerPanel.add(continueButton);

        // Nút Hủy
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(60, 40));
        cancelButton.addActionListener(e -> dispose());
        footerPanel.add(cancelButton);

        panel.add(footerPanel);
    }

    protected boolean validateFields() {
        // Kiểm tra hợp lệ (có thể tùy chỉnh theo yêu cầu của bạn)
        if (nameField.getText().isEmpty() || birthDateField.getText().isEmpty() ||
                genderField.getText().isEmpty() || idCardField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng điền đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    protected abstract void save();
}
