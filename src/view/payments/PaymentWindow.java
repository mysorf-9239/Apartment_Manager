package view.payments;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import controller.DatabaseConnected;
import util.ImageLoader;

public class PaymentWindow extends JPanel {
    private static final int[] columnX = {18, 80, 238, 330, 543, 660, 730, 800};
    private BufferedImage searchImage;
    private JTextField searchField;
    private String[] columnNames = {"STT", "Chủ hộ", "Phải nộp", "Đã nộp", "Hạn nộp", "Xem", "Sửa", "Xóa"};

    public ArrayList<Object[]> data;
    public ArrayList<Object[]> currentPageData;
    public ArrayList<Object[]> feesData;

    public int currentPage = 1;
    public int rowsPerPage = 10;
    private int totalPages;
    private PaymentTable paymentTable;
    private JPanel paginationPanel;
    private JComboBox<String> feeDropdown;
    private int selectedFeeId;

    public PaymentWindow() {
        setLayout(null);
        loadImages();

        // Tiêu đề
        JLabel titleLabel = new JLabel("Quản lý thanh toán", SwingConstants.CENTER);
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

        searchField = new JTextField();
        searchField.setBounds(40, 0, 744, 30);
        searchBox.add(searchField);

        add(searchBox);

        // Nút thêm và dropdown chọn khoản phí
        JPanel addPanel = new JPanel();
        addPanel.setBounds(0, 65, 859, 30);
        addPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        // Lấy dữ liệu từ cơ sở dữ liệu
        DatabaseConnected db = new DatabaseConnected();
        feesData = db.getFeesDropdown();

        // Tạo dropdown từ danh sách
        String[] feeNames = feesData.stream().map(data -> data[1].toString()).toArray(String[]::new);
        feeDropdown = new JComboBox<>(feeNames);
        feeDropdown.setPreferredSize(new Dimension(150, 30));

        // Gán selectedFeeId bằng khoản phí đầu tiên
        selectedFeeId = (int) feesData.get(0)[0];

        // Cập nhật dữ liệu mỗi khi chọn lại khoản phí
        feeDropdown.addActionListener(e -> {
            int selectedIndex = feeDropdown.getSelectedIndex();
            selectedFeeId = (int) feesData.get(selectedIndex)[0];
            updatePaymentData();
        });
        addPanel.add(feeDropdown);

        JButton addButton = new JButton("Thêm");
        addButton.setPreferredSize(new Dimension(70, 30));
        addButton.setFocusable(false);
        addButton.addActionListener(e -> {
            PaymentPopup popup = new PaymentPopup((JFrame) SwingUtilities.getWindowAncestor(this), PaymentPopup.ADD_PAYMENT, this, selectedFeeId);
            popup.show();
        });
        addPanel.add(addButton);

        add(addPanel);

        // Lấy dữ liệu thanh toán từ cơ sở dữ liệu
        data = DatabaseConnected.getPaymentData(selectedFeeId);

        // Tính tổng số trang
        totalPages = (int) Math.ceil((double) data.size() / rowsPerPage);
        currentPageData = getPageData(currentPage);

        // Bảng thanh toán
        paymentTable = new PaymentTable(currentPageData, columnNames, this);
        paymentTable.setBounds(0, 100, 859, 550);
        add(paymentTable);

        // Bảng phân trang
        paginationPanel = new JPanel();
        paginationPanel.setBounds(0, 650, 859, 40);
        paginationPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        add(paginationPanel);

        updatePagination();
    }

    // Hàm để cập nhật dữ liệu bảng khi chọn khoản phí mới
    private void updatePaymentData() {
        data = DatabaseConnected.getPaymentData(selectedFeeId);
        totalPages = (int) Math.ceil((double) data.size() / rowsPerPage);
        currentPage = 1;
        updateTable();
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
        paymentTable.updateTableData(currentPageData);
        updatePagination();
    }

    public static int[] getColumnX() {
        return columnX;
    }
}
