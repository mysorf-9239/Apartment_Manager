package view.households;

import controller.HouseholdDAO;
import model.Household;
import model.HouseholdMember;
import model.Resident;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class HouseholdPopup extends JDialog {
    public HouseholdsWindow householdsWindow;

    ArrayList<HouseholdMember> members;

    private JTextField addressField;
    private JTextField acreageField;
    private JTextField nameField;
    private JTextField CCCDField;

    public static final int VIEW_HOUSEHOLD = 1;
    public static final int ADD_HOUSEHOLD = 2;
    public static final int EDIT_HOUSEHOLD = 3;

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
            case VIEW_HOUSEHOLD:
                viewHouseholdContent(popupPanel, editIndex);
                break;
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

    private void viewHouseholdContent(JPanel panel, int editIndex) {
        Household data = HouseholdDAO.getHouseholdById(editIndex);

        String householdAddress = data.address;
        Resident headOfHousehold = data.head_of_household;

        panel.setLayout(null);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(null);
        headerPanel.setBounds(0, 0, getWidth(), getHeight() / 10);
        headerPanel.setBackground(Color.LIGHT_GRAY);

        JLabel titleLabel = new JLabel("Xem Hộ Khẩu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(0, 0, getWidth(), headerPanel.getHeight());
        headerPanel.add(titleLabel);
        panel.add(headerPanel);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null);
        contentPanel.setBounds(0, headerPanel.getHeight(), getWidth(), getHeight() * 4 / 5);
        contentPanel.setBackground(Color.WHITE);

        // Địa chỉ
        JLabel addressLabel = new JLabel("Địa chỉ:");
        addressLabel.setBounds(20, 50, getWidth() / 4, 40);
        contentPanel.add(addressLabel);

        JTextField addressField = new JTextField(householdAddress);
        addressField.setBounds(20 + getWidth() / 4, 50, 300, 40);
        addressField.setEditable(false);
        contentPanel.add(addressField);

        // Chủ hộ
        JLabel headOfHouseholdLabel = new JLabel("Chủ hộ:");
        headOfHouseholdLabel.setBounds(20, 100, getWidth() / 4, 40);
        contentPanel.add(headOfHouseholdLabel);

        JTextField headOfHouseholdField = new JTextField(headOfHousehold.full_name);
        headOfHouseholdField.setBounds(20 + getWidth() / 4, 100, 200, 40);
        headOfHouseholdField.setEditable(false);
        contentPanel.add(headOfHouseholdField);


        JPanel leftMembersPanel = new JPanel();
        JPanel rightActionPanel = new JPanel();


        JButton detailsButton = new JButton("Chi tiết");
        detailsButton.setBounds(330, 100, 100, 40);
        detailsButton.addActionListener(e -> {
            showHeadOfHouseholdDetails(rightActionPanel, headOfHousehold);
        });
        contentPanel.add(detailsButton);

        JLabel menberTitleLable = new JLabel("Các thành viên");
        menberTitleLable.setBounds(25, 143, 150, 30);
        contentPanel.add(menberTitleLable);

        // Left Panel for members (Chia ra phần bên trái)
        leftMembersPanel.setLayout(null);
        leftMembersPanel.setBounds(20, 180, (getWidth() - 60) / 2, 270);
        rightActionPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        contentPanel.add(leftMembersPanel);

        // Right Panel (Initially empty, sẽ hiển thị thông tin chi tiết hoặc form thêm)
        rightActionPanel.setLayout(null);
        rightActionPanel.setBounds(40 + leftMembersPanel.getWidth(), 180, (getWidth() - 60) / 2, 270);
        rightActionPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        contentPanel.add(rightActionPanel);

        // Lấy danh sách thành viên cùng mối quan hệ
        members = HouseholdDAO.getHouseholdMembersWithRelationships(data.id);
        int yOffset = 10;
        for (HouseholdMember member : members) {
            // Member name
            JLabel memberLabel = new JLabel(member.resident.full_name);
            memberLabel.setBounds(5, yOffset, leftMembersPanel.getWidth() / 2, 30);
            leftMembersPanel.add(memberLabel);

            // Member relationship
            JLabel relationshipLabel = new JLabel(member.relationshipType);
            relationshipLabel.setBounds(15+ leftMembersPanel.getWidth() / 2, yOffset, leftMembersPanel.getWidth() / 2, 30);
            leftMembersPanel.add(relationshipLabel);

            JButton showMemberButton = new JButton("View");
            showMemberButton.setBounds(130, yOffset, 60, 30);
            showMemberButton.addActionListener(e -> {
                showMemberDetails(rightActionPanel, member.resident, member.relationshipType);
            });
            leftMembersPanel.add(showMemberButton);

            // Tăng vị trí y để thành viên kế tiếp hiển thị bên dưới
            yOffset += 35;

            // Add a listener to show member details on click
            memberLabel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    showMemberDetails(rightActionPanel, member.resident, member.relationshipType);
                }
            });
        }


        // Button "Thêm" to add a new member
        JButton addButton = new JButton("Thêm");
        addButton.setBounds(350, 143, 60, 30);
        addButton.addActionListener(e -> {
            showAddMemberForm(leftMembersPanel, rightActionPanel, data.id, headOfHousehold.id);
        });
        contentPanel.add(addButton);

        panel.add(contentPanel);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(null);
        footerPanel.setBounds(0, headerPanel.getHeight() + contentPanel.getHeight(), getWidth(), getHeight() / 10);
        footerPanel.setBackground(Color.LIGHT_GRAY);

        // Nút Hủy
        JButton cancelButton = new JButton("Hủy");
        cancelButton.setBounds((footerPanel.getWidth() - 60) / 2, (footerPanel.getHeight() - 40) / 2, 60, 40);
        cancelButton.addActionListener(e -> dispose());
        footerPanel.add(cancelButton);

        panel.add(footerPanel);
    }

    // Thông tin chủ hộ
    private void showHeadOfHouseholdDetails(JPanel rightPanel, Resident headOfHousehold) {
        rightPanel.removeAll();

        JLabel title = new JLabel("Chi tiết Chủ hộ:");
        title.setBounds(10, 10, rightPanel.getWidth() - 20, 30);
        rightPanel.add(title);

        JLabel nameLabel = new JLabel("Họ và tên: " + headOfHousehold.full_name);
        nameLabel.setBounds(10, 50, rightPanel.getWidth() - 20, 30);
        rightPanel.add(nameLabel);

        JLabel dobLabel = new JLabel("Ngày sinh: " + headOfHousehold.date_of_birth);
        dobLabel.setBounds(10, 90, rightPanel.getWidth() - 20, 30);
        rightPanel.add(dobLabel);

        JLabel genderLabel = new JLabel("Giới tính: " + headOfHousehold.gender);
        genderLabel.setBounds(10, 130, rightPanel.getWidth() - 20, 30);
        rightPanel.add(genderLabel);

        JLabel idCardLabel = new JLabel("CCCD: " + headOfHousehold.idCard);
        idCardLabel.setBounds(10, 170, rightPanel.getWidth() - 20, 30);
        rightPanel.add(idCardLabel);

        rightPanel.revalidate();
        rightPanel.repaint();
    }

    // Thông tin thành viên
    private void showMemberDetails(JPanel rightPanel, Resident member, String relationshipType) {
        rightPanel.removeAll();

        JLabel title = new JLabel("Chi tiết Thành viên:");
        title.setBounds(10, 10, rightPanel.getWidth() - 20, 30);
        rightPanel.add(title);

        JLabel nameLabel = new JLabel("Họ và tên: " + member.full_name);
        nameLabel.setBounds(10, 50, rightPanel.getWidth() - 20, 30);
        rightPanel.add(nameLabel);

        JLabel relationLabel = new JLabel("Quan hệ: " + relationshipType);
        relationLabel.setBounds(10, 90, rightPanel.getWidth() - 20, 30);
        rightPanel.add(relationLabel);

        JLabel dobLabel = new JLabel("Ngày sinh: " + member.date_of_birth);
        dobLabel.setBounds(10, 130, rightPanel.getWidth() - 20, 30);
        rightPanel.add(dobLabel);

        JLabel genderLabel = new JLabel("Giới tính: " + member.gender);
        genderLabel.setBounds(10, 170, rightPanel.getWidth() - 20, 30);
        rightPanel.add(genderLabel);

        JLabel idCardLabel = new JLabel("CCCD: " + member.idCard);
        idCardLabel.setBounds(10, 210, rightPanel.getWidth() - 20, 30);
        rightPanel.add(idCardLabel);

        rightPanel.revalidate();
        rightPanel.repaint();
    }

    // Thêm thành viên
    private void showAddMemberForm(JPanel leftPanel, JPanel rightPanel, int householdID, int headOfHouseholdID) {
        rightPanel.removeAll();

        // Tạo tiêu đề
        JLabel title = new JLabel("Thêm thành viên mới");
        title.setBounds(10, 10, rightPanel.getWidth() - 20, 30);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        rightPanel.add(title);

        // Tạo label và text field để nhập tên thành viên
        JLabel nameLabel = new JLabel("Họ và tên:");
        nameLabel.setBounds(10, 50, 100, 25);
        rightPanel.add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setBounds(10, 75, 165, 25);
        rightPanel.add(nameField);

        // Tạo label và text field để nhập CCCD
        JLabel idCardLabel = new JLabel("CCCD:");
        idCardLabel.setBounds(10, 100, 100, 25);
        rightPanel.add(idCardLabel);

        JTextField idCardField = new JTextField();
        idCardField.setBounds(10, 125, 165, 25);
        rightPanel.add(idCardField);

        // Tạo label và text field để nhập quan hệ với chủ hộ
        JLabel relationshipLabel = new JLabel("Quan hệ với chủ hộ:");
        relationshipLabel.setBounds(10, 150, 150, 25);
        rightPanel.add(relationshipLabel);

        JTextField relationshipField = new JTextField();
        relationshipField.setBounds(10, 175, 165, 25);
        rightPanel.add(relationshipField);

        // Nút Lưu để thêm thành viên mới
        JButton saveButton = new JButton("Lưu");
        saveButton.setBounds(60, 220, 60, 40);
        rightPanel.add(saveButton);

        // Xử lý sự kiện khi nhấn nút Lưu
        saveButton.addActionListener(e -> {
            String name = nameField.getText();
            String idCard = idCardField.getText();
            String relationship = relationshipField.getText();

            if (name.isEmpty() || idCard.isEmpty() || relationship.isEmpty()) {
                JOptionPane.showMessageDialog(rightPanel, "Vui lòng điền đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Gọi hàm để thêm thành viên mới vào hộ khẩu
            addNewMemberToHousehold(leftPanel, rightPanel, householdID, headOfHouseholdID, name, idCard, relationship);

            // Cập nhật giao diện sau khi thêm thành viên thành công
            rightPanel.removeAll();
            rightPanel.add(new JLabel("Thành viên mới đã được thêm!"));
            rightPanel.revalidate();
            rightPanel.repaint();
        });

        // Cập nhật lại giao diện
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    private void addNewMemberToHousehold(JPanel leftPanel, JPanel rightPanel, int householdID, int headOfHouseholdID, String name, String idCard, String relationshipType) {
        try {
            // Step 1: Tìm resident_id từ residents table
            Resident addResident = HouseholdDAO.getHeadOfHouseholdInfo(name, idCard);

            // Step 2: Thêm thành viên vào hộ khẩu
            assert addResident != null;
            boolean inserted = HouseholdDAO.addRelationship(addResident.id, headOfHouseholdID, relationshipType, householdID);
            if (inserted) {
                JOptionPane.showMessageDialog(null, "Thêm hộ khẩu thành công!");

                // Step 3: Update member
                HouseholdMember newMember = new HouseholdMember(addResident, relationshipType);

                members.add(newMember);

                // Re-render the members panel to show the newly added member
                reRender(leftPanel, rightPanel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Vẽ lại
    private void reRender(JPanel leftPanel, JPanel rightPanel) {
        leftPanel.removeAll();

        int yOffset = 10;

        for (HouseholdMember member : members) {
            JLabel memberLabel = new JLabel(member.resident.full_name);
            memberLabel.setBounds(5, yOffset, leftPanel.getWidth() / 2, 30);
            leftPanel.add(memberLabel);

            JLabel relationshipLabel = new JLabel(member.relationshipType);
            relationshipLabel.setBounds(15 + leftPanel.getWidth() / 2, yOffset, leftPanel.getWidth() / 2, 30);
            leftPanel.add(relationshipLabel);

            JButton viewMemberButton = new JButton("View");
            viewMemberButton.setBounds(leftPanel.getWidth() - 70, yOffset, 60, 30);
            viewMemberButton.addActionListener(e -> {
                showMemberDetails(rightPanel, member.resident, member.relationshipType);
            });
            leftPanel.add(viewMemberButton);

            yOffset += 35;
        }

        leftPanel.revalidate();
        leftPanel.repaint();
    }

    // Thêm hộ khẩu
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

        // Diện tích
        JLabel m2Label = new JLabel("Diện tích:");
        m2Label.setBounds(20, 120, getWidth() / 4, 40);
        contentPanel.add(m2Label);

        acreageField = new JTextField();
        acreageField.setBounds(20 + getWidth() / 4, 120, 300, 40);
        contentPanel.add(acreageField);

        // Gợi ý cho Địa chỉ
        JLabel m2HintLabel = new JLabel("Diện tích căn hộ (m2)");
        m2HintLabel.setBounds(25 + getWidth() / 4, 160, 300, 20);
        m2HintLabel.setForeground(Color.GRAY);
        contentPanel.add(m2HintLabel);

        // Chủ hộ
        JLabel headOfHouseholdLabel = new JLabel("Chủ hộ:");
        headOfHouseholdLabel.setBounds(20, 170, getWidth() / 4, 40);
        contentPanel.add(headOfHouseholdLabel);

        // Tên chủ hộ
        JLabel nameLabel = new JLabel("Họ và tên:");
        nameLabel.setBounds(40, 210, getWidth() / 4, 40);
        contentPanel.add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(20 + getWidth() / 4, 230, 300, 40);
        contentPanel.add(nameField);

        // Gợi ý cho Họ và tên
        JLabel nameHintLabel = new JLabel("Họ và tên đầy đủ");
        nameHintLabel.setBounds(25 + getWidth() / 4, 270, 300, 20);
        nameHintLabel.setForeground(Color.GRAY);
        contentPanel.add(nameHintLabel);

        // CCCD
        JLabel idCardLabel = new JLabel("CCCD:");
        idCardLabel.setBounds(40, 290, getWidth() / 4, 40);
        contentPanel.add(idCardLabel);

        CCCDField = new JTextField();
        CCCDField.setBounds(20 + getWidth() / 4, 320, 300, 40);
        contentPanel.add(CCCDField);

        // Gợi ý cho CCCD
        JLabel CCCDHintLabel = new JLabel("Số căn cước công dân");
        CCCDHintLabel.setBounds(25 + getWidth() / 4, 360, 300, 20);
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
        Double acreage = null;
        if (acreageField.getText() != null && !acreageField.getText().equals("")) {
            acreage = Double.valueOf(acreageField.getText());
        }
        String fullName = nameField.getText();
        String idCard = CCCDField.getText();

        if (address.isEmpty() || fullName.isEmpty() || idCard.isEmpty() || acreage.isNaN()) {
            JOptionPane.showMessageDialog(null, "Vui lòng điền đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Bước 1: Tìm thông tin của chủ hộ
            Resident headOfHousehold = HouseholdDAO.getHeadOfHouseholdInfo(fullName, idCard);
            if (headOfHousehold == null) {
                JOptionPane.showMessageDialog(null, "Không tìm thấy thông tin chủ hộ trong hệ thống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Bước 2: Thêm hộ khẩu mới vào bảng households và lấy household_id
            int householdId = HouseholdDAO.addHousehold(address, acreage, headOfHousehold.id);
            if (householdId == -1) {
                JOptionPane.showMessageDialog(null, "Có lỗi xảy ra khi thêm hộ khẩu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Hiển thị thông báo thêm thành công
            JOptionPane.showMessageDialog(null, "Thêm hộ khẩu thành công!");

            // Cập nhật bảng và phân trang
            householdsWindow.resetData();

            // Đóng popup sau khi lưu thành công
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Có lỗi xảy ra khi thêm hộ khẩu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Sửa hộ khẩu
    private void editHouseholdContent(JPanel panel, int editIndex) {
        Household data = HouseholdDAO.getHouseholdById(editIndex);

        String householdAddress = data.address;
        Resident householdHeader = data.head_of_household;
        Double acreage = data.acreage;

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

        //Old
        JLabel oldAddressLabel = new JLabel("Địa chỉ cũ:");
        oldAddressLabel.setBounds(20, 50, getWidth() / 4, 40);
        oldAddressLabel.setFont(oldAddressLabel.getFont().deriveFont(Font.ITALIC));
        contentPanel.add(oldAddressLabel);

        JLabel oldAddressLabel1 = new JLabel(householdAddress);
        oldAddressLabel1.setBounds(25 + getWidth() / 4, 50, getWidth() / 2, 40);
        contentPanel.add(oldAddressLabel1);

        // Địa chỉ
        JLabel addressLabel = new JLabel("Địa chỉ:");
        addressLabel.setBounds(20, 90, getWidth() / 4, 40);
        contentPanel.add(addressLabel);

        addressField = new JTextField(householdAddress);
        addressField.setBounds(20 + getWidth() / 4, 90, 300, 40);
        contentPanel.add(addressField);

        //Old
        JLabel oldAcreageLabel = new JLabel("Diện tích cũ:");
        oldAcreageLabel.setBounds(20, 130, getWidth() / 4, 40);
        oldAcreageLabel.setFont(oldAddressLabel.getFont().deriveFont(Font.ITALIC));
        contentPanel.add(oldAcreageLabel);

        JLabel oldAcreageLabel1 = new JLabel(acreage.toString());
        oldAcreageLabel1.setBounds(25 + getWidth() / 4, 130, getWidth() / 2, 40);
        contentPanel.add(oldAcreageLabel1);

        // Diện tích
        JLabel acreageLabel = new JLabel("Diện tích:");
        acreageLabel.setBounds(20, 170, getWidth() / 4, 40);
        contentPanel.add(acreageLabel);

        acreageField = new JTextField(acreage.toString());
        acreageField.setBounds(20 + getWidth() / 4, 170, 300, 40);
        contentPanel.add(acreageField);

        // Chủ hộ
        JLabel headOfHouseholdLabel = new JLabel("Chủ hộ:");
        headOfHouseholdLabel.setBounds(20, 210, getWidth() / 4, 40);
        contentPanel.add(headOfHouseholdLabel);

        //Old
        JLabel oldNameLabel = new JLabel("Họ và tên cũ:");
        oldNameLabel.setBounds(40, 250, getWidth() / 4, 40);
        oldNameLabel.setFont(oldNameLabel.getFont().deriveFont(Font.ITALIC));
        contentPanel.add(oldNameLabel);

        JLabel oldNameLabel1 = new JLabel(householdHeader.full_name);
        oldNameLabel1.setBounds(25 + getWidth() / 4, 250, getWidth() / 2, 40);
        contentPanel.add(oldNameLabel1);

        // Tên chủ hộ
        JLabel nameLabel = new JLabel("Họ và tên:");
        nameLabel.setBounds(40, 290, getWidth() / 4, 40);
        contentPanel.add(nameLabel);

        nameField = new JTextField(householdHeader.full_name);
        nameField.setBounds(20 + getWidth() / 4, 290, 300, 40);
        contentPanel.add(nameField);

        //Old
        JLabel oldCCCDLabel = new JLabel("CCCD cũ:");
        oldCCCDLabel.setBounds(40, 340, getWidth() / 4, 40);
        oldCCCDLabel.setFont(oldCCCDLabel.getFont().deriveFont(Font.ITALIC));
        contentPanel.add(oldCCCDLabel);

        JLabel oldCCCDLabel1 = new JLabel(householdHeader.idCard);
        oldCCCDLabel1.setBounds(25 + getWidth() / 4, 340, getWidth() / 2, 40);
        contentPanel.add(oldCCCDLabel1);

        // CCCD
        JLabel idCardLabel = new JLabel("CCCD:");
        idCardLabel.setBounds(40, 380, getWidth() / 4, 40);
        contentPanel.add(idCardLabel);

        CCCDField = new JTextField(householdHeader.idCard);
        CCCDField.setBounds(20 + getWidth() / 4, 380, 300, 40);
        contentPanel.add(CCCDField);

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
            saveEditHousehold(data.id);
        });
        footerPanel.add(saveButton);

        // Nút Hủy
        JButton cancelButton = new JButton("Hủy");
        cancelButton.setPreferredSize(new Dimension(60, 40));
        cancelButton.addActionListener(e -> dispose());
        footerPanel.add(cancelButton);

        panel.add(footerPanel);
    }

    private void saveEditHousehold(int householdID) {
        String newAddress = addressField.getText();
        Double newAcreage = null;
        if (acreageField.getText() != null) {
            newAcreage = Double.parseDouble(acreageField.getText());
        }
        String newHeadOfHouseholdName = nameField.getText();
        String newIdCard = CCCDField.getText();

        if (newAddress.isEmpty() || newHeadOfHouseholdName.isEmpty() || newIdCard.isEmpty() || newAcreage.isNaN()) {
            JOptionPane.showMessageDialog(null, "Vui lòng điền đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Resident headOfHousehold = HouseholdDAO.getHeadOfHouseholdInfo(newHeadOfHouseholdName, newIdCard);
            if (headOfHousehold == null) {
                JOptionPane.showMessageDialog(null, "Không tìm thấy thông tin chủ hộ trong hệ thống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Cập nhật cơ sở dữ liệu
            boolean isUpdated = HouseholdDAO.updateHousehold(householdID, newAddress, newAcreage, headOfHousehold.id);
            if (!isUpdated) {
                JOptionPane.showMessageDialog(null, "Có lỗi xảy ra khi cập nhật thông tin hộ khẩu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Cập nhật bảng và phân trang
            householdsWindow.resetData();

            // Hiển thị thông báo thành công
            JOptionPane.showMessageDialog(null, "Cập nhật hộ khẩu thành công!");

            // Đóng popup sau khi lưu thành công
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Có lỗi xảy ra khi cập nhật hộ khẩu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
