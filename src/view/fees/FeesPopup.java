package view.fees;

import controller.DatabaseConnected;
import model.Fee;
import model.HouseholdInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class FeesPopup extends JDialog {
    public FeesWindow feesWindow;

    private JTextField feeNameField;
    private JTextField amountField;
    private JTextField descriptionField;
    private JComboBox<String> feeTypeComboBox;
    JCheckBox unpaidCheckBox;
    JCheckBox paidCheckBox;
    JCheckBox partialPaidCheckBox;
    JCheckBox overdueCheckBox;
    JCheckBox canceledCheckBox;
    JCheckBox processingCheckBox;

    private JTable householdTable;

    public static final int VIEW_FEE = 1;
    public static final int ADD_FEE = 2;
    public static final int EDIT_FEE = 3;

    Fee fee;

    public FeesPopup(JFrame parent, int popupName, FeesWindow feesWindow, int editIndex) {
        super(parent, "Popup", true);
        setUndecorated(true);
        setSize((int)(parent.getWidth() * 0.4), (int)(parent.getHeight() * 0.8));
        setLocationRelativeTo(parent);

        this.feesWindow = feesWindow;

        JPanel popupPanel = new JPanel();
        popupPanel.setLayout(null);
        popupPanel.setBackground(Color.WHITE);

        switch (popupName) {
            case VIEW_FEE:
                viewFeeContent(popupPanel, editIndex);
                break;
            case ADD_FEE:
                addFeeContent(popupPanel);
                break;
            case EDIT_FEE:
                editFeeContent(popupPanel, editIndex);
                break;
            default:
                throw new IllegalArgumentException("Invalid popup type");
        }

        setContentPane(popupPanel);
    }

    private void viewFeeContent(JPanel panel, int viewIndex) {
        panel.setLayout(null);
        fee = DatabaseConnected.getFeeById(viewIndex);


        // Header
        createHeaderPanel(panel);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null);
        contentPanel.setBounds(0, getHeight() / 10, getWidth(), getHeight() * 4 / 5);
        panel.add(contentPanel);

        // Phần hiển thị tên khoản phí, loại phí và các checkbox/dropdown
        createFeeInfoSection(contentPanel);

        // Footer
        createFooterPanel(panel);
    }

    // Hàm tạo header
    private void createHeaderPanel(JPanel panel) {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(null);
        headerPanel.setBounds(0, 0, getWidth(), getHeight() / 10);
        headerPanel.setBackground(Color.LIGHT_GRAY);

        JLabel titleLabel = new JLabel("Xem Khoản Phí", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(0, 0, getWidth(), headerPanel.getHeight());
        headerPanel.add(titleLabel);

        panel.add(headerPanel);
    }

    private void createFeeInfoSection(JPanel contentPanel) {
        JLabel feeNameLabel = new JLabel("Tên Khoản Phí:");
        feeNameLabel.setBounds(20, 50, 100, 30);
        contentPanel.add(feeNameLabel);

        // Điền tên khoản phí từ database vào trường nhập liệu
        feeNameField = new JTextField(fee.fee_name);
        feeNameField.setBounds(130, 50, 200, 30);
        contentPanel.add(feeNameField);

        JLabel feeTypeLabel = new JLabel("Loại Phí:");
        feeTypeLabel.setBounds(20, 90, 100, 30);
        contentPanel.add(feeTypeLabel);

        // ComboBox cho loại phí (Chung, Riêng)
        JComboBox<String> feeTypeComboBox = new JComboBox<>(new String[]{"Chung", "Riêng"});
        feeTypeComboBox.setBounds(130, 90, 200, 30);
        // Đặt giá trị loại phí từ database vào ComboBox
        feeTypeComboBox.setSelectedItem(fee.type.equals("all") ? "Chung" : "Riêng");
        contentPanel.add(feeTypeComboBox);
        addStatusCheckboxes(contentPanel, fee);
    }

    // Hàm các checkbox
    private void addStatusCheckboxes(JPanel contentPanel, Fee fee) {
        JLabel statusLabel = new JLabel("Trạng Thái:");
        statusLabel.setBounds(20, 130, 100, 30);
        contentPanel.add(statusLabel);

        unpaidCheckBox = new JCheckBox("Chưa thanh toán", true);
        unpaidCheckBox.setBounds(130, 130, 200, 30);
        unpaidCheckBox.addActionListener(e -> updateHouseholdTable(fee));
        contentPanel.add(unpaidCheckBox);

        paidCheckBox = new JCheckBox("Đã thanh toán");
        paidCheckBox.setBounds(130, 160, 200, 30);
        paidCheckBox.addActionListener(e -> updateHouseholdTable(fee));
        contentPanel.add(paidCheckBox);

        partialPaidCheckBox = new JCheckBox("Thanh toán một phần");
        partialPaidCheckBox.setBounds(130, 190, 200, 30);
        partialPaidCheckBox.addActionListener(e -> updateHouseholdTable(fee));
        contentPanel.add(partialPaidCheckBox);

        overdueCheckBox = new JCheckBox("Quá hạn");
        overdueCheckBox.setBounds(330, 130, 200, 30);
        overdueCheckBox.addActionListener(e -> updateHouseholdTable(fee));
        contentPanel.add(overdueCheckBox);

        canceledCheckBox = new JCheckBox("Đã hủy");
        canceledCheckBox.setBounds(330, 160, 200, 30);
        canceledCheckBox.addActionListener(e -> updateHouseholdTable(fee));
        contentPanel.add(canceledCheckBox);

        processingCheckBox = new JCheckBox("Đang xử lý");
        processingCheckBox.setBounds(330, 190, 200, 30);
        processingCheckBox.addActionListener(e -> updateHouseholdTable(fee));
        contentPanel.add(processingCheckBox);

        createHouseholdInfoSection(contentPanel, fee);
    }

    // Hàm cập nhật bảng hộ gia đình
    private void updateHouseholdTable(Fee fee) {
        // Lấy danh sách hộ gia đình từ database
        String[] selectedStatuses = getSelectedStatuses(unpaidCheckBox, paidCheckBox, partialPaidCheckBox, overdueCheckBox, canceledCheckBox, processingCheckBox);
        ArrayList<HouseholdInfo> householdInfos = DatabaseConnected.getHouseholdInfoByFeeIdAndStatus(fee.id, selectedStatuses);

        // Cập nhật bảng với dữ liệu mới
        DefaultTableModel tableModel = (DefaultTableModel) householdTable.getModel();
        tableModel.setRowCount(0);
        for (HouseholdInfo info : householdInfos) {
            Object[] row = {info.headOfHouseholdName, info.address, info.feeStatus};
            tableModel.addRow(row);
        }
    }

    // Hàm hiển thị phần thông tin hộ gia đình
    private void createHouseholdInfoSection(JPanel contentPanel, Fee fee) {
        JLabel householdLabel = new JLabel("Danh sách Hộ Gia Đình:");
        householdLabel.setBounds(20, 220, 200, 30);
        contentPanel.add(householdLabel);

        // Tạo bảng để hiển thị thông tin hộ gia đình
        String[] columnNames = {"Tên Chủ Hộ", "Địa Chỉ", "Trạng Thái"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        householdTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(householdTable);
        scrollPane.setBounds(20, 250, 400, 150);
        contentPanel.add(scrollPane);

        // Gọi phương thức cập nhật bảng khi tạo phần thông tin hộ gia đình lần đầu
        updateHouseholdTable(fee);
    }

    private String[] getSelectedStatuses(JCheckBox... checkBoxes) {
        ArrayList<String> selectedStatuses = new ArrayList<>();

        for (JCheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                selectedStatuses.add(checkBox.getText());
            }
        }

        return selectedStatuses.toArray(new String[0]);
    }

    // Hàm tạo footer
    private void createFooterPanel(JPanel panel) {
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(null);
        footerPanel.setBounds(0, getHeight() * 9 / 10, getWidth(), getHeight() / 10);
        footerPanel.setBackground(Color.LIGHT_GRAY);

        JButton cancelButton = new JButton("Hủy");
        cancelButton.setBounds((footerPanel.getWidth() - 100) / 2, (footerPanel.getHeight() - 40) / 2, 100, 40);
        cancelButton.addActionListener(e -> dispose());
        footerPanel.add(cancelButton);

        panel.add(footerPanel);
    }


    private void addFeeContent(JPanel panel) {
        panel.setLayout(null);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(null);
        headerPanel.setBounds(0, 0, getWidth(), getHeight() / 10);
        headerPanel.setBackground(Color.LIGHT_GRAY);

        JLabel titleLabel = new JLabel("Thêm Khoản Phí", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(0, 0, getWidth(), headerPanel.getHeight());
        headerPanel.add(titleLabel);
        panel.add(headerPanel);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null);
        contentPanel.setBounds(0, headerPanel.getHeight(), getWidth(), getHeight() * 4 / 5);
        contentPanel.setBackground(Color.WHITE);

        //Tên
        JLabel feeNameLabel = new JLabel("Tên khoản phí:");
        feeNameLabel.setBounds(20, 50, 100, 40);
        contentPanel.add(feeNameLabel);

        feeNameField = new JTextField();
        feeNameField.setBounds(130, 50, 200, 40);
        contentPanel.add(feeNameField);

        // Gợi ý
        JLabel nameHintLabel = new JLabel("Tên của khoản phí");
        nameHintLabel.setBounds(25 + getWidth() / 4, 90, 300, 20);
        nameHintLabel.setForeground(Color.GRAY);
        contentPanel.add(nameHintLabel);

        //Mô tả
        JLabel descriptionLabel = new JLabel("Mô tả:");
        descriptionLabel.setBounds(20, 120, 100, 40);
        contentPanel.add(descriptionLabel);

        descriptionField = new JTextField();
        descriptionField.setBounds(130, 120, 200, 40);
        contentPanel.add(descriptionField);

        // Gợi ý
        JLabel desHintLabel = new JLabel("Miêu tả về khoản phí");
        desHintLabel.setBounds(25 + getWidth() / 4, 160, 300, 20);
        desHintLabel.setForeground(Color.GRAY);
        contentPanel.add(desHintLabel);

        //Số tiền phải nộp của khoản phí
        JLabel amountLabel = new JLabel("Số tiền:");
        amountLabel.setBounds(20, 180, 100, 40);
        contentPanel.add(amountLabel);

        amountField = new JTextField();
        amountField.setBounds(130, 180, 200, 40);
        contentPanel.add(amountField);

        // Gợi ý
        JLabel amountHintLabel = new JLabel("Số tiền phải nộp");
        amountHintLabel.setBounds(25 + getWidth() / 4, 220, 300, 20);
        amountHintLabel.setForeground(Color.GRAY);
        contentPanel.add(amountHintLabel);

        // Dropdown chọn loại phí: Chung/Riêng
        JLabel typeLabel = new JLabel("Loại phí:");
        typeLabel.setBounds(20, 260, 100, 40);
        contentPanel.add(typeLabel);

        String[] feeTypes = {"Chung", "Riêng"};
        feeTypeComboBox = new JComboBox<>(feeTypes);
        feeTypeComboBox.setBounds(130, 260, 200, 40);
        contentPanel.add(feeTypeComboBox);

        // Gợi ý
        JLabel typeHintLabel = new JLabel("Chọn loại phí");
        typeHintLabel.setBounds(25 + getWidth() / 4, 300, 300, 20);
        typeHintLabel.setForeground(Color.GRAY);
        contentPanel.add(typeHintLabel);

        panel.add(contentPanel);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new GridLayout(1, 2));
        footerPanel.setBounds(0, headerPanel.getHeight() + contentPanel.getHeight(), getWidth(), getHeight() / 10);
        footerPanel.setBackground(Color.LIGHT_GRAY);

        // Nút Lưu
        JButton continueButton = new JButton("Lưu");
        continueButton.setPreferredSize(new Dimension(60, 40));
        continueButton.addActionListener(e -> saveAddFee());
        footerPanel.add(continueButton);

        // Nút Hủy
        JButton cancelButton = new JButton("Hủy");
        cancelButton.setPreferredSize(new Dimension(60, 40));
        cancelButton.addActionListener(e -> dispose());
        footerPanel.add(cancelButton);

        panel.add(footerPanel);
    }

    private void saveAddFee() {
        String feeName = feeNameField.getText().trim();
        String feeDescription = descriptionField.getText().trim();
        String amountStr = amountField.getText().trim();
        String feeType = Objects.equals(feeTypeComboBox.getSelectedItem(), "Chung") ? "all" : "part";

        if (feeName.isEmpty() || feeDescription.isEmpty() || amountStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng điền đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Số tiền không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int generatedId = DatabaseConnected.addFee(feeName, feeDescription, amount, feeType);

        if (generatedId != -1) {
            Object[] newFee = new Object[8];
            newFee[0] = String.format("%02d", feesWindow.data.size() + 1);
            newFee[1] = generatedId;
            newFee[2] = feeName;
            newFee[3] = amount;
            newFee[4] = feeDescription;

            Timestamp createdAt = new Timestamp(System.currentTimeMillis());
            newFee[5] = createdAt;
            newFee[6] = createdAt;

            newFee[7] = "active";

            feesWindow.data.add(newFee);
            feesWindow.updateTable();
            dispose();
        } else {
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi trong quá trình lưu phí.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editFeeContent(JPanel panel, int editIndex) {
        Object[] oldData = feesWindow.data.get(editIndex);

        // Lấy các giá trị cũ
        int feeId = (int) oldData[1];
        String oldFeeName = (String) oldData[2];
        String oldFeeDescription = (String) oldData[4];
        double oldAmount = (double) oldData[3];

        panel.setLayout(null);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(null);
        headerPanel.setBounds(0, 0, getWidth(), getHeight() / 10);
        headerPanel.setBackground(Color.LIGHT_GRAY);

        JLabel titleLabel = new JLabel("Chỉnh sửa Khoản Phí", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(0, 0, headerPanel.getWidth(), headerPanel.getHeight());
        headerPanel.add(titleLabel);
        panel.add(headerPanel);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null);
        contentPanel.setBounds(0, headerPanel.getHeight(), getWidth(), getHeight() * 4 / 5);
        contentPanel.setBackground(Color.WHITE);

        // Tên khoản phí cũ
        JLabel oldFeeNameLabel = new JLabel("Tên cũ:");
        oldFeeNameLabel.setBounds(20, 50, getWidth() / 4, 30);
        oldFeeNameLabel.setFont(oldFeeNameLabel.getFont().deriveFont(Font.ITALIC));
        contentPanel.add(oldFeeNameLabel);

        JLabel oldFeeNameValue = new JLabel(oldFeeName);
        oldFeeNameValue.setBounds(25 + getWidth() / 4, 50, getWidth() / 2, 30);
        contentPanel.add(oldFeeNameValue);

        // Tên khoản phí mới
        JLabel feeNameLabel = new JLabel("Tên khoản phí:");
        feeNameLabel.setBounds(20, 90, getWidth() / 4, 30);
        contentPanel.add(feeNameLabel);

        JTextField feeNameField = new JTextField(oldFeeName);
        feeNameField.setBounds(20 + getWidth() / 4, 90, 300, 30);
        contentPanel.add(feeNameField);

        // Mô tả cũ
        JLabel oldDescriptionLabel = new JLabel("Mô tả cũ:");
        oldDescriptionLabel.setBounds(20, 130, getWidth() / 4, 30);
        oldDescriptionLabel.setFont(oldDescriptionLabel.getFont().deriveFont(Font.ITALIC));
        contentPanel.add(oldDescriptionLabel);

        JLabel oldDescriptionValue = new JLabel(oldFeeDescription);
        oldDescriptionValue.setBounds(25 + getWidth() / 4, 130, getWidth() / 2, 30);
        contentPanel.add(oldDescriptionValue);

        // Mô tả mới
        JLabel descriptionLabel = new JLabel("Mô tả:");
        descriptionLabel.setBounds(20, 170, getWidth() / 4, 30);
        contentPanel.add(descriptionLabel);

        JTextField descriptionField = new JTextField(oldFeeDescription);
        descriptionField.setBounds(20 + getWidth() / 4, 170, 300, 30);
        contentPanel.add(descriptionField);

        // Số tiền cũ
        JLabel oldAmountLabel = new JLabel("Số tiền cũ:");
        oldAmountLabel.setBounds(20, 210, getWidth() / 4, 30);
        oldAmountLabel.setFont(oldAmountLabel.getFont().deriveFont(Font.ITALIC));
        contentPanel.add(oldAmountLabel);

        JLabel oldAmountValue = new JLabel(formatCurrency(oldAmount));
        oldAmountValue.setBounds(25 + getWidth() / 4, 210, getWidth() / 2, 30);
        contentPanel.add(oldAmountValue);

        // Số tiền mới
        JLabel amountLabel = new JLabel("Số tiền:");
        amountLabel.setBounds(20, 250, getWidth() / 4, 30);
        contentPanel.add(amountLabel);

        JTextField amountField = new JTextField(String.valueOf(oldAmount));
        amountField.setBounds(20 + getWidth() / 4, 250, 300, 30);
        contentPanel.add(amountField);

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
            saveEditFee(editIndex, feeId, feeNameField.getText(), descriptionField.getText(), amountField.getText());
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

    private void saveEditFee(int editIndex, int feeId, String newFeeName, String newDescription, String newAmountStr) {
        if (newFeeName.isEmpty() || newDescription.isEmpty() || newAmountStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng điền đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double newAmount;
        try {
            newAmount = Double.parseDouble(newAmountStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Số tiền không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = DatabaseConnected.editFee(feeId, newFeeName, newDescription, newAmount);

        if (success) {
            Object[] updatedFee = feesWindow.data.get(editIndex);

            updatedFee[2] = newFeeName;
            updatedFee[3] = newAmount;
            updatedFee[4] = newDescription;

            Timestamp createdAt = new Timestamp(System.currentTimeMillis());
            updatedFee[6] = createdAt;

            feesWindow.updateTable();
            dispose();
        } else {
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi trong quá trình lưu phí.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatCurrency(Double amount) {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(0);
        return numberFormat.format(amount) + " đ";
    }
}
