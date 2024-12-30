package view.households;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.stream.Collectors;

import controller.DatabaseConnection;
import model.Resident;
import util.ImageLoader;

import static controller.ResidentDAO.getHouseholdsData;

public class HouseholdsWindow extends JPanel {
    private static final int[] columnXHousehold = {15, 70, 380, 560, 660, 730, 800};
    private BufferedImage searchImage;
    private JTextField searchField;
    private String[] columnNames = {"STT", "Địa chỉ", "Chủ hộ", "Diện tích (m2)", "Xem", "Sửa", "Xóa"};

    public ArrayList<Object[]> data;
    public ArrayList<Object[]> currentPageData;

    public int currentPage = 1;
    private int rowsPerPage = 10;
    private int totalPages;
    private HouseholdTable householdTable;
    private JPanel paginationPanel;

    public HouseholdsWindow() {
        setLayout(null);
        loadImages();

        // Title
        JLabel titleLabel = new JLabel("Quản lý hộ gia đình", SwingConstants.CENTER);
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

        searchField = new JTextField("Nhập từ khoá (địa chỉ hoặc thông tin chủ hộ) và Enter để tìm kiếm");
        searchField.setForeground(Color.GRAY);
        searchField.setBounds(40, 0, 744, 30);

        // Placeholder logic
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Nhập từ khoá (địa chỉ hoặc thông tin chủ hộ) và Enter để tìm kiếm")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Nhập từ khoá (địa chỉ hoặc thông tin chủ hộ) và Enter để tìm kiếm");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        searchField.addActionListener(e -> filterTable());
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
            HouseholdPopup popup = new HouseholdPopup((JFrame) SwingUtilities.getWindowAncestor(this), HouseholdPopup.ADD_HOUSEHOLD, this, -1);
            popup.show();
        });
        addPanel.add(addButton);

        add(addPanel);

        // Retrieve data from the database
        data = getHouseholdsData();

        // Calculate total pages
        totalPages = (int) Math.ceil((double) data.size() / rowsPerPage);
        currentPageData = getPageData(currentPage);

        // Custom Table
        householdTable = new HouseholdTable(currentPageData, columnNames, this);
        householdTable.setBounds(0, 100, 859, 550);
        add(householdTable);

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

    // Cập nhật bảng
    public void updateTable() {
        currentPageData = getPageData(currentPage);
        householdTable.updateTableData(currentPageData);
        updatePagination();
    }

    public void resetData() {
        data = getHouseholdsData();
        totalPages = (int) Math.ceil((double) data.size() / rowsPerPage);
        currentPage = 1;
        updateTable();
    }

    // Cập nhật phân trang
    private void updatePagination() {
        paginationPanel.removeAll();

        JButton previousButton = new JButton("Trước");
        previousButton.setEnabled(currentPage > 1);
        previousButton.addActionListener(e -> {
            currentPage--;
            updateTable();
        });
        paginationPanel.add(previousButton);

        JLabel pageLabel = new JLabel("Trang " + currentPage + " / " + totalPages);
        paginationPanel.add(pageLabel);

        JButton nextButton = new JButton("Tiếp theo");
        nextButton.setEnabled(currentPage < totalPages);
        nextButton.addActionListener(e -> {
            currentPage++;
            updateTable();
        });
        paginationPanel.add(nextButton);

        paginationPanel.revalidate();
        paginationPanel.repaint();
    }

    public static int[] getColumnXHousehold() {
        return columnXHousehold;
    }

    private void filterTable() {
        String query = searchField.getText().trim().toLowerCase();

        if (query.isEmpty() || query.equals("Nhập từ khoá (địa chỉ hoặc thông tin chủ hộ) và Enter để tìm kiếm")) {
            data = getHouseholdsData();
        } else {
            data = (ArrayList<Object[]>) getHouseholdsData().stream()
                    .filter(row -> row[2].toString().toLowerCase().contains(query) || ((Resident) row[4]).full_name.toString().toLowerCase().contains(query) || ((Resident) row[3]).idCard.toString().toLowerCase().contains(query))
                    .collect(Collectors.toList());
        }

        currentPage = 1;
        totalPages = (int) Math.ceil((double) data.size() / rowsPerPage);
        updateTable();
    }
}
