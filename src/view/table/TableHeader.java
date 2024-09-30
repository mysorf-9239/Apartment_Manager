package view.table;

import java.awt.*;

public class TableHeader {

    static int[] columnX1 = {13, 350, 735, 805};
    static int[] columnX2 = {13, 250, 590, 735, 805};


    public static void drawHeaderResident(Graphics g, String[] columnNames, int[] colWidth, int tableWidth) {
        int rowHeight = 50;

        // Draw header background
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, tableWidth, rowHeight);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, tableWidth, rowHeight);

        // Draw header text
        for (int i = 0; i < columnNames.length; i++) {
            g.drawString(columnNames[i], columnX1[i], 30);
        }
    }

    public static void drawHeaderHousehold(Graphics g, String[] columnNames, int[] colWidth, int tableWidth) {
        int rowHeight = 50;

        // Draw header background
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, tableWidth, rowHeight);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, tableWidth, rowHeight);

        // Draw header text
        for (int i = 0; i < columnNames.length; i++) {
            g.drawString(columnNames[i], columnX2[i], 30);
        }
    }
}
