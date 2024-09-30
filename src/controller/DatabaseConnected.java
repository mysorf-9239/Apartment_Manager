package controller;

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

}
