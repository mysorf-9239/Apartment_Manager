package view.residents;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import controller.DatabaseConnection;
import util.ImageLoader;

import static controller.ResidentDAO.getResidentsData;

public class ResidentsWindow extends JPanel {
    private static final int[] columnX = {15, 80, 400, 510, 590, 730, 800};
    private BufferedImage searchImage;
    private JTextField searchField;
    private String[] columnNames = {"STT", "Họ và tên", "Ngày sinh", "Giới tính", "CCCD", "Sửa", "Xóa"};

    public ArrayList<Object[]> data;
    public ArrayList<Object[]> currentPageData;

    public int currentPage = 1;
    private int rowsPerPage = 10;
    private int totalPages;
    private ResidentTable residentTable;
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
        searchField.addActionListener(e -> performSearch());
        searchField.setText("Nhập từ khoá (tên hoặc CCCD) và Enter để tìm kiếm");
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("Nhập từ khoá (tên hoặc CCCD) và Enter để tìm kiếm")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Nhập từ khoá (tên hoặc CCCD) và Enter để tìm kiếm");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        searchBox.add(searchField);

        add(searchBox);

        // Ô reset
        JButton resetButton = new JButton("↻");
        resetButton.addActionListener(e -> resetData());
        resetButton.setFocusPainted(false);
        resetButton.setFocusable(false);
        resetButton.setBounds(800, 0, 30, 30);
        add(resetButton);

        // Add Button
        JPanel addPanel = new JPanel();
        addPanel.setBounds(0, 65, 859, 30);
        addPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton addButton = new JButton("Thêm");
        addButton.setPreferredSize(new Dimension(70, 30));
        addButton.setFocusable(false);
        addButton.addActionListener(e -> {
            ResidentPopup popup = new ResidentPopup((JFrame) SwingUtilities.getWindowAncestor(this), ResidentPopup.ADD_RESIDENT, this, -1);
            popup.show();
        });
        addPanel.add(addButton);

        add(addPanel);

        // Retrieve data from the database
        data = getResidentsData();

        // Calculate total pages
        totalPages = (int) Math.ceil((double) data.size() / rowsPerPage);
        currentPageData = getPageData(currentPage);

        // Custom Table
        residentTable = new ResidentTable(currentPageData, columnNames, this);
        residentTable.setBounds(0, 100, 859, 550);
        add(residentTable);

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

        // Nút Trước (trang trước)
        JButton previousButton = new JButton("Trước");
        previousButton.setEnabled(currentPage > 1);
        previousButton.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                updateTable();
            }
        });
        paginationPanel.add(previousButton);

        // Nhãn hiển thị số trang
        JLabel pageLabel = new JLabel("Trang " + currentPage + " / " + totalPages);
        paginationPanel.add(pageLabel);

        // Nút Tiếp theo (trang sau)
        JButton nextButton = new JButton("Tiếp theo");
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
    public void updateTable() {
        currentPageData = getPageData(currentPage);
        residentTable.updateTableData(currentPageData);
        updatePagination();
    }

    public void resetData() {
        data = getResidentsData();
        totalPages = (int) Math.ceil((double) data.size() / rowsPerPage);
        currentPage = 1;
        updateTable();
    }

    public static int[] getColumnX() {
        return columnX;
    }

    // Phương thức tìm kiếm
    private void performSearch() {
        String keyword = searchField.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            // Hiển thị toàn bộ dữ liệu nếu không có từ khóa
            currentPage = 1;
            totalPages = (int) Math.ceil((double) data.size() / rowsPerPage);
            currentPageData = getPageData(currentPage);
        } else {
            // Lọc dữ liệu dựa trên từ khóa
            ArrayList<Object[]> filteredData = new ArrayList<>();
            for (Object[] resident : data) {
                // Tìm kiếm trong tên, CCCD hoặc bất kỳ cột nào bạn muốn
                if (resident[1].toString().toLowerCase().contains(keyword) ||
                        resident[4].toString().toLowerCase().contains(keyword)) {
                    filteredData.add(resident);
                }
            }

            // Cập nhật dữ liệu hiện tại và phân trang
            currentPage = 1;
            totalPages = (int) Math.ceil((double) filteredData.size() / rowsPerPage);
            currentPageData = filteredData.size() > 0 ? new ArrayList<>(filteredData.subList(0, Math.min(rowsPerPage, filteredData.size()))) : new ArrayList<>();
        }

        // Cập nhật bảng và phân trang
        residentTable.updateTableData(currentPageData);
        updatePagination();
    }
}
