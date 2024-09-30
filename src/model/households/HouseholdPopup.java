package model.households;

import controller.DatabaseConnected;
import model.Resident;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class HouseholdPopup extends JDialog {
    public HouseholdsWindow householdsWindow;

    private JTextField addressField;
    private JTextField nameField;
    private JTextField CCCDField;

    public static final int ADD_HOUSEHOLD = 1;
    public static final int EDIT_HOUSEHOLD = 2;

    public HouseholdPopup(JFrame parent, int popupType, HouseholdsWindow householdsWindow, int editIndex) {
        super(parent, "Popup", true);
        setUndecorated(true);
        setSize((int)(parent.getWidth() * 0.4), (int)(parent.getHeight() * 0.8));
        setLocationRelativeTo(parent);

        this.householdsWindow = householdsWindow;

        JPanel popupPanel = new JPanel();
        popupPanel.setLayout(null);
        popupPanel.setBackground(Color.WHITE);
        popupPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        switch (popupType) {
            case ADD_HOUSEHOLD:
                addHouseholdContent(popupPanel);
                break;
            case EDIT_HOUSEHOLD:
                editHouseholdContent(popupPanel, editIndex);
                break;
            default:
                throw new IllegalArgumentException("Invalid popup type");
        }

        setContentPane(popupPanel);
    }

    private void addHouseholdContent(JPanel panel) {
        panel.setLayout(null);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(null);
        headerPanel.setBounds(0, 0, getWidth(), getHeight() / 10);
        headerPanel.setBackground(Color.LIGHT_GRAY);

        JLabel titleLabel = new JLabel("Thêm Hộ Khẩu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(0, 0, getWidth(), headerPanel.getHeight());
        headerPanel.add(titleLabel);
        panel.add(headerPanel);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null);
        contentPanel.setBounds(0, headerPanel.getHeight(), getWidth(), getHeight() * 4 / 5);
        contentPanel.setBackground(Color.WHITE);

        // Địa chỉ
        JLabel addressLabel = new JLabel("Địa chỉ:");
        addressLabel.setBounds(20, 50, getWidth() / 4, 40);
        contentPanel.add(addressLabel);

        addressField = new JTextField();
        addressField.setBounds(20 + getWidth() / 4, 50, 300, 40);
        contentPanel.add(addressField);

        // Gợi ý cho Địa chỉ
        JLabel addressHintLabel = new JLabel("Địa chỉ cụ thể");
        addressHintLabel.setBounds(25 + getWidth() / 4, 90, 300, 20);
        addressHintLabel.setForeground(Color.GRAY);
        contentPanel.add(addressHintLabel);

        // Chủ hộ
        JLabel headOfHouseholdLabel = new JLabel("Chủ hộ:");
        headOfHouseholdLabel.setBounds(20, 110, getWidth() / 4, 40);
        contentPanel.add(headOfHouseholdLabel);

        // Tên chủ hộ
        JLabel nameLabel = new JLabel("Họ và tên:");
        nameLabel.setBounds(40, 150, getWidth() / 4, 40);
        contentPanel.add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(20 + getWidth() / 4, 150, 300, 40);
        contentPanel.add(nameField);

        // Gợi ý cho Họ và tên
        JLabel nameHintLabel = new JLabel("Họ và tên đầy đủ");
        nameHintLabel.setBounds(25 + getWidth() / 4, 190, 300, 20);
        nameHintLabel.setForeground(Color.GRAY);
        contentPanel.add(nameHintLabel);

        // CCCD
        JLabel idCardLabel = new JLabel("CCCD:");
        idCardLabel.setBounds(40, 230, getWidth() / 4, 40);
        contentPanel.add(idCardLabel);

        CCCDField = new JTextField();
        CCCDField.setBounds(20 + getWidth() / 4, 230, 300, 40);
        contentPanel.add(CCCDField);

        // Gợi ý cho CCCD
        JLabel CCCDHintLabel = new JLabel("Số căn cước công dân");
        CCCDHintLabel.setBounds(25 + getWidth() / 4, 270, 300, 20);
        CCCDHintLabel.setForeground(Color.GRAY);
        contentPanel.add(CCCDHintLabel);

        panel.add(contentPanel);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new GridLayout(1, 2));
        footerPanel.setBounds(0, headerPanel.getHeight() + contentPanel.getHeight(), getWidth(), getHeight() / 10);
        footerPanel.setBackground(Color.LIGHT_GRAY);

        // Nút Lưu
        JButton continueButton = new JButton("Lưu");
        continueButton.setPreferredSize(new Dimension(60, 40));
        continueButton.addActionListener(e -> {
            saveAddHousehold();
            dispose();
        });
        footerPanel.add(continueButton);

        // Nút Hủy
        JButton cancelButton = new JButton("Hủy");
        cancelButton.setPreferredSize(new Dimension(60, 40));
        cancelButton.addActionListener(e -> dispose());
        footerPanel.add(cancelButton);

        panel.add(footerPanel);
    }


    private void saveAddHousehold() {
        // Lấy giá trị từ các trường nhập liệu
        String address = addressField.getText();
        String fullName = nameField.getText();
        String idCard = CCCDField.getText();

        if (address.isEmpty() || fullName.isEmpty() || idCard.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng điền đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Bước 1: Tìm thông tin của chủ hộ
            Resident headOfHousehold = DatabaseConnected.getHeadOfHouseholdInfo(fullName, idCard);
            if (headOfHousehold == null) {
                JOptionPane.showMessageDialog(null, "Không tìm thấy thông tin chủ hộ trong hệ thống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Bước 2: Thêm hộ khẩu mới vào bảng households và lấy household_id
            int householdId = DatabaseConnected.addHousehold(address, headOfHousehold.id);
            if (householdId == -1) {
                JOptionPane.showMessageDialog(null, "Có lỗi xảy ra khi thêm hộ khẩu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Hiển thị thông báo thêm thành công
            JOptionPane.showMessageDialog(null, "Thêm hộ khẩu thành công!");

            // Bước 3: Cập nhật danh sách hộ khẩu trên giao diện
            Object[] newHousehold = new Object[4];
            newHousehold[0] = String.format("%02d", householdsWindow.data.size() + 1);
            newHousehold[1] = householdId;  // Lưu household_id
            newHousehold[2] = address;
            newHousehold[3] = headOfHousehold; // Đối tượng Resident chứa toàn bộ thông tin của chủ hộ
            householdsWindow.data.add(newHousehold);

            // Cập nhật bảng và phân trang
            householdsWindow.updateTable();

            // Đóng popup sau khi lưu thành công
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Có lỗi xảy ra khi thêm hộ khẩu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }




    private void editHouseholdContent(JPanel panel, int editIndex) {
        panel.setLayout(null);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(null);
        headerPanel.setBounds(0, 0, getWidth(), getHeight() / 10);
        headerPanel.setBackground(Color.LIGHT_GRAY);

        JLabel titleLabel = new JLabel("Chỉnh sửa Hộ Khẩu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(0, 0, headerPanel.getWidth(), headerPanel.getHeight());
        headerPanel.add(titleLabel);
        panel.add(headerPanel);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null);
        contentPanel.setBounds(0, headerPanel.getHeight(), getWidth(), getHeight() * 4 / 5);
        contentPanel.setBackground(Color.WHITE);

        // Địa chỉ
        JLabel addressLabel = new JLabel("Địa chỉ:");
        addressLabel.setBounds(20, 50, getWidth() / 4, 40);
        contentPanel.add(addressLabel);

        addressField = new JTextField();
        addressField.setBounds(10 + getWidth() / 4 + 5, 50, 300, 40);
        contentPanel.add(addressField);

        /*

        // Chủ hộ
        JLabel headOfHouseholdLabel = new JLabel("Chủ hộ:");
        headOfHouseholdLabel.setBounds(20, 110, getWidth() / 4, 40);
        contentPanel.add(headOfHouseholdLabel);

        headOfHouseholdField = new JTextField();
        headOfHouseholdField.setBounds(10 + getWidth() / 4 + 5, 110, 300, 40);
        contentPanel.add(headOfHouseholdField);

         */

        panel.add(contentPanel);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new GridLayout(1, 2));
        footerPanel.setBounds(0, headerPanel.getHeight() + contentPanel.getHeight(), getWidth(), getHeight() / 10);
        footerPanel.setBackground(Color.LIGHT_GRAY);

        // Nút Lưu
        JButton saveButton = new JButton("Lưu");
        saveButton.setPreferredSize(new Dimension(60, 40));
        saveButton.addActionListener(e -> {
            //saveEditHousehold(Integer.parseInt(householdIDField.getText()));
            dispose();
        });
        footerPanel.add(saveButton);

        // Nút Hủy
        JButton cancelButton = new JButton("Hủy");
        cancelButton.setPreferredSize(new Dimension(60, 40));
        cancelButton.addActionListener(e -> dispose());
        footerPanel.add(cancelButton);

        panel.add(footerPanel);
    }

    /*
    private void saveEditHousehold(int householdID) {
        // Lấy giá trị từ các trường nhập liệu
        String address = addressField.getText();
        String headOfHousehold = headOfHouseholdField.getText();

        if (address.isEmpty() || headOfHousehold.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng điền đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Cập nhật cơ sở dữ liệu
        //DatabaseConnected.updateHousehold(householdID, householdName, address, headOfHousehold);

        // Cập nhật danh sách hộ khẩu
        Object[] updatedHousehold = new Object[4];
        updatedHousehold[1] = householdID;
        updatedHousehold[2] = address;
        updatedHousehold[3] = headOfHousehold;

        //householdsWindow.data.set(householdsWindow.data.indexOf(householdsWindow.data.get(householdID)), updatedHousehold);

        // Cập nhật bảng và phân trang
        householdsWindow.updateTable();
    }

     */

}
