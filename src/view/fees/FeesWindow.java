package view.fees;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.stream.Collectors;

import controller.DatabaseConnection;
import util.ImageLoader;

import static controller.FeeDAO.getFeesData;
import static controller.FeeDAO.getFeesDataByType;

public class FeesWindow extends JPanel {
    private static final int[] columnX = {15, 80, 200, 300, 473, 590, 660, 730, 800};
    private BufferedImage searchImage;
    private JTextField searchField;
    private String[] columnNames = {"STT", "Tên Khoản Phí", "Số Tiền", "Miêu tả", "Thời gian tạo", "Xem", "Thêm", "Sửa", "Xóa"};

    public ArrayList<Object[]> data;
    public ArrayList<Object[]> currentPageData;

    public int currentPage = 1;
    public int rowsPerPage = 10;
    private int totalPages;
    private FeesTable feesTable;
    private JPanel paginationPanel;

    public FeesWindow() {
        setLayout(null);
        loadImages();

        // Tiêu đề
        JLabel titleLabel = new JLabel("Quản lý khoản phí", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(0, 0, 859, 30);
        add(titleLabel);

        // Ô tìm kiếm
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

        searchField = new JTextField("Nhập từ khoá (tên khoản phí hoặc miêu tả) và Enter để tìm kiếm");
        searchField.setForeground(Color.GRAY);
        searchField.setBounds(40, 0, 744, 30);

        // Placeholder logic
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Nhập từ khoá (tên khoản phí hoặc miêu tả) và Enter để tìm kiếm")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Nhập từ khoá (tên khoản phí hoặc miêu tả) và Enter để tìm kiếm");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        searchField.addActionListener(e -> filterTable());
        searchBox.add(searchField);

        add(searchBox);

        // Nút thêm và dropdown chọn khoản phí
        JPanel addPanel = new JPanel();
        addPanel.setBounds(0, 65, 859, 30);
        addPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        // Dropdown chọn loại khoản phí
        String[] feeTypes = {"All", "Chung", "Riêng"};
        JComboBox<String> feeDropdown = new JComboBox<>(feeTypes);
        feeDropdown.setPreferredSize(new Dimension(150, 30));

        // Lấy dữ liệu từ cơ sở dữ liệu
        DatabaseConnection db = new DatabaseConnection();

        // Default
        data = getFeesData();

        // Xử lý sự kiện khi chọn loại khoản phí
        feeDropdown.addActionListener(e -> {
            String selectedType = (String) feeDropdown.getSelectedItem();
            String Type = "";
            if (selectedType.equals("All")) {
                Type = "AllF";
            } else if (selectedType.equals("Chung")) {
                Type = "all";
            } else if (selectedType.equals("Riêng")) {
                Type = "part";
            }

            data = getFeesDataByType(Type);
            currentPage = 1;
            totalPages = (int) Math.ceil((double) data.size() / rowsPerPage);
            updateTable();
        });
        addPanel.add(feeDropdown);

        // Nút thêm
        JButton addButton = new JButton("Thêm");
        addButton.setPreferredSize(new Dimension(70, 30));
        addButton.setFocusable(false);
        addButton.addActionListener(e -> {
            FeesPopup popup = new FeesPopup((JFrame) SwingUtilities.getWindowAncestor(this), FeesPopup.ADD_FEE, this, -1);
            popup.show();
        });
        addPanel.add(addButton);

        add(addPanel);

        // Tính tổng số trang
        totalPages = (int) Math.ceil((double) data.size() / rowsPerPage);
        currentPageData = getPageData(currentPage);

        // Bảng phí
        feesTable = new FeesTable(currentPageData, columnNames, this);
        feesTable.setBounds(0, 100, 859, 550);
        add(feesTable);

        // Bảng phân trang
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
        feesTable.updateTableData(currentPageData);
        updatePagination();
    }

    public static int[] getColumnX() {
        return columnX;
    }

    private void filterTable() {
        String query = searchField.getText().trim().toLowerCase();

        if (query.isEmpty() || query.equals("Nhập từ khoá (tên khoản phí hoặc miêu tả) và Enter để tìm kiếm")) {
            data = getFeesData();
        } else {
            data = (ArrayList<Object[]>) getFeesData().stream()
                    .filter(row -> row[2].toString().toLowerCase().contains(query) || row[4].toString().toLowerCase().contains(query))
                    .collect(Collectors.toList());
        }

        currentPage = 1;
        totalPages = (int) Math.ceil((double) data.size() / rowsPerPage);
        updateTable();
    }
}
