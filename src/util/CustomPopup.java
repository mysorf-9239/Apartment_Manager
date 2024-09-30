package util;

import controller.DatabaseConnected;
import view.window.ResidentsWindow;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class CustomPopup extends JDialog {
    public ResidentsWindow residentsWindow;

    private JTextField nameField;
    private JTextField birthDateField;
    private JTextField genderField;
    private JTextField idCardField;

    // Các hằng số để xác định loại popup
    public static final int ADD_RESIDENT = 1;
    public static final int EDIT_RESIDENT = 2;
    public static final int ADD_HOUSEHOLD = 3;

    public CustomPopup(JFrame parent, int popupName, ResidentsWindow residentsWindow) {
        super(parent, "Popup", true);
        setUndecorated(true);
        setSize((int)(parent.getWidth() * 0.4), (int)(parent.getHeight() * 0.8));
        setLocationRelativeTo(parent);

        this.residentsWindow = residentsWindow;

        // Tạo panel cho popup
        JPanel popupPanel = new JPanel();
        popupPanel.setLayout(null);
        popupPanel.setBackground(Color.WHITE);
        popupPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Xây dựng nội dung dựa vào popupName
        switch (popupName) {
            case ADD_RESIDENT:
                addResidentContent(popupPanel);
                break;
            case EDIT_RESIDENT:
                editResidentContent(popupPanel);
                break;
            case ADD_HOUSEHOLD:
                addHouseholdContent(popupPanel);
                break;
            default:
                throw new IllegalArgumentException("Invalid popup type");
        }

        // Thêm panel vào dialog
        setContentPane(popupPanel);
    }

    private void addResidentContent(JPanel panel) {
        panel.setLayout(null);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(null);
        headerPanel.setBounds(0, 0, getWidth(), getHeight() / 10);
        headerPanel.setBackground(Color.LIGHT_GRAY);

        JLabel titleLabel = new JLabel("Thêm Nhân Khẩu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(0, 0, headerPanel.getWidth(), headerPanel.getHeight());
        headerPanel.add(titleLabel);
        panel.add(headerPanel);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null);
        contentPanel.setBounds(0, headerPanel.getHeight(), getWidth(), getHeight() * 4 / 5);
        contentPanel.setBackground(Color.WHITE);

        // Họ và tên
        JLabel nameLabel = new JLabel("Họ và tên:");
        nameLabel.setBounds(20, 50, getWidth() / 4, 40);
        contentPanel.add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(10 + getWidth() / 4 + 5, 50, 300, 40);
        contentPanel.add(nameField);

        // Ngày sinh
        JLabel birthDateLabel = new JLabel("Ngày sinh:");
        birthDateLabel.setBounds(20, 100, getWidth() / 4, 40);
        contentPanel.add(birthDateLabel);

        birthDateField = new JTextField();
        birthDateField.setBounds(10 + getWidth() / 4 + 5, 100, 300, 40);
        contentPanel.add(birthDateField);

        // Giới tính
        JLabel genderLabel = new JLabel("Giới tính:");
        genderLabel.setBounds(20, 150, getWidth() / 4, 40);
        contentPanel.add(genderLabel);

        genderField = new JTextField();
        genderField.setBounds(10 + getWidth() / 4 + 5, 150, 300, 40);
        contentPanel.add(genderField);

        // CCCD
        JLabel idCardLabel = new JLabel("CCCD:");
        idCardLabel.setBounds(20, 200, getWidth() / 4, 40);
        contentPanel.add(idCardLabel);

        idCardField = new JTextField();
        idCardField.setBounds(10 + getWidth() / 4 + 5, 200, 300, 40);
        contentPanel.add(idCardField);

        panel.add(contentPanel);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new GridLayout(1, 2));
        footerPanel.setBounds(0, headerPanel.getHeight() + contentPanel.getHeight(), getWidth(), getHeight() / 10);
        footerPanel.setBackground(Color.LIGHT_GRAY);

        // Nút Lưu
        JButton continueButton = new JButton("Continue");
        continueButton.setPreferredSize(new Dimension(60, 40));
        continueButton.addActionListener(e -> {
            saveAddResident();
            dispose();
        });
        footerPanel.add(continueButton);

        // Nút Hủy
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(60, 40));
        cancelButton.addActionListener(e -> dispose());
        footerPanel.add(cancelButton);

        panel.add(footerPanel);
    }

    private void saveAddResident() {
        // Lấy giá trị từ các trường nhập liệu
        String name = nameField.getText();
        String birthDate = birthDateField.getText();
        String gender = genderField.getText();
        String idCard = idCardField.getText();

        // Kiểm tra hợp lệ (có thể tùy chỉnh theo yêu cầu của bạn)
        if (name.isEmpty() || birthDate.isEmpty() || gender.isEmpty() || idCard.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng điền đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Lưu vào cơ sở dữ liệu
        DatabaseConnected.addResident(name, birthDate, gender, idCard);

        // Cập nhật danh sách cư dân
        Object[] newResident = new Object[4];
        newResident[0] = String.format("%02d", residentsWindow.data.size() + 1); // STT mới
        newResident[1] = name + "  -  " + birthDate;
        residentsWindow.data.add(newResident);

        // Cập nhật bảng và phân trang
        residentsWindow.updateTable();
    }




    private void editResidentContent(JPanel panel) {
        // Tương tự như addResidentContent, nhưng với các trường khác nếu cần
        // Ví dụ: có thể chỉ hiển thị các trường đã có dữ liệu để chỉnh sửa
        addResidentContent(panel); // Giả định rằng cách nhập liệu giống nhau
    }

    private void addHouseholdContent(JPanel panel) {
        // Thêm các trường nhập liệu cho hộ khẩu ở đây
        // Ví dụ đơn giản:
        JLabel householdLabel = new JLabel("Hộ Khẩu:");
        householdLabel.setBounds(20, 20, 80, 30);
        panel.add(householdLabel);

        JTextField householdField = new JTextField();
        householdField.setBounds(100, 20, 250, 50);
        panel.add(householdField);

        // Nút lưu và hủy
        JButton saveButton = new JButton("Lưu");
        saveButton.setBounds(100, 150, 100, 50);
        saveButton.addActionListener(e -> {
            // Lưu dữ liệu hộ khẩu
            dispose();
        });
        panel.add(saveButton);

        JButton cancelButton = new JButton("Hủy");
        cancelButton.setBounds(220, 150, 100, 50);
        cancelButton.addActionListener(e -> dispose());
        panel.add(cancelButton);
    }

    public void showWithBackgroundDim() {
        JPanel dimBackground = new JPanel();
        dimBackground.setBackground(new Color(0, 0, 0, 0.5f));
        dimBackground.setBounds(0, 0, getParent().getWidth(), getParent().getHeight());

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(getParent().getWidth(), getParent().getHeight()));
        layeredPane.add(dimBackground, Integer.valueOf(0));
        layeredPane.add(this.getContentPane(), Integer.valueOf(1));

        setContentPane(layeredPane);
        setVisible(true);
    }

}
