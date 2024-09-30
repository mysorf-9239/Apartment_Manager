package controller;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnected {
    private static final String URL = "jdbc:mysql://localhost:3306/apartment";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

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

    // Lấy dữ liệu của cư dân
    public static Object[][] getResidentsData() {
        String query = "SELECT CONCAT(full_name, '  -  ', DATE_FORMAT(date_of_birth, '%d/%m/%Y')) AS full_info FROM residents";
        Connection connection = null;
        try {
            connection = getConnection();
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery(query);

            // Đếm số lượng hàng
            resultSet.last();
            int rowCount = resultSet.getRow();
            resultSet.beforeFirst();

            // Tạo mảng dữ liệu
            Object[][] data = new Object[rowCount][4];

            int index = 0;
            while (resultSet.next()) {
                data[index][0] = String.format("%02d", index + 1);
                data[index][1] = resultSet.getString("full_info");
                index++;
            }

            resultSet.close();
            statement.close();

            return data;
        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
            return new Object[0][0];
        } finally {
            closeConnection(connection);
        }
    }
}
