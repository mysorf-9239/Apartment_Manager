package view.window;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import controller.DatabaseConnected;
import util.ImageLoader;
import view.table.CustomTable;

public class ResidentsWindow extends JPanel {

    private static final int[] columnX = {15, 70, 750, 820};

    private BufferedImage searchImage;
    private JTextField searchField;
    private String[] columnNames = {"STT", "Nội dung cơ bản", "Sửa", "Xóa"};
    private Object[][] data;

    public ResidentsWindow() {
        setLayout(null);
        loadImages(); // Load images

        // Title
        JLabel titleLabel = new JLabel("Quản lý nhân khẩu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(0, 0, 859, 30);
        add(titleLabel);

        // Search Box
        JPanel searchBox = new JPanel();
        searchBox.setBounds(0, 40, 859, 30);
        searchBox.setLayout(null);

        JLabel searchLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (searchImage != null) {
                    g.drawImage(searchImage, 0, 0, 30, 30, this);
                }
            }
        };
        searchLabel.setBounds(0, 0, 30, 30);
        searchBox.add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(40, 0, 744, 30);
        searchBox.add(searchField);

        add(searchBox);

        // Add Button
        JPanel addPanel = new JPanel();
        addPanel.setBounds(0, 65, 859, 30);
        addPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton addButton = new JButton("Thêm");
        addButton.setPreferredSize(new Dimension(70, 30));
        addButton.setFocusable(false);
        addPanel.add(addButton);

        add(addPanel);

        // Retrieve data from the database
        DatabaseConnected db = new DatabaseConnected();
        // Fetch data from database
        Object[][] data = db.getResidentsData();

        // Custom Table
        CustomTable customTable = new CustomTable(data, columnNames);
        customTable.setBounds(0, 100, 859, 550);
        add(customTable);

        // Pagination Panel
        JPanel paginationPanel = new JPanel();
        paginationPanel.setBounds(0, 650, 859, 40);
        paginationPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        paginationPanel.add(new JLabel("< 1 2 3 4 5 >"));
        paginationPanel.add(new JLabel("Chuyển đến trang:"));
        JTextField pageInput = new JTextField(5);
        paginationPanel.add(pageInput);
        add(paginationPanel);
    }


    private void loadImages() {
        searchImage = ImageLoader.loadImage("/img/search.png", 30, 30);
    }

    public static int[] getColumnX() {
        return columnX;
    }
}
