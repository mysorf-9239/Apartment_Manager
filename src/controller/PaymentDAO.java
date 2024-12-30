package controller;

import model.Fee;
import model.Household;
import model.Payment;
import model.Resident;

import java.sql.*;
import java.util.ArrayList;

import static controller.DatabaseConnection.getConnection;
import static controller.HouseholdDAO.getResidentById;

public class PaymentDAO {
    // Lấy data cho payment có id
    public static Payment getPaymentById(int household_id, int fee_id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Kết nối cơ sở dữ liệu
            conn = DatabaseConnection.getConnection();

            String sql = "SELECT * FROM `payments` WHERE household_id = ? AND fee_id = ? LIMIT 1";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, household_id);
            pstmt.setInt(2, fee_id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // Lấy household_id -> Lấy thông tin từ bảng households
                Household household = getHouseholdById(household_id, conn);

                // Lấy fee_id -> Lấy thông tin từ bảng fees
                Fee fee = getFeeById(fee_id, conn);

                // Tạo đối tượng Payment
                return new Payment(
                        rs.getInt("id"),
                        household,
                        fee,
                        rs.getDouble("payment_amount"),
                        rs.getTimestamp("payment_date"),
                        rs.getString("payment_method"),
                        rs.getString("note")
                );
            }
        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        return null;
    }

    // Method to get all payments for a specific household_id and fee_id
    public static ArrayList<Payment> getPaymentsByHouseholdAndFee(int householdId, int feeId, Connection conn) throws SQLException {
        ArrayList<Payment> paymentRecords = new ArrayList<>();

        String sql = "SELECT `id`, `payment_amount`, `payment_date`, `payment_method`, `note` FROM `payments` WHERE household_id = ? AND fee_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, householdId);
            pstmt.setInt(2, feeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Lấy thông tin từ bảng households
                    Household household = getHouseholdById(householdId, conn);

                    // Lấy thông tin từ bảng fees
                    Fee fee = getFeeById(feeId, conn);

                    // Tạo một đối tượng Payment mới
                    Payment payment = new Payment(
                            rs.getInt("id"),
                            household,
                            fee,
                            rs.getInt("payment_amount"),
                            rs.getTimestamp("payment_date"),
                            rs.getString("payment_method"),
                            rs.getString("note")
                    );
                    paymentRecords.add(payment);
                }
            }
        }
        return paymentRecords;
    }

    //Lấy data cho dropdown
    public static ArrayList<Object[]> getFeesDropdown() {
        ArrayList<Object[]> fees = new ArrayList<>();
        String query = "SELECT id, fee_name FROM fees";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int fee_id = resultSet.getInt("id");
                String fee_name = resultSet.getString("fee_name");
                fees.add(new Object[]{fee_id, fee_name});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DatabaseConnectionException e) {
            throw new RuntimeException(e);
        }

        return fees;
    }

    // Phương thức lấy thông tin từ bảng households theo household_id
    public static Household getHouseholdById(int householdId, Connection conn) throws SQLException {
        String sql = "SELECT `id`, `address`, `head_of_household`, `acreage` FROM `households` WHERE id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, householdId);
        ResultSet rs = pstmt.executeQuery();

        Household household = null;
        if (rs.next()) {
            int headOfHouseholdId = rs.getInt("head_of_household");

            // Lấy thông tin của head_of_household từ bảng residents
            Resident headOfHousehold = getResidentById(headOfHouseholdId);

            // Tạo đối tượng Household bao gồm cả đối tượng Resident cho head_of_household
            household = new Household(rs.getInt("id"), rs.getString("address"), rs.getDouble("acreage"), headOfHousehold);
        }
        rs.close();
        pstmt.close();
        return household;
    }

    // Phương thức lấy thông tin từ bảng fees theo fee_id
    public static Fee getFeeById(int feeId, Connection conn) throws SQLException {
        String sql = "SELECT `id`, `fee_name`, `fee_description`, `amount`, `created_at`, `updated_at`, `status`, `type` FROM `fees` WHERE id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, feeId);
        ResultSet rs = pstmt.executeQuery();

        Fee fee = null;
        if (rs.next()) {
            fee = new Fee(rs.getInt("id"), rs.getString("fee_name"), rs.getString("fee_description"),
                    rs.getDouble("amount"), rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at"), rs.getString("status"), rs.getString("type"));
        }
        rs.close();
        pstmt.close();
        return fee;
    }

    public static Fee getFeeById(int feeId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Fee fee = null;

        try {
            conn = DatabaseConnection.getConnection();

            String sql = "SELECT `id`, `fee_name`, `fee_description`, `amount`, `created_at`, `updated_at`, `status`, `type` FROM `fees` WHERE id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, feeId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                fee = new Fee(rs.getInt("id"),
                        rs.getString("fee_name"),
                        rs.getString("fee_description"),
                        rs.getDouble("amount"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"),
                        rs.getString("status"),
                        rs.getString("type"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DatabaseConnectionException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return fee;
    }

    // Phương thức mới lấy thông tin từ bảng payments dựa trên household_id và fee_id
    private static Payment getPaymentByHouseholdAndFeeId(int householdId, int feeId, Connection conn) throws SQLException {
        PreparedStatement paymentStmt = null;
        ResultSet paymentRs = null;
        Payment payment = null;

        try {
            String paymentSql = "SELECT `id`, `payment_amount`, `payment_date`, `payment_method`, `note` FROM `payments` WHERE household_id = ? AND fee_id = ?";
            paymentStmt = conn.prepareStatement(paymentSql);
            paymentStmt.setInt(1, householdId);
            paymentStmt.setInt(2, feeId);
            paymentRs = paymentStmt.executeQuery();

            if (paymentRs.next()) {
                // Lấy thông tin từ bảng households
                Household household = getHouseholdById(householdId, conn);

                // Lấy thông tin từ bảng fees
                Fee fee = getFeeById(feeId, conn);

                // Tạo một đối tượng Payment mới
                payment = new Payment(
                        paymentRs.getInt("id"),
                        household,
                        fee,
                        paymentRs.getInt("payment_amount"),
                        paymentRs.getTimestamp("payment_date"),
                        paymentRs.getString("payment_method"),
                        paymentRs.getString("note")
                );
            }

        } finally {
            if (paymentRs != null) try { paymentRs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (paymentStmt != null) try { paymentStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        return payment;
    }

    // Lấy data cho payment của khoản phí có fee_id
    public static ArrayList<Object[]> getPaymentData(int fee_Id) {
        ArrayList<Object[]> paymentData = new ArrayList<>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Kết nối cơ sở dữ liệu
            conn = DatabaseConnection.getConnection();

            // Bước 1: Lấy dữ liệu từ bảng households_fees theo fee_id
            String sql = "SELECT `id`, `household_id`, `fee_id`, `amount_due`, `due_date`, `status`, `created_at`, `updated_at` FROM `households_fees` WHERE fee_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, fee_Id);
            rs = pstmt.executeQuery();

            int index = 0;
            while (rs.next()) {
                Object[] paymentRecord = new Object[10];

                // 0: Index
                paymentRecord[0] = String.format("%02d", index + 1);

                // 1: id (households_fees)
                int householdFeeId = rs.getInt("id");
                paymentRecord[1] = householdFeeId;

                // 2: Lấy household_id -> Lấy thông tin từ bảng households
                int householdId = rs.getInt("household_id");
                Household household = getHouseholdById(householdId, conn);
                paymentRecord[2] = household;

                // 3: Lấy fee_id -> Lấy thông tin từ bảng fees
                int feeId = rs.getInt("fee_id");
                Fee fee = getFeeById(feeId, conn);
                paymentRecord[3] = fee;

                // 4: amount_due
                paymentRecord[4] = rs.getDouble("amount_due");

                // 5: due_date
                paymentRecord[5] = rs.getDate("due_date");

                // 6: status
                paymentRecord[6] = rs.getString("status");

                // 7: created_at
                paymentRecord[7] = rs.getTimestamp("created_at");

                // 8: updated_at
                paymentRecord[8] = rs.getTimestamp("updated_at");

                // 9: Lấy thông tin thanh toán từ bảng payments qua phương thức mới
                ArrayList<Payment> payments = getPaymentsByHouseholdAndFee(householdId, feeId, conn);
                paymentRecord[9] = payments;

                // Thêm record vào danh sách
                paymentData.add(paymentRecord);
                index++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DatabaseConnectionException e) {
            throw new RuntimeException(e);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        return paymentData;
    }

    /*Add payment*/
    public static ArrayList<Object[]> addPayment(int householdId, int feeId, double paymentAmount, String paymentMethod) throws SQLException {
        ArrayList<Object[]> paymentData = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement updatePstmt = null;
        PreparedStatement selectPstmt = null;

        try {
            conn = DatabaseConnection.getConnection();

            // Insert new payment
            String insertSql = "INSERT INTO payments (household_id, fee_id, payment_amount, payment_method) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, householdId);
            pstmt.setInt(2, feeId);
            pstmt.setDouble(3, paymentAmount);
            pstmt.setString(4, paymentMethod);
            pstmt.executeUpdate();

            // Get the payment ID
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int paymentId = generatedKeys.getInt(1);

                // Update status in households_fees
                String updateSql = "UPDATE households_fees SET status = ?, updated_at = NOW() WHERE household_id = ? AND fee_id = ?";
                double totalPaid = getTotalPaid(householdId, feeId, conn);
                double amountDue = getAmountDue(householdId, feeId, conn);
                String status = totalPaid >= amountDue ? "Đã thanh toán" : "Thanh toán một phần";

                updatePstmt = conn.prepareStatement(updateSql);
                updatePstmt.setString(1, status);
                updatePstmt.setInt(2, householdId);
                updatePstmt.setInt(3, feeId);
                updatePstmt.executeUpdate();

                // Get updated households_fees record
                String selectSql = "SELECT * FROM households_fees WHERE household_id = ? AND fee_id = ?";
                selectPstmt = conn.prepareStatement(selectSql);
                selectPstmt.setInt(1, householdId);
                selectPstmt.setInt(2, feeId);
                ResultSet rs = selectPstmt.executeQuery();

                int index = 0;
                while (rs.next()) {
                    Object[] paymentRecord = new Object[10];
                    paymentRecord[0] = String.format("%02d", index + 1);
                    paymentRecord[1] = rs.getInt("id");
                    paymentRecord[2] = getHouseholdById(householdId, conn);
                    paymentRecord[3] = getFeeById(feeId, conn);
                    paymentRecord[4] = rs.getDouble("amount_due");
                    paymentRecord[5] = rs.getDate("due_date");
                    paymentRecord[6] = rs.getString("status");
                    paymentRecord[7] = rs.getTimestamp("created_at");
                    paymentRecord[8] = rs.getTimestamp("updated_at");
                    paymentRecord[9] = getPaymentsByHouseholdAndFee(householdId, feeId, conn);
                    paymentData.add(paymentRecord);
                    index++;
                }
            }

        } catch (DatabaseConnectionException e) {
            throw new RuntimeException(e);
        } finally {
            if (selectPstmt != null) selectPstmt.close();
            if (updatePstmt != null) updatePstmt.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
        return paymentData;
    }

    //Lấy id chủ nhà bằng cccd và full_name
    public static int getHouseholdIdByCCCD(String headOfHouseholdName, String cccd) throws SQLException {
        String sql = "SELECT h.id FROM households h INNER JOIN residents r ON h.head_of_household = r.id WHERE r.full_name = ? AND r.id_card = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, headOfHouseholdName);
            pstmt.setString(2, cccd);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new SQLException("Không tìm thấy hộ gia đình với chủ hộ đã cho.");
            }
        } catch (DatabaseConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getFeeIdByName(String feeName) throws SQLException {
        String sql = "SELECT id FROM fees WHERE fee_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, feeName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new SQLException("Không tìm thấy khoản phí với tên đã cho.");
            }
        } catch (DatabaseConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean checkHouseholdFeeExist(int householdId, int feeId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(*) FROM households_fees WHERE household_id = ? AND fee_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, householdId);
            pstmt.setInt(2, feeId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (DatabaseConnectionException e) {
            throw new RuntimeException(e);
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
        return false;
    }

    public static double getTotalPaid(int householdId, int feeId, Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double totalPaid = 0.0;

        try {
            String sql = "SELECT SUM(payment_amount) FROM payments WHERE household_id = ? AND fee_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, householdId);
            pstmt.setInt(2, feeId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                totalPaid = rs.getDouble(1);
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
        return totalPaid;
    }

    public static double getAmountDue(int householdId, int feeId, Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double amountDue = 0.0;

        try {
            String sql = "SELECT amount_due FROM households_fees WHERE household_id = ? AND fee_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, householdId);
            pstmt.setInt(2, feeId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                amountDue = rs.getDouble("amount_due");
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
        return amountDue;
    }

    public static boolean updatePayment(int paymentId, int amount, String method) throws SQLException {
        String query = "UPDATE payments SET payment_amount = ?, payment_method = ? WHERE id = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, amount);
            preparedStatement.setString(2, method);
            preparedStatement.setInt(3, paymentId);

            return preparedStatement.executeUpdate() > 0;
        } catch (DatabaseConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean deletePayment(int household_id, int fee_id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            // Thiết lập kết nối đến cơ sở dữ liệu
            conn = getConnection();

            // Câu lệnh SQL để xóa các thanh toán có fee_id và household_id tương ứng
            String sql = "DELETE FROM payments WHERE household_id = ? AND fee_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, household_id);
            pstmt.setInt(2, fee_id);

            // Thực thi câu lệnh xóa
            int rowsAffected = pstmt.executeUpdate();

            // Kiểm tra nếu có ít nhất một hàng bị xóa
            success = (rowsAffected > 0);

        } catch (SQLException e) {
            e.printStackTrace();
            success = false; // Đặt success về false nếu có lỗi xảy ra
        } catch (DatabaseConnectionException e) {
            throw new RuntimeException(e);
        } finally {
            // Đảm bảo đóng kết nối và PreparedStatement
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return success;
    }
}
