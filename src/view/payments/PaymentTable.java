package view.payments;

import model.Fee;
import model.Payment;
import model.Resident;
import view.table.TableHeader;
import util.ImageLoader;
import model.Household;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static controller.PaymentDAO.deletePayment;
import static controller.PaymentDAO.getPaymentById;

public class PaymentTable extends JPanel {
    PaymentWindow paymentWindow;

    private ArrayList<Object[]> data;
    private String[] columnNames;
    private int columnX[];
    private BufferedImage viewImage;
    private BufferedImage editImage;
    private BufferedImage deleteImage;

    public PaymentTable(ArrayList<Object[]> data, String[] columnNames, PaymentWindow paymentWindow) {
        this.paymentWindow = paymentWindow;

        this.data = data;
        this.columnNames = columnNames;

        columnX = PaymentWindow.getColumnX();
        loadImages();
        setLayout(null);
        createButtons();
    }

    private void loadImages() {
        viewImage = ImageLoader.loadImage("/img/view.png", 30, 30);
        editImage = ImageLoader.loadImage("/img/edit.png", 30, 30);
        deleteImage = ImageLoader.loadImage("/img/delete.png", 30, 30);
    }

    private void createButtons() {
        int rowHeight = 50;

        for (int i = 0; i < data.size(); i++) {
            // Tạo nút "Chỉnh sửa"
            JButton editButton = new JButton(new ImageIcon(editImage));
            editButton.setBounds(columnX[5], (i + 1) * rowHeight + 10, 30, 30);
            add(editButton);

            // Add ActionListener to the view button
            int rowIndex = i;

            // Thêm ActionListener cho nút chỉnh sửa
            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editRow(((Household) data.get(rowIndex)[2]).id, ((Fee) data.get(rowIndex)[3]).id);
                }
            });

            // Tạo nút "Xóa"
            JButton deleteButton = new JButton(new ImageIcon(deleteImage));
            deleteButton.setBounds(columnX[6], (i + 1) * rowHeight + 10, 30, 30);
            add(deleteButton);

            // Thêm ActionListener cho nút xóa
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deleteRow(((Household) data.get(rowIndex)[2]).id, ((Fee) data.get(rowIndex)[3]).id);
                }
            });
        }
    }

    public void updateTableData(ArrayList<Object[]> newData) {
        this.data = newData;
        removeAll();
        createButtons();
        revalidate();
        repaint();
    }

    private void editRow(int houseHoldId, int feeId) {
        PaymentPopup popup = new PaymentPopup(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                PaymentPopup.EDIT_PAYMENT,
                paymentWindow,
                houseHoldId,
                feeId
        );
        popup.setVisible(true);
    }

    private void deleteRow(int houseHoldId, int feeId) {
        int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa mục này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = deletePayment(houseHoldId, feeId);

            if (success) {
                paymentWindow.resetData();

                JOptionPane.showMessageDialog(null, "Xóa thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi trong quá trình xóa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void drawTable(Graphics g) {
        int rowHeight = 50;
        int[] colWidth = {50, 200, 165, 165, 130, 70, 70};
        int totalHeight = (data.size() + 1) * rowHeight;

        // Vẽ tiêu đề
        TableHeader.drawHeaderPayment(g, columnNames, colWidth, getWidth());

        // Vẽ các hàng
        drawRow(g);

        // Vẽ các đường ngang
        for (int i = 0; i <= data.size(); i++) {
            int y = i * rowHeight;
            g.drawLine(0, y, getWidth(), y);
        }

        // Vẽ các đường dọc và viền dưới
        int x = 0;
        for (int width : colWidth) {
            x += width;
            g.drawLine(x, 0, x, totalHeight);
        }

        // Vẽ viền dưới của bảng
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, getWidth(), totalHeight);
    }

    private void drawRow(Graphics g) {
        int rowHeight = 50;

        for (int i = 0; i < data.size(); i++) {
            Object[] rowData = data.get(i);

            // Vẽ chỉ số hàng (STT)
            g.drawString(String.valueOf(rowData[0]), columnX[0], (i + 1) * rowHeight + 30);

            // Vẽ tên chủ hộ
            Household household = (Household) rowData[2];
            Resident resident = household.head_of_household;
            g.drawString(resident.full_name, columnX[1], (i + 1) * rowHeight + 30);

            // Vẽ số tiền phải nộp
            g.drawString(String.valueOf(rowData[4]), columnX[2], (i + 1) * rowHeight + 30);

            // Vẽ tổng số tiền đã nộp
            ArrayList<Payment> payments = (ArrayList<Payment>) rowData[9];
            double totalAmountPaid = 0.0;

            // Tính tổng số tiền đã nộp
            for (Payment payment : payments) {
                totalAmountPaid += payment.payment_amount;
            }

            // Vẽ tổng số tiền lên giao diện
            g.drawString(formatCurrency(totalAmountPaid), columnX[3], (i + 1) * rowHeight + 30);


            // Vẽ hạn nộp
            g.drawString(formatDate((Date) rowData[5]), columnX[4], (i + 1) * rowHeight + 30);
        }

        if (data.isEmpty()) {
            g.setFont(getFont().deriveFont(Font.BOLD, 20f));
            g.drawString("Hiện tại chưa có dữ liệu ở bảng này", 250, 300);
        }
    }

    private String formatCurrency(Double amount) {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(0);
        return numberFormat.format(amount);
    }

    private String formatDate(Date date) {
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy/MM/dd");
        return outputFormat.format(date);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawTable(g);
    }
}