package view.residents;

import model.Resident;

import javax.swing.*;
import java.awt.*;

import static controller.ResidentDAO.*;

public class ResidentPopup extends JDialog {
    public ResidentsWindow residentsWindow;

    private JTextField nameField;
    private JTextField birthDateField;
    private JComboBox<String> genderField;
    private JTextField idCardField;

    // Các hằng số để xác định loại popup
    public static final int ADD_RESIDENT = 1;
    public static final int EDIT_RESIDENT = 2;

    public ResidentPopup(JFrame parent, int popupName, ResidentsWindow residentsWindow, int editIndex) {
        super(parent, "Popup", true);
        setUndecorated(true);
        setSize((int) (parent.getWidth() * 0.4), (int) (parent.getHeight() * 0.8));
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
                editResidentContent(popupPanel, editIndex);
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

        // Gợi ý cho Họ và tên
        JLabel nameHintLabel = new JLabel("Họ và tên đầy đủ");
        nameHintLabel.setBounds(20 + getWidth() / 4, 90, 300, 20);
        nameHintLabel.setForeground(Color.GRAY);
        contentPanel.add(nameHintLabel);

        // Ngày sinh
        JLabel birthDateLabel = new JLabel("Ngày sinh:");
        birthDateLabel.setBounds(20, 110, getWidth() / 4, 40);
        contentPanel.add(birthDateLabel);

        birthDateField = new JTextField();
        birthDateField.setBounds(10 + getWidth() / 4 + 5, 110, 300, 40);
        contentPanel.add(birthDateField);

        // Gợi ý cho Ngày sinh
        JLabel birthDateHintLabel = new JLabel("YYYY-MM-DD");
        birthDateHintLabel.setBounds(20 + getWidth() / 4, 150, 300, 20);
        birthDateHintLabel.setForeground(Color.GRAY);
        contentPanel.add(birthDateHintLabel);

        // Giới tính
        JLabel genderLabel = new JLabel("Giới tính:");
        genderLabel.setBounds(20, 170, getWidth() / 4, 40);
        contentPanel.add(genderLabel);

        String[] genderOptions = {"Nam", "Nữ", "Khác"};
        genderField = new JComboBox<>(genderOptions);
        genderField.setBounds(10 + getWidth() / 4 + 5, 170, 300, 40);
        contentPanel.add(genderField);

        genderField.setSelectedIndex(0);

        // Gợi ý cho Giới tính
        JLabel genderHintLabel = new JLabel("Nam/Nữ/Khác");
        genderHintLabel.setBounds(20 + getWidth() / 4, 210, 300, 20);
        genderHintLabel.setForeground(Color.GRAY);
        contentPanel.add(genderHintLabel);

        // CCCD
        JLabel idCardLabel = new JLabel("CCCD:");
        idCardLabel.setBounds(20, 230, getWidth() / 4, 40);
        contentPanel.add(idCardLabel);

        idCardField = new JTextField();
        idCardField.setBounds(10 + getWidth() / 4 + 5, 230, 300, 40);
        contentPanel.add(idCardField);

        // Gợi ý cho CCCD
        JLabel idCardHintLabel = new JLabel("Đúng định dạng (12 số)");
        idCardHintLabel.setBounds(20 + getWidth() / 4, 270, 300, 20);
        idCardHintLabel.setForeground(Color.GRAY);
        contentPanel.add(idCardHintLabel);

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
        String gender = (String) genderField.getSelectedItem();
        String idCard = idCardField.getText();

        // Kiểm tra hợp lệ (có thể tùy chỉnh theo yêu cầu của bạn)
        if (name.isEmpty() || birthDate.isEmpty() || gender.isEmpty() || idCard.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng điền đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Lưu vào cơ sở dữ liệu
        boolean success = addResident(name, birthDate, gender, idCard);

        // Cập nhật dữ liệu
        residentsWindow.resetData();

        if (success) {
            dispose();
        }
    }

    private void editResidentContent(JPanel panel, int editIndex) {
        Resident data = getResidentById(editIndex);

        String oldName = data.full_name;
        String oldBirthDate = data.date_of_birth;
        String oldGender = data.gender;
        String oldIdCard = data.idCard;

        panel.setLayout(null);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(null);
        headerPanel.setBounds(0, 0, getWidth(), getHeight() / 10);
        headerPanel.setBackground(Color.LIGHT_GRAY);

        JLabel titleLabel = new JLabel("Sửa Nhân Khẩu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(0, 0, headerPanel.getWidth(), headerPanel.getHeight());
        headerPanel.add(titleLabel);
        panel.add(headerPanel);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null);
        contentPanel.setBounds(0, headerPanel.getHeight(), getWidth(), getHeight() * 4 / 5);
        contentPanel.setBackground(Color.WHITE);

        //Old
        JLabel oldNameLabel = new JLabel("Họ và tên cũ:");
        oldNameLabel.setBounds(20, 50, getWidth() / 4, 40);
        oldNameLabel.setFont(oldNameLabel.getFont().deriveFont(Font.ITALIC));
        contentPanel.add(oldNameLabel);

        JLabel oldNameLabel2 = new JLabel(oldName);
        oldNameLabel2.setBounds(20 + getWidth() / 4, 50, 300, 40);
        contentPanel.add(oldNameLabel2);

        // Họ và tên
        JLabel nameLabel = new JLabel("Họ và tên:");
        nameLabel.setBounds(20, 90, getWidth() / 4, 40);
        contentPanel.add(nameLabel);

        nameField = new JTextField(oldName);
        nameField.setBounds(10 + getWidth() / 4 + 5, 90, 300, 40);
        contentPanel.add(nameField);

        //Old
        JLabel oldDateLabel = new JLabel("Ngày sinh cũ:");
        oldDateLabel.setBounds(20, 130, getWidth() / 4, 40);
        oldDateLabel.setFont(oldDateLabel.getFont().deriveFont(Font.ITALIC));
        contentPanel.add(oldDateLabel);

        JLabel oldDateLabel2 = new JLabel(oldBirthDate);
        oldDateLabel2.setBounds(20 + getWidth() / 4, 130, 300, 40);
        contentPanel.add(oldDateLabel2);

        // Ngày sinh
        JLabel birthDateLabel = new JLabel("Ngày sinh:");
        birthDateLabel.setBounds(20, 170, getWidth() / 4, 40);
        contentPanel.add(birthDateLabel);

        birthDateField = new JTextField(oldBirthDate);
        birthDateField.setBounds(10 + getWidth() / 4 + 5, 170, 300, 40);
        contentPanel.add(birthDateField);

        //Old
        JLabel oldGenderLabel = new JLabel("Giới tính cũ:");
        oldGenderLabel.setBounds(20, 210, getWidth() / 4, 40);
        oldGenderLabel.setFont(oldGenderLabel.getFont().deriveFont(Font.ITALIC));
        contentPanel.add(oldGenderLabel);

        JLabel oldGenderLabel2 = new JLabel(oldGender);
        oldGenderLabel2.setBounds(20 + getWidth() / 4, 210, 300, 40);
        contentPanel.add(oldGenderLabel2);

        // Giới tính
        JLabel genderLabel = new JLabel("Giới tính:");
        genderLabel.setBounds(20, 250, getWidth() / 4, 40);
        contentPanel.add(genderLabel);

        // Create a JComboBox with predefined options
        String[] genderOptions = {"Nam", "Nữ", "Khác"};
        genderField = new JComboBox<>(genderOptions);
        genderField.setBounds(10 + getWidth() / 4 + 5, 250, 300, 40);
        contentPanel.add(genderField);

        // Set the selected item based on oldGender value
        genderField.setSelectedItem(oldGender);

        //Old
        JLabel oldIdCardLabel = new JLabel("CCCD cũ:");
        oldIdCardLabel.setBounds(20, 290, getWidth() / 4, 40);
        oldIdCardLabel.setFont(oldIdCardLabel.getFont().deriveFont(Font.ITALIC));
        contentPanel.add(oldIdCardLabel);

        JLabel oldIdCardLabel2 = new JLabel(oldIdCard);
        oldIdCardLabel2.setBounds(20 + getWidth() / 4, 290, 300, 40);
        contentPanel.add(oldIdCardLabel2);

        // CCCD
        JLabel idCardLabel = new JLabel("CCCD:");
        idCardLabel.setBounds(20, 330, getWidth() / 4, 40);
        contentPanel.add(idCardLabel);

        idCardField = new JTextField(oldIdCard);
        idCardField.setBounds(10 + getWidth() / 4 + 5, 330, 300, 40);
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
            saveEditResident(data.id);
        });
        footerPanel.add(continueButton);

        // Nút Hủy
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(60, 40));
        cancelButton.addActionListener(e -> dispose());
        footerPanel.add(cancelButton);

        panel.add(footerPanel);
    }

    private void saveEditResident(int residentID) {
        // Lấy giá trị từ các trường nhập liệu
        String name = nameField.getText();
        String birthDate = birthDateField.getText();
        String gender = (String) genderField.getSelectedItem();
        String idCard = idCardField.getText();

        // Kiểm tra hợp lệ (có thể tùy chỉnh theo yêu cầu của bạn)
        if (name.isEmpty() || birthDate.isEmpty() || gender.isEmpty() || idCard.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng điền đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Sửa cơ sở dữ liệu
        boolean success = updateResident(residentID, name, birthDate, gender, idCard);

        if (success) {
            JOptionPane.showMessageDialog(null, "Cập nhật thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            residentsWindow.resetData();
            dispose();
        } else {
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi trong quá trình cập nhật.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
