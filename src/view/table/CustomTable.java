package view.table;

import view.window.ResidentsWindow;
import util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomTable extends JPanel {
    private Object[][] data;
    private String[] columnNames;
    private int columnX[];
    private BufferedImage editImage;
    private BufferedImage deleteImage;

    public CustomTable(Object[][] data, String[] columnNames) {
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

        for (int i = 0; i < data.length; i++) {
            // Create the "Edit" button
            JButton editButton = new JButton(new ImageIcon(editImage));
            editButton.setBounds(columnX[2] - 20, (i + 1) * rowHeight + 10, 30, 30);
            add(editButton);

            // Add ActionListener to the edit button
            int rowIndex = i; // Capture row index for use in the ActionListener
            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Editing row: " + rowIndex);
                    // Implement edit action for the specific row
                    editRow(rowIndex);
                }
            });

            // Create the "Delete" button
            JButton deleteButton = new JButton(new ImageIcon(deleteImage));
            deleteButton.setBounds(columnX[3] - 20, (i + 1) * rowHeight + 10, 30, 30);
            add(deleteButton);

            // Add ActionListener to the delete button
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Deleting row: " + rowIndex);
                    // Implement delete action for the specific row
                    deleteRow(rowIndex);
                }
            });
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawTable(g);
    }

    private void drawTable(Graphics g) {
        int rowHeight = 50;
        int[] colWidth = {50, 660, 70, 70};
        int totalHeight = (data.length + 1) * rowHeight;

        // Draw header
        TableHeader.drawHeader(g, columnNames, colWidth, getWidth());

        // Draw rows
        drawRow(g);

        // Draw horizontal lines
        for (int i = 0; i <= data.length; i++) {
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
        int rowHeight = 50; // Height for each row

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < columnNames.length; j++) {
                String cellValue = (data[i][j] != null) ? data[i][j].toString() : "";
                int cellXPosition = columnX[j];

                if (j < 2) {
                    g.drawString(cellValue, cellXPosition, (i + 2) * rowHeight - 20);
                }
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(859, 600);
    }

    private void editRow(int rowIndex) {
        // Here you can implement the edit logic, e.g., open a new dialog to edit row data
        System.out.println("Editing data for row: " + rowIndex + " with content: " + data[rowIndex][1]);
    }

    private void deleteRow(int rowIndex) {
        // Here you can implement the delete logic, e.g., remove from the database and refresh the table
        System.out.println("Deleting data for row: " + rowIndex + " with content: " + data[rowIndex][1]);
    }
}
