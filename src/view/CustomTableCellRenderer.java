package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CustomTableCellRenderer extends DefaultTableCellRenderer {
    private BufferedImage editImage;
    private BufferedImage deleteImage;

    public CustomTableCellRenderer() {
        this.editImage = editImage;
        this.deleteImage = deleteImage;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (column == 2) { // Edit column
            JLabel label = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (editImage != null) {
                        g.drawImage(editImage, 0, 0, 30, 30, this);
                    }
                }
            };
            label.setPreferredSize(new Dimension(70, 50));
            return label;
        } else if (column == 3) { // Delete column
            JLabel label = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (deleteImage != null) {
                        g.drawImage(deleteImage, 0, 0, 30, 30, this);
                    }
                }
            };
            label.setPreferredSize(new Dimension(70, 50));
            return label;
        } else {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}
