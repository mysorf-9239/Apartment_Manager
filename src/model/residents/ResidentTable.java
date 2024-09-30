package model.residents;

import controller.DatabaseConnected;
import view.table.TableHeader;
import util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ResidentTable extends JPanel {
    ResidentsWindow residentsWindow;

    private ArrayList<Object[]> data;
    private String[] columnNames;
    private int columnX[];
    private BufferedImage editImage;
    private BufferedImage deleteImage;

    public ResidentTable(ArrayList<Object[]> data, String[] columnNames, ResidentsWindow residentsWindow) {
        this.residentsWindow = residentsWindow;

        this.data = data;
        this.columnNames = columnNames;

        columnX = ResidentsWindow.getColumnX();
        loadImages();
        setLayout(null);
        createButtons();
    }

    private void loadImages() {
        editImage = ImageLoader.loadImage("/img/edit.png", 30, 30);
        deleteImage = ImageLoader.loadImage("/img/delete.png", 30, 30);
    }

    private void createButtons() {
        int rowHeight = 50; // Adjust this based on your row height

        for (int i = 0; i < data.size(); i++) {
            // Create the "Edit" button
            JButton editButton = new JButton(new ImageIcon(editImage));
            editButton.setBounds(columnX[2], (i + 1) * rowHeight + 10, 30, 30);
            add(editButton);

            // Add ActionListener to the edit button
            int rowIndex = i; // Capture row index for use in the ActionListener
            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editRow(rowIndex);
                }
            });

            // Create the "Delete" button
            JButton deleteButton = new JButton(new ImageIcon(deleteImage));
            deleteButton.setBounds(columnX[3], (i + 1) * rowHeight + 10, 30, 30);
            add(deleteButton);

            // Add ActionListener to the delete button
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

    private void editRow(int rowIndex) {
        int editIndex = rowIndex + (residentsWindow.currentPage - 1) * 10;

        ResidentPopup popup = new ResidentPopup(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                ResidentPopup.EDIT_RESIDENT,
                residentsWindow,
                editIndex
        );
        popup.setVisible(true);
    }


    private void deleteRow(int rowIndex) {
        int deleteIndex = rowIndex + (residentsWindow.currentPage - 1) * 10;

        Object[] deleteData = residentsWindow.data.get(deleteIndex);

        int residentID = Integer.parseInt(deleteData[7].toString());

        // Xóa khỏi cơ sở dữ liệu
        boolean success = DatabaseConnected.deleteResident(residentID);

        if (success) {
            // Xóa dữ liệu khỏi ArrayList
            residentsWindow.data.remove(deleteIndex);
            System.out.println("Data deleted successfully.");

            // Cập nhật bảng
            residentsWindow.updateTable();

            JOptionPane.showMessageDialog(null, "Xóa thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi trong quá trình xóa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void drawTable(Graphics g) {
        int rowHeight = 50;
        int[] colWidth = {50, 660, 70, 70};
        int totalHeight = (data.size() + 1) * rowHeight;

        // Draw header
        TableHeader.drawHeaderResident(g, columnNames, colWidth, getWidth());

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
            g.drawString(String.valueOf(rowData[0]), columnX[0], (i + 1) * rowHeight + 30);

            // Draw row content (Nội dung cơ bản)
            g.drawString((String) rowData[1], columnX[1], (i + 1) * rowHeight + 30);

            // Draw date
            g.drawString((String) rowData[2], columnX[1] + 200, (i + 1) * rowHeight + 30);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawTable(g);
    }
}
