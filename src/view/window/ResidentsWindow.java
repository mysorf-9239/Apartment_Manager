package view.window;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import controller.DatabaseConnected;
import util.CustomPopup;
import util.ImageLoader;
import view.table.CustomTable;

public class ResidentsWindow extends JPanel {
    private static final int[] columnX = {15, 70, 730, 800};
    private BufferedImage searchImage;
    private JTextField searchField;
    private String[] columnNames = {"STT", "Nội dung cơ bản", "Sửa", "Xóa"};
    private ArrayList<Object[]> data;
    private ArrayList<Object[]> currentPageData;
    private int currentPage = 1;
    private int rowsPerPage = 10;
    private int totalPages;
    private CustomTable customTable;
    private JPanel paginationPanel;

    public ResidentsWindow() {
        setLayout(null);
        loadImages();

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
        addButton.addActionListener(e -> {
            CustomPopup popup = new CustomPopup((JFrame) SwingUtilities.getWindowAncestor(this));
            popup.show();
        });
        addPanel.add(addButton);

        add(addPanel);

        // Retrieve data from the database
        DatabaseConnected db = new DatabaseConnected();
        data = db.getResidentsData(); // Fetch data from database

        // Calculate total pages
        totalPages = (int) Math.ceil((double) data.size() / rowsPerPage);
        currentPageData = getPageData(currentPage); // Lấy dữ liệu cho trang đầu tiên

        // Custom Table
        customTable = new CustomTable(currentPageData, columnNames);
        customTable.setBounds(0, 100, 859, 550);
        add(customTable);

        // Pagination Panel
        paginationPanel = new JPanel();
        paginationPanel.setBounds(0, 650, 859, 40);
        paginationPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        add(paginationPanel);

        updatePagination();
    }

    private void loadImages() {
        searchImage = ImageLoader.loadImage("/img/search.png", 30, 30);
    }

    // Lấy dữ liệu cho trang hiện tại
    private ArrayList<Object[]> getPageData(int page) {
        int start = (page - 1) * rowsPerPage;
        int end = Math.min(start + rowsPerPage, data.size());
        return new ArrayList<>(data.subList(start, end));
    }

    // Cập nhật bảng và phân trang
    private void updatePagination() {
        paginationPanel.removeAll();

        // Nút < (trang trước)
        JButton prevButton = new JButton("<");
        prevButton.setEnabled(currentPage > 1);
        prevButton.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                updateTable();
            }
        });
        paginationPanel.add(prevButton);

        // Hiển thị các nút số trang
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, currentPage + 2);

        if (startPage > 1) {
            paginationPanel.add(new JLabel("..."));
        }

        for (int i = startPage; i <= endPage; i++) {
            JButton pageButton = new JButton(String.valueOf(i));
            pageButton.setEnabled(i != currentPage);
            pageButton.addActionListener(e -> {
                currentPage = Integer.parseInt(pageButton.getText());
                updateTable();
            });
            paginationPanel.add(pageButton);
        }

        if (endPage < totalPages) {
            paginationPanel.add(new JLabel("..."));
        }

        // Nút > (trang sau)
        JButton nextButton = new JButton(">");
        nextButton.setEnabled(currentPage < totalPages);
        nextButton.addActionListener(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                updateTable();
            }
        });
        paginationPanel.add(nextButton);

        paginationPanel.revalidate();
        paginationPanel.repaint();
    }

    // Cập nhật bảng dữ liệu và phân trang khi chuyển trang
    private void updateTable() {
        currentPageData = getPageData(currentPage);
        customTable.updateTableData(currentPageData);
        updatePagination();
    }

    public static int[] getColumnX() {
        return columnX;
    }
}
