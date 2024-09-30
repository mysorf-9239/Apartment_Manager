package controller;

import model.Resident;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseConnected {
    private static final String URL = "jdbc:mysql://localhost:3306/apartment";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    // Kết nối đến cơ sở dữ liệu
    public static Connection getConnection() throws DatabaseConnectionException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            if (connection != null && !connection.isClosed()) {
                return connection;
            } else {
                throw new DatabaseConnectionException("Không thể kết nối đến cơ sở dữ liệu.");
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Lỗi khi kết nối đến cơ sở dữ liệu: " + e.getMessage(), e);
        }
    }

    // Đóng kết nối cơ sở dữ liệu
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
            }
        }
    }

    // Lớp ngoại lệ tùy chỉnh cho kết nối cơ sở dữ liệu
    public static class DatabaseConnectionException extends Exception {
        public DatabaseConnectionException(String message) {
            super(message);
        }

        public DatabaseConnectionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // Lấy dữ liệu cư dân và trả về dưới dạng ArrayList<Object[]>
    public static ArrayList<Object[]> getResidentsData() {
        String query = "SELECT id, full_name, date_of_birth, gender, id_card, is_temp_resident, household_id FROM residents";
        Connection connection = null;
        ArrayList<Object[]> residentsData = new ArrayList<>(); // Sử dụng ArrayList

        try {
            connection = getConnection();
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery(query);

            // Duyệt qua các hàng trong ResultSet và thêm vào ArrayList
            int index = 0;
            while (resultSet.next()) {
                Object[] row = new Object[8];
                row[0] = String.format("%02d", index + 1);
                row[1] = resultSet.getString("full_name");
                row[2] = resultSet.getString("date_of_birth");
                row[3] = resultSet.getString("gender");
                row[4] = resultSet.getString("id_card");
                row[5] = resultSet.getString("is_temp_resident");
                if (resultSet.getString("household_id") != null) {
                    row[6] = resultSet.getString("household_id");
                } else {
                    row[6] = "";
                }
                row[7] = resultSet.getString("id");
                residentsData.add(row);
                index++;
            }

            resultSet.close();
            statement.close();
        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }

        return residentsData;
    }

    public static void addResident(String name, String birthDate, String gender, String idCard) {
        String query = "INSERT INTO residents (full_name, date_of_birth, gender, id_card) VALUES (?, ?, ?, ?)";
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setDate(2, java.sql.Date.valueOf(birthDate));
            preparedStatement.setString(3, gender);
            preparedStatement.setString(4, idCard);

            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }

    // Phương thức mới để cập nhật cư dân
    public static boolean updateResident(int residentID, String name, String birthDate, String gender, String idCard) {
        String sql = "UPDATE residents SET full_name = ?, date_of_birth = ?, gender = ?, id_card = ? WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            // Thiết lập các tham số cho PreparedStatement
            pstmt.setString(1, name);
            pstmt.setString(2, birthDate);
            pstmt.setString(3, gender);
            pstmt.setString(4, idCard);
            pstmt.setInt(5, residentID);

            // Thực thi câu lệnh cập nhật
            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (DatabaseConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean deleteResident(int residentID) {
        String sql = "DELETE FROM residents WHERE id = ?";

        try (Connection connection = DatabaseConnected.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // Set the residentID in the query
            preparedStatement.setInt(1, residentID);

            // Execute the update
            int rowsAffected = preparedStatement.executeUpdate();

            // Return true if delete was successful
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (DatabaseConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    // Lấy dữ liệu hộ khẩu và trả về dưới dạng ArrayList<Object[]>
    public static ArrayList<Object[]> getHouseholdsData() {
        ArrayList<Object[]> householdsData = new ArrayList<>();
        String query = "SELECT h.id, h.address, r.id, r.full_name, r.date_of_birth, r.gender, r.id_card " +
                "FROM households h " +
                "JOIN residents r ON h.head_of_household = r.id";

        try (Connection conn = DatabaseConnected.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Duyệt qua các hàng trong ResultSet và thêm vào ArrayList
            int index = 0;
            while (rs.next()) {
                Object[] row = new Object[4];
                row[0] = String.format("%02d", index + 1);
                row[1] = rs.getInt("h.id");
                row[2] = rs.getString("h.address");

                // Tạo đối tượng Resident (chủ hộ) từ kết quả truy vấn
                Resident headOfHousehold = new Resident(rs.getInt("r.id"),
                        rs.getString("r.full_name"),
                        rs.getString("r.date_of_birth"),
                        rs.getString("r.gender"),
                        rs.getString("r.id_card"));
                row[3] = headOfHousehold;

                householdsData.add(row);
                index++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DatabaseConnectionException e) {
            throw new RuntimeException(e);
        }

        return householdsData;
    }


    // Phương thức tìm thông tin của chủ hộ dựa trên họ tên và CCCD
    public static Resident getHeadOfHouseholdInfo(String fullName, String idCard) throws SQLException {
        String sql = "SELECT id, full_name, date_of_birth, gender, id_card FROM residents WHERE full_name = ? AND id_card = ?";
        try (Connection conn = DatabaseConnected.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Thiết lập các tham số cho PreparedStatement
            pstmt.setString(1, fullName);
            pstmt.setString(2, idCard);

            // Thực thi truy vấn
            ResultSet rs = pstmt.executeQuery();

            // Kiểm tra kết quả và trả về đối tượng Resident nếu tìm thấy
            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("full_name");
                String birthDate = rs.getString("date_of_birth");
                String gender = rs.getString("gender");
                String idCardNumber = rs.getString("id_card");

                // Tạo và trả về đối tượng Resident
                return new Resident(id, name, birthDate, gender, idCardNumber);
            } else {
                return null; // Trả về null nếu không tìm thấy cư dân
            }
        } catch (DatabaseConnectionException e) {
            throw new RuntimeException(e);
        }
    }


    // Phương thức thêm hộ khẩu mới vào cơ sở dữ liệu và trả về household_id vừa được tạo
    public static int addHousehold(String address, int headOfHouseholdId) throws SQLException {
        String sql = "INSERT INTO households (address, head_of_household) VALUES (?, ?)";
        try (Connection conn = DatabaseConnected.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Thiết lập các tham số cho PreparedStatement
            pstmt.setString(1, address);
            pstmt.setInt(2, headOfHouseholdId);

            // Thực thi câu lệnh thêm mới
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Lấy household_id vừa được tạo từ bảng households
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);  // Trả về household_id vừa được thêm
                }
            }
            return -1; // Trả về -1 nếu không thêm được hộ khẩu
        } catch (DatabaseConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    // Phương thức cập nhật hộ khẩu trong cơ sở dữ liệu
    public static boolean updateHousehold(int householdID, String newAddress, int newHeadOfHouseholdID) throws SQLException {
        String sql = "UPDATE households SET address = ?, head_of_household = ? WHERE id = ?";
        try (Connection conn = DatabaseConnected.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Thiết lập các tham số cho PreparedStatement
            pstmt.setString(1, newAddress);
            pstmt.setInt(2, newHeadOfHouseholdID);
            pstmt.setInt(3, householdID);

            // Thực thi câu lệnh cập nhật
            int affectedRows = pstmt.executeUpdate();

            // Trả về true nếu cập nhật thành công
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (DatabaseConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    // Phương thức xóa hộ khẩu trong cơ sở dữ liệu
    public static boolean deleteHousehold(int householdID) {
        String sql = "DELETE FROM households WHERE id = ?";
        try (Connection conn = DatabaseConnected.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, householdID);

            // Thực thi câu lệnh xóa
            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (DatabaseConnectionException e) {
            throw new RuntimeException(e);
        }
    }




}
