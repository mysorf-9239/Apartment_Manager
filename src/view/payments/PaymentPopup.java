package view.payments;

import controller.DatabaseConnected;
import model.Household;
import model.Payment;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import static controller.DatabaseConnected.addPayment;

public class PaymentPopup extends JDialog {
    public PaymentWindow paymentWindow;

    private JTextField householdNameField;
    private JTextField cccdField;
    private JTextField amountField;
    private JComboBox<String> feeDropdown;
    private JComboBox<String> paymentMethodDropdown;
    private JTextField statusField;

    private JTextField paymentNameField;

    public static final int VIEW_PAYMENT = 1;
    public static final int ADD_PAYMENT = 2;
    public static final int EDIT_PAYMENT = 3;

    ArrayList<Object[]> feesData;

    public PaymentPopup(JFrame parent, int popupName, PaymentWindow paymentWindow, int editIndex) {
        super(parent, "Popup", true);
        setUndecorated(true);
        setSize((int)(parent.getWidth() * 0.4), (int)(parent.getHeight() * 0.8));
        setLocationRelativeTo(parent);

        this.paymentWindow = paymentWindow;

        JPanel popupPanel = new JPanel();
        popupPanel.setLayout(null);
        popupPanel.setBackground(Color.WHITE);

        switch (popupName) {
            case VIEW_PAYMENT:
                viewPaymentContent(popupPanel, editIndex);
                break;
            case ADD_PAYMENT:
                addPaymentContent(popupPanel);
                break;
            case EDIT_PAYMENT:
                editPaymentContent(popupPanel, editIndex);
                break;
            default:
                throw new IllegalArgumentException("Invalid popup type");
        }

        setContentPane(popupPanel);
    }

    private void viewPaymentContent(JPanel panel, int viewIndex) {
        panel.setLayout(null);

        Object[] oldData = paymentWindow.data.get(viewIndex);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(null);
        headerPanel.setBounds(0, 0, getWidth(), getHeight() / 10);
        headerPanel.setBackground(Color.LIGHT_GRAY);

        JLabel titleLabel = new JLabel("Xem khoản phí", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(0, 0, getWidth(), headerPanel.getHeight());
        headerPanel.add(titleLabel);
        panel.add(headerPanel);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null);
        contentPanel.setBounds(0, headerPanel.getHeight(), getWidth(), getHeight() * 4 / 5);
        contentPanel.setBackground(Color.WHITE);

        // Thông tin của chủ hộ
        JLabel householdNameLabel = new JLabel("Họ và tên:");
        householdNameLabel.setBounds(20, 30, 100, 40);
        contentPanel.add(householdNameLabel);

        householdNameField = new JTextField();
        householdNameField.setBounds(130, 30, 200, 40);
        householdNameField.setEditable(false); // Chỉ cho phép xem
        contentPanel.add(householdNameField);
        // Hiển thị giá trị cũ cho chủ hộ
        householdNameField.setText(((Household) oldData[2]).head_of_household.full_name);

        // CCCD
        JLabel cccdLabel = new JLabel("CCCD:");
        cccdLabel.setBounds(20, 90, 100, 40);
        contentPanel.add(cccdLabel);

        cccdField = new JTextField();
        cccdField.setBounds(130, 90, 200, 40);
        cccdField.setEditable(false); // Chỉ cho phép xem
        contentPanel.add(cccdField);
        // Hiển thị giá trị cũ cho CCCD
        cccdField.setText(((Household) oldData[2]).head_of_household.idCard);

        // Thông tin về khoản phí
        JLabel feeNameLabel = new JLabel("Khoản phí:");
        feeNameLabel.setBounds(20, 150, 100, 40);
        contentPanel.add(feeNameLabel);

        // Dropdown cho khoản phí
        feeDropdown = new JComboBox<>();
        feesData = DatabaseConnected.getFeesDropdown();
        String[] feeNames = feesData.stream().map(data -> data[1].toString()).toArray(String[]::new);
        feeDropdown = new JComboBox<>(feeNames);
        feeDropdown.setBounds(130, 150, 200, 40);
        contentPanel.add(feeDropdown);

        // Hiển thị giá trị cũ cho khoản phí
        String oldFeeName = oldData[3].toString();
        feeDropdown.setSelectedItem(oldFeeName);

        // Số tiền thu
        JLabel amountLabel = new JLabel("Số tiền đã thanh toán:");
        amountLabel.setBounds(20, 210, 100, 40);
        contentPanel.add(amountLabel);

        amountField = new JTextField();
        amountField.setBounds(130, 210, 200, 40);
        amountField.setEditable(false);
        contentPanel.add(amountField);
        // Hiển thị giá trị cũ cho số tiền
        ArrayList<Payment> payments = (ArrayList<Payment>) oldData[9];
        double totalAmountPaid = 0.0;
        // Tính tổng số tiền đã nộp
        for (Payment payment : payments) {
            totalAmountPaid += payment.payment_amount;
        }
        amountField.setText(String.valueOf(totalAmountPaid));

        // Phương thức thanh toán
        JLabel paymentMethodLabel = new JLabel("Trạng thái:");
        paymentMethodLabel.setBounds(20, 270, 200, 40);
        contentPanel.add(paymentMethodLabel);

        statusField = new JTextField();
        statusField.setBounds(130, 270, 200, 40);
        statusField.setEditable(false); // Chỉ cho phép xem
        contentPanel.add(statusField);
        // Hiển thị giá trị cũ
        statusField.setText(oldData[6].toString());

        contentPanel.add(statusField);


        panel.add(contentPanel);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new GridLayout(1, 2));
        footerPanel.setBounds(0, headerPanel.getHeight() + contentPanel.getHeight(), getWidth(), getHeight() / 10);
        footerPanel.setBackground(Color.LIGHT_GRAY);

        // Nút Hủy
        JButton cancelButton = new JButton("Hủy");
        cancelButton.setPreferredSize(new Dimension(60, 40));
        cancelButton.addActionListener(e -> dispose());
        footerPanel.add(cancelButton);

        panel.add(footerPanel);
    }

    private void addPaymentContent(JPanel panel) {
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

        // Thông tin của chủ hộ
        // Tên (Họ và tên)
        JLabel householdNameLabel = new JLabel("Họ và tên:");
        householdNameLabel.setBounds(20, 30, 100, 40);
        contentPanel.add(householdNameLabel);

        householdNameField = new JTextField();
        householdNameField.setBounds(130, 30, 200, 40);
        contentPanel.add(householdNameField);

        // Gợi ý cho Họ và tên
        JLabel nameHintLabel = new JLabel("Họ và tên đầy đủ");
        nameHintLabel.setBounds(25 + getWidth() / 4, 70, 300, 20);
        nameHintLabel.setForeground(Color.GRAY);
        contentPanel.add(nameHintLabel);

        // CCCD
        JLabel cccdLabel = new JLabel("CCCD:");
        cccdLabel.setBounds(20, 90, 100, 40);
        contentPanel.add(cccdLabel);

        cccdField = new JTextField();
        cccdField.setBounds(130, 90, 200, 40);
        contentPanel.add(cccdField);

        // Gợi ý cho CCCD
        JLabel nameCCCDLabel = new JLabel("Căn cước công dân (12 số)");
        nameCCCDLabel.setBounds(25 + getWidth() / 4, 130, 300, 20);
        nameCCCDLabel.setForeground(Color.GRAY);
        contentPanel.add(nameCCCDLabel);

        // Thông tin về khoản phí
        // Khoản phí
        JLabel feeNameLabel = new JLabel("Khoản phí:");
        feeNameLabel.setBounds(20, 150, 100, 40);
        contentPanel.add(feeNameLabel);

        // Dropdown cho khoản phí
        feeDropdown = new JComboBox<>();
        feesData = DatabaseConnected.getFeesDropdown();
        String[] feeNames = feesData.stream().map(data -> data[1].toString()).toArray(String[]::new);
        feeDropdown = new JComboBox<>(feeNames);
        feeDropdown.setBounds(130, 150, 200, 40);
        contentPanel.add(feeDropdown);

        // Số tiền thu
        JLabel amountLabel = new JLabel("Số tiền:");
        amountLabel.setBounds(20, 210, 100, 40);
        contentPanel.add(amountLabel);

        amountField = new JTextField();
        amountField.setBounds(130, 210, 200, 40);
        contentPanel.add(amountField);

        // Gợi ý cho amount
        JLabel namePaymentLabel = new JLabel("Số tiền thu vào");
        namePaymentLabel.setBounds(25 + getWidth() / 4, 250, 300, 20);
        namePaymentLabel.setForeground(Color.GRAY);
        contentPanel.add(namePaymentLabel);

        // Phương thức thanh toán
        JLabel paymentMethodLabel = new JLabel("Phương thức thanh toán:");
        paymentMethodLabel.setBounds(20, 270, 200, 40);
        contentPanel.add(paymentMethodLabel);

        // Dropdown cho phương thức thanh toán
        paymentMethodDropdown = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "Thẻ"});
        paymentMethodDropdown.setBounds(250, 270, 150, 40);
        contentPanel.add(paymentMethodDropdown);

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
            // Gọi phương thức lưu sau khi người dùng nhấn nút Lưu
            saveAddPayment();
        });
        footerPanel.add(continueButton);

        // Nút Hủy
        JButton cancelButton = new JButton("Hủy");
        cancelButton.setPreferredSize(new Dimension(60, 40));
        cancelButton.addActionListener(e -> dispose());
        footerPanel.add(cancelButton);

        panel.add(footerPanel);
    }

    private void saveAddPayment() {
        String householdName = householdNameField.getText();
        String cccd = cccdField.getText();
        String selectedFeeName = feeDropdown.getSelectedItem().toString();
        String amountStr = amountField.getText();
        String paymentMethod = paymentMethodDropdown.getSelectedItem().toString();

        if (householdName.isEmpty() || cccd.isEmpty() || amountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Lấy household_id và kiểm tra nếu khoản phí có tồn tại
            int householdId = DatabaseConnected.getHouseholdIdByCCCD(householdName, cccd);
            int feeId = DatabaseConnected.getFeeIdByName(selectedFeeName);
            int paymentAmount = Integer.parseInt(amountStr);

            // Kiểm tra xem household_id có fee_id trong households_fees không
            if (!DatabaseConnected.checkHouseholdFeeExist(householdId, feeId)) {
                JOptionPane.showMessageDialog(this, "Hộ gia đình không có khoản phí này!", "Thông báo", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Thêm payment
            ArrayList<Object[]> newPayment = addPayment(householdId, feeId, paymentAmount, paymentMethod);

            if (!newPayment.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Thêm khoản phí thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm khoản phí thất bại!", "Thông báo", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi truy vấn cơ sở dữ liệu: " + e.getMessage(), "Thông báo", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền hợp lệ!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void editPaymentContent(JPanel panel, int editIndex) {
        panel.setLayout(null);

        Object[] oldData = paymentWindow.data.get(editIndex);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(null);
        headerPanel.setBounds(0, 0, getWidth(), getHeight() / 10);
        headerPanel.setBackground(Color.LIGHT_GRAY);

        JLabel titleLabel = new JLabel("Chỉnh sửa khoản phí", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(0, 0, getWidth(), headerPanel.getHeight());
        headerPanel.add(titleLabel);
        panel.add(headerPanel);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null);
        contentPanel.setBounds(0, headerPanel.getHeight(), getWidth(), getHeight() * 4 / 5);
        contentPanel.setBackground(Color.WHITE);

        // Thông tin của chủ hộ
        JLabel householdNameLabel = new JLabel("Chủ hộ:");
        householdNameLabel.setBounds(20, 30, 100, 40);
        contentPanel.add(householdNameLabel);

        householdNameField = new JTextField();
        householdNameField.setBounds(130, 30, 200, 40);
        householdNameField.setEditable(false);
        contentPanel.add(householdNameField);
        // Hiển thị giá trị cũ cho chủ hộ
        householdNameField.setText(((Household) oldData[2]).head_of_household.full_name);

        // Thông tin về khoản phí
        JLabel feeNameLabel = new JLabel("Khoản phí:");
        feeNameLabel.setBounds(20, 90, 100, 40);
        contentPanel.add(feeNameLabel);

        // Dropdown cho khoản phí
        feeDropdown = new JComboBox<>();
        feesData = DatabaseConnected.getFeesDropdown();
        String[] feeNames = feesData.stream().map(data -> data[1].toString()).toArray(String[]::new);
        feeDropdown = new JComboBox<>(feeNames);
        feeDropdown.setBounds(130, 90, 200, 40);
        contentPanel.add(feeDropdown);

        // Hiển thị giá trị cũ cho khoản phí
        String oldFeeName = oldData[3].toString();
        feeDropdown.setSelectedItem(oldFeeName);

        ArrayList<Payment> payments = (ArrayList<Payment>) oldData[9];

        // Bảng để hiển thị các lần thanh toán
        String[] paymentColumnNames = {"ID", "Số tiền", "Ngày thanh toán", "Phương thức", "Sửa"};
        DefaultTableModel paymentTableModel = new DefaultTableModel(paymentColumnNames, 0);

        // Thêm dữ liệu vào bảng
        for (Payment payment : payments) {
            Object[] rowData = {
                    payment.id,
                    payment.payment_amount,
                    payment.payment_date,
                    payment.payment_method,
                    "Sửa"
            };
            paymentTableModel.addRow(rowData);
        }

        // Tạo JTable và thêm vào contentPanel
        JTable paymentTable = new JTable(paymentTableModel);
        JScrollPane scrollPane = new JScrollPane(paymentTable);
        scrollPane.setBounds(20, 150, 400, 150);
        contentPanel.add(scrollPane);

        // Phần dùng để hiển thị phần nội dung để chỉnh sửa

        panel.add(contentPanel);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new GridLayout(1, 2));
        footerPanel.setBounds(0, headerPanel.getHeight() + contentPanel.getHeight(), getWidth(), getHeight() / 10);
        footerPanel.setBackground(Color.LIGHT_GRAY);

        // Nút Hủy
        JButton cancelButton = new JButton("Hủy");
        cancelButton.setPreferredSize(new Dimension(60, 40));
        cancelButton.addActionListener(e -> dispose());
        footerPanel.add(cancelButton);

        panel.add(footerPanel);
    }

    private void saveEditPayment(int paymentId) {

        System.out.println("Edit");
    }

    private String formatCurrency(Double amount) {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(0);
        return numberFormat.format(amount) + " đ";
    }
}
