package model.payments;

import controller.DatabaseConnected;

import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.Locale;

public class PaymentPopup extends JDialog {
    public PaymentWindow paymentWindow;

    private JTextField paymentNameField;
    private JTextField amountField;
    private JTextField descriptionField;

    public static final int VIEW_PAYMENT = 1;
    public static final int ADD_PAYMENT = 2;
    public static final int EDIT_PAYMENT = 3;

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

        JLabel feeNameLabel = new JLabel("Tên Khoản Phí:");
        feeNameLabel.setBounds(20, 50, 100, 30);
        panel.add(feeNameLabel);

        paymentNameField = new JTextField();
        paymentNameField.setBounds(130, 50, 200, 30);
        panel.add(paymentNameField);

        JButton cancelButton = new JButton("Hủy");
        cancelButton.setBounds(220, 200, 100, 30);
        cancelButton.addActionListener(e -> dispose());
        panel.add(cancelButton);
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

        //Tên
        JLabel feeNameLabel = new JLabel("Tên khoản phí:");
        feeNameLabel.setBounds(20, 50, 100, 40);
        contentPanel.add(feeNameLabel);

        paymentNameField = new JTextField();
        paymentNameField.setBounds(130, 50, 200, 40);
        contentPanel.add(paymentNameField);

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

        panel.add(contentPanel);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new GridLayout(1, 2));
        footerPanel.setBounds(0, headerPanel.getHeight() + contentPanel.getHeight(), getWidth(), getHeight() / 10);
        footerPanel.setBackground(Color.LIGHT_GRAY);

        // Nút Lưu
        JButton continueButton = new JButton("Lưu");
        continueButton.setPreferredSize(new Dimension(60, 40));
        continueButton.addActionListener(e -> saveAddPayment());
        footerPanel.add(continueButton);

        // Nút Hủy
        JButton cancelButton = new JButton("Hủy");
        cancelButton.setPreferredSize(new Dimension(60, 40));
        cancelButton.addActionListener(e -> dispose());
        footerPanel.add(cancelButton);

        panel.add(footerPanel);
    }

    private void saveAddPayment() {
        String paymentName = paymentNameField.getText().trim();
        String paymentDescription = descriptionField.getText().trim();
        String amountStr = amountField.getText().trim();

        if (paymentName.isEmpty() || paymentDescription.isEmpty() || amountStr.isEmpty()) {
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

        int generatedId = DatabaseConnected.addFee(paymentName, paymentDescription, amount);

        if (generatedId != -1) {
            Object[] newFee = new Object[8];
            newFee[0] = String.format("%02d", paymentWindow.data.size() + 1);
            newFee[1] = generatedId;
            newFee[2] = paymentName;
            newFee[3] = amount;
            newFee[4] = paymentDescription;

            Timestamp createdAt = new Timestamp(System.currentTimeMillis());
            newFee[5] = createdAt;
            newFee[6] = createdAt;

            newFee[7] = "active";

            paymentWindow.data.add(newFee);
            paymentWindow.updateTable();
            dispose();
        } else {
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi trong quá trình lưu phí.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editPaymentContent(JPanel panel, int editIndex) {
        Object[] oldData = paymentWindow.data.get(editIndex);

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
            saveEditPayment(editIndex, feeId, feeNameField.getText(), descriptionField.getText(), amountField.getText());
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

    private void saveEditPayment(int editIndex, int feeId, String newFeeName, String newDescription, String newAmountStr) {
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
            Object[] updatedFee = paymentWindow.data.get(editIndex);

            updatedFee[2] = newFeeName;
            updatedFee[3] = newAmount;
            updatedFee[4] = newDescription;

            Timestamp createdAt = new Timestamp(System.currentTimeMillis());
            updatedFee[6] = createdAt;

            paymentWindow.updateTable();
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
