package view.households;

import view.table.TableHeader;
import util.ImageLoader;
import model.Resident;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static controller.HouseholdDAO.deleteHousehold;

public class HouseholdTable extends JPanel {
    HouseholdsWindow householdsWindow;

    private ArrayList<Object[]> data;
    private final String[] columnNames;
    private final int[] columnXHousehold;
    private BufferedImage viewImage;
    private BufferedImage editImage;
    private BufferedImage deleteImage;

    public HouseholdTable(ArrayList<Object[]> data, String[] columnNames, HouseholdsWindow householdsWindow) {
        this.householdsWindow = householdsWindow;
        this.data = data;
        this.columnNames = columnNames;
        columnXHousehold = HouseholdsWindow.getColumnXHousehold();
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
            viewButton.setBounds(columnXHousehold[4], (i + 1) * rowHeight + 10, 30, 30);
            add(viewButton);

            // Add ActionListener to the view button
            int rowIndex = i;
            viewButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    viewRow((Integer) data.get(rowIndex)[1]);
                }
            });

            // Create the "Edit" button
            JButton editButton = new JButton(new ImageIcon(editImage));
            editButton.setBounds(columnXHousehold[5], (i + 1) * rowHeight + 10, 30, 30);
            add(editButton);

            // Add ActionListener to the edit button
            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editRow((Integer) data.get(rowIndex)[1]);
                }
            });

            // Create the "Delete" button
            JButton deleteButton = new JButton(new ImageIcon(deleteImage));
            deleteButton.setBounds(columnXHousehold[6], (i + 1) * rowHeight + 10, 30, 30);
            add(deleteButton);

            // Add ActionListener to the delete button
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deleteRow((Integer) data.get(rowIndex)[1]);
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

    private void viewRow(int viewIndex) {
        HouseholdPopup popup = new HouseholdPopup(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                HouseholdPopup.VIEW_HOUSEHOLD,
                householdsWindow,
                viewIndex
        );
        popup.setVisible(true);
    }

    private void editRow(int editIndex) {
        HouseholdPopup popup = new HouseholdPopup(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                HouseholdPopup.EDIT_HOUSEHOLD,
                householdsWindow,
                editIndex
        );
        popup.setVisible(true);
    }

    private void deleteRow(int deleteIndex) {
        // Xác nhận trước khi xóa
        int confirmation = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa hộ khẩu này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            // Xóa khỏi cơ sở dữ liệu
            boolean success = deleteHousehold(deleteIndex);

            if (success) {
                // Cập nhật bảng
                householdsWindow.resetData();

                // Hiển thị thông báo thành công
                JOptionPane.showMessageDialog(null, "Xóa thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi trong quá trình xóa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void drawTable(Graphics g) {
        int rowHeight = 50;
        int[] colWidth = {50, 300, 180, 110, 70, 70, 70};
        int totalHeight = (data.size() + 1) * rowHeight;

        // Draw header
        TableHeader.drawHeaderHousehold(g, columnNames, colWidth, getWidth());

        // Draw rows
        drawRow(g);

        // Draw horizontal lines
        for (int i = 0; i <= data.size(); i++) {
            int y = i * rowHeight;
            g.drawLine(0, y, getWidth(), y);
        }

        // Draw vertical lines and bottom border
        int x = 0;
        for (int width : colWidth) {
            x += width;
            g.drawLine(x, 0, x, totalHeight);
        }

        // Draw the bottom border of the table
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, getWidth(), totalHeight);
    }

    private void drawRow(Graphics g) {
        int rowHeight = 50;

        for (int i = 0; i < data.size(); i++) {
            Object[] rowData = data.get(i);

            // Draw row index (STT)
            g.drawString(String.valueOf(rowData[0]), columnXHousehold[0], (i + 1) * rowHeight + 30);

            // Draw row content (Địa chỉ)
            g.drawString((String) rowData[2], columnXHousehold[1], (i + 1) * rowHeight + 30);

            // Draw head of household
            Resident headOfHousehold = (Resident) rowData[4];
            g.drawString(headOfHousehold.full_name, columnXHousehold[2], (i + 1) * rowHeight + 30);

            // Draw row acreage (m2)
            g.drawString((rowData[3]).toString(), columnXHousehold[3], (i + 1) * rowHeight + 30);
        }

        if (data.isEmpty()) {
            g.setFont(getFont().deriveFont(Font.BOLD, 20f));
            g.drawString("Hiện tại chưa có dữ liệu ở bảng này", 250, 300);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawTable(g);
    }
}
