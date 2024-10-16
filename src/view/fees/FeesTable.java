package view.fees;

import controller.DatabaseConnected;
import view.table.TableHeader;
import util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class FeesTable extends JPanel {
    FeesWindow feesWindow;

    private ArrayList<Object[]> data;
    private String[] columnNames;
    private int columnX[];
    private BufferedImage viewImage;
    private BufferedImage editImage;
    private BufferedImage deleteImage;

    public FeesTable(ArrayList<Object[]> data, String[] columnNames, FeesWindow feesWindow) {
        this.feesWindow = feesWindow;

        this.data = data;
        this.columnNames = columnNames;

        columnX = FeesWindow.getColumnX();
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
            // Create the "View" button
            JButton viewButton = new JButton(new ImageIcon(viewImage));
            viewButton.setBounds(columnX[5], (i + 1) * rowHeight + 10, 30, 30);
            add(viewButton);

            // Add ActionListener to the view button
            int rowIndex = i;
            viewButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    viewRow(rowIndex);
                }
            });

            // Tạo nút "Chỉnh sửa"
            JButton editButton = new JButton(new ImageIcon(editImage));
            editButton.setBounds(columnX[6], (i + 1) * rowHeight + 10, 30, 30);
            add(editButton);

            // Thêm ActionListener cho nút chỉnh sửa
            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editRow(rowIndex);
                }
            });

            // Tạo nút "Xóa"
            JButton deleteButton = new JButton(new ImageIcon(deleteImage));
            deleteButton.setBounds(columnX[7], (i + 1) * rowHeight + 10, 30, 30);
            add(deleteButton);

            // Thêm ActionListener cho nút xóa
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deleteRow(rowIndex);
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

    private void viewRow(int rowIndex) {
        int viewIndex = rowIndex + (feesWindow.currentPage - 1) * feesWindow.rowsPerPage + 1;

        System.out.println(viewIndex);

        FeesPopup popup = new FeesPopup(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                FeesPopup.VIEW_FEE,
                feesWindow,
                viewIndex
        );
        popup.setVisible(true);
    }

    private void editRow(int rowIndex) {
        int editIndex = rowIndex + (feesWindow.currentPage - 1) * feesWindow.rowsPerPage;

        FeesPopup popup = new FeesPopup(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                FeesPopup.EDIT_FEE,
                feesWindow,
                editIndex
        );
        popup.setVisible(true);
    }

    private void deleteRow(int rowIndex) {
        int deleteIndex = rowIndex + (feesWindow.currentPage - 1) * feesWindow.rowsPerPage;

        Object[] deleteData = feesWindow.data.get(deleteIndex);

        int feeID = Integer.parseInt(deleteData[1].toString());

        boolean success = DatabaseConnected.deleteFee(feeID);

        if (success) {
            feesWindow.data.remove(deleteIndex);
            System.out.println("Data deleted successfully.");

            feesWindow.updateTable();

            JOptionPane.showMessageDialog(null, "Xóa thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi trong quá trình xóa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void drawTable(Graphics g) {
        int rowHeight = 50;
        int[] colWidth = {50, 170, 100, 200, 120, 70, 70, 70};
        int totalHeight = (data.size() + 1) * rowHeight;

        // Vẽ tiêu đề
        TableHeader.drawHeaderFees(g, columnNames, colWidth, getWidth());

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

        // Vẽ dữ liệu hàng
        for (int i = 0; i < data.size(); i++) {
            Object[] rowData = data.get(i);

            // Vẽ chỉ số hàng (STT)
            g.drawString(String.valueOf(rowData[0]), columnX[0], (i + 1) * rowHeight + 30);

            // Vẽ nội dung hàng (Tên phí)
            g.drawString((String) rowData[2], columnX[1], (i + 1) * rowHeight + 30);

            // Vẽ số tiền
            Double amount = (Double) rowData[3];
            String formattedAmount = formatCurrency(amount);
            g.drawString(formattedAmount, columnX[2], (i + 1) * rowHeight + 30);

            // Vẽ mô tả
            g.drawString((String) rowData[4], columnX[3], (i + 1) * rowHeight + 30);

            // Vẽ ngày tạo
            String createdAt = rowData[5].toString();
            String formattedDate = formatDate(createdAt);
            g.drawString(formattedDate, columnX[4], (i + 1) * rowHeight + 30);
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
        return numberFormat.format(amount) + " đ";
    }

    private String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = inputFormat.parse(dateString);

            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy/MM/dd");
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawTable(g);
    }
}
