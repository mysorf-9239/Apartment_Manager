package view.table;

import view.window.ResidentsWindow;
import util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

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
    }

    private void loadImages() {
        editImage = ImageLoader.loadImage("/img/edit.png", 30, 30);
        deleteImage = ImageLoader.loadImage("/img/delete.png", 30, 30);
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
                // Prepare cell value
                String cellValue = (data[i][j] != null) ? data[i][j].toString() : "";

                // Set the X position based on column
                int cellXPosition = columnX[j];

                // Draw text for first two columns
                if (j < 2) {
                    g.drawString(cellValue, cellXPosition, (i + 2) * rowHeight - 20);
                }

                // Draw images for edit and delete columns
                if (j == 2 && editImage != null) {
                    g.drawImage(editImage, cellXPosition - 20, (i + 1) * rowHeight + 10, this);
                }
                if (j == 3 && deleteImage != null) {
                    g.drawImage(deleteImage, cellXPosition - 20, (i + 1) * rowHeight + 10, this);
                }
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(859, 600);
    }
}
