package controller;

import model.Fee;
import model.HouseholdInfo;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

import static controller.DatabaseConnection.getConnection;

public class FeeDAO {
    /* Đếm */
    public static int countFees() {
        String query = "SELECT COUNT(*) FROM fees";
        int count = 0;

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                count = resultSet.getInt(1); // Lấy giá trị COUNT(*) từ kết quả truy vấn
            }
        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
        }

        return count;
    }

    public static int countFeesChung() {
        String query = "SELECT COUNT(*) FROM fees WHERE type = 'Chung'";
        int count = 0;

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                count = resultSet.getInt(1); // Lấy giá trị COUNT(*) từ kết quả truy vấn
            }
        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
        }

        return count;
    }

    public static int countFeesRieng() {
        String query = "SELECT COUNT(*) FROM fees WHERE type = 'Riêng'";
        int count = 0;

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                count = resultSet.getInt(1); // Lấy giá trị COUNT(*) từ kết quả truy vấn
            }
        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
        }

        return count;
    }

    public static int countFeesBB() {
        String query = "SELECT COUNT(*) FROM fees WHERE type = 'Bắt buộc'";
        int count = 0;

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                count = resultSet.getInt(1); // Lấy giá trị COUNT(*) từ kết quả truy vấn
            }
        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
        }

        return count;
    }

    public static ArrayList<Object[]> getFeesData() {
        ArrayList<Object[]> feesData = new ArrayList<>();
        String query = "SELECT id, fee_name, amount, fee_description, created_at, updated_at, status FROM fees";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            int index = 0;
            while (rs.next()) {
                Object[] row = new Object[8];
                row[0] = String.format("%02d", index + 1);
                row[1] = rs.getInt("id");
                row[2] = rs.getString("fee_name");
                row[3] = rs.getDouble("amount");
                row[4] = rs.getString("fee_description");
                row[5] = rs.getTimestamp("created_at");
                row[6] = rs.getTimestamp("updated_at");
                row[7] = rs.getString("status");

                feesData.add(row);
                index++;
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DatabaseConnectionException e) {
            throw new RuntimeException(e);
        }

        return feesData;
    }

    public static int addFee(String feeName, String feeDescription, double amount, String type) {
        String query = "INSERT INTO fees (fee_name, fee_description, amount, type) VALUES (?, ?, ?, ?)";
        int generatedId = -1;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, feeName);
            pstmt.setString(2, feeDescription);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, type);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedId = generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } catch (DatabaseConnectionException e) {
            throw new RuntimeException(e);
        }

        return generatedId;
    }

    public static boolean editFee(int feeId, String feeName, String feeDescription, double amount) {
        String query = "UPDATE fees SET fee_name = ?, fee_description = ?, amount = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, feeName);
            pstmt.setString(2, feeDescription);
            pstmt.setDouble(3, amount);
            pstmt.setInt(4, feeId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (DatabaseConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean deleteFee(int feeID) {
        String query = "DELETE FROM fees WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, feeID);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (DatabaseConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<Object[]> getFeesDataByType(String type) {
        ArrayList<Object[]> feesData = new ArrayList<>();
        String query;

        if (type.equals("AllF")) {
            query = "SELECT id, fee_name, amount, fee_description, created_at, updated_at, status FROM fees";
        } else {
            query = "SELECT id, fee_name, amount, fee_description, created_at, updated_at, status FROM fees WHERE type = ?";
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (!type.equals("AllF")) {
                stmt.setString(1, type);
            }

            ResultSet rs = stmt.executeQuery();
            int index = 0;
            while (rs.next()) {
                Object[] row = new Object[8];
                row[0] = String.format("%02d", index + 1);
                row[1] = rs.getInt("id");
                row[2] = rs.getString("fee_name");
                row[3] = rs.getDouble("amount");
                row[4] = rs.getString("fee_description");
                row[5] = rs.getTimestamp("created_at");
                row[6] = rs.getTimestamp("updated_at");
                row[7] = rs.getString("status");

                feesData.add(row);
                index++;
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DatabaseConnectionException e) {
            throw new RuntimeException(e);
        }

        return feesData;
    }

    public static ArrayList<HouseholdInfo> getHouseholdInfoByFeeIdAndStatus(int feeId, String[] statuses) {
        ArrayList<HouseholdInfo> householdInfoList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            // Tạo dấu hỏi cho IN clause
            String placeholders = String.join(",", Collections.nCopies(statuses.length, "?"));
            String sql = "SELECT r.full_name, h.address, fh.status " +
                    "FROM households h " +
                    "JOIN households_fees fh ON h.id = fh.household_id " +
                    "JOIN fees f ON fh.fee_id = f.id " +
                    "JOIN residents r ON h.head_of_household = r.id " +
                    "WHERE f.id = ? AND fh.status IN (" + placeholders + ")";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, feeId);
            for (int i = 0; i < statuses.length; i++) {
                pstmt.setString(i + 2, statuses[i]);
            }
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String headOfHouseholdName = rs.getString("full_name");
                String address = rs.getString("address");
                String feeStatus = rs.getString("status");
                householdInfoList.add(new HouseholdInfo(headOfHouseholdName, address, feeStatus));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DatabaseConnectionException e) {
            throw new RuntimeException(e);
        } finally {
            // Đóng tài nguyên
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return householdInfoList;
    }

    public static boolean isPart(int feeId) {
        String query = "SELECT type FROM fees WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, feeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String type = rs.getString("type");
                    return "Riêng".equalsIgnoreCase(type);
                }
            }
        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean addHousehold(int feeId, String name, String cccd, Date dueDate) {
        // Lấy thông tin phí từ bảng phí
        Fee fee = PaymentDAO.getFeeById(feeId);
        if (fee == null) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy phí với ID này.");
            return false;
        }

        System.out.println(name + "" + cccd);
        int household_id = ResidentDAO.getIdByNameAndCCCD(name, cccd);
        System.out.println(household_id);
        if (household_id <= 0) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy hộ gia đình này.");
            return false;
        }

        // Chuẩn bị câu lệnh SQL để chèn dữ liệu vào bảng households_fees
        String query = "INSERT INTO households_fees (household_id, fee_id, amount_due, due_date) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Lấy giá trị amount từ đối tượng Fee
            double amountDue = fee.amount;

            // Thiết lập các tham số trong câu lệnh SQL
            stmt.setInt(1, household_id);
            stmt.setInt(2, feeId);  // fee_id
            stmt.setDouble(3, amountDue);  // amount_due
            stmt.setDate(4, new java.sql.Date(dueDate.getTime()));  // due_date (chuyển Date thành java.sql.Date)

            // Thực thi câu lệnh SQL
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Hộ gia đình đã được thêm thành công.");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Có lỗi khi thêm hộ gia đình.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi kết nối cơ sở dữ liệu.");
            return false;
        } catch (DatabaseConnectionException e) {
            throw new RuntimeException(e);
        }
    }
}
