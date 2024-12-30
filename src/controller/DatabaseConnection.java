package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASSWORD = "password";
    private static final String DATABASE_NAME = "apartment";
    private static final String DUMP_FILE_PATH = "src/controller/dump.sql";


    // Kết nối đến cơ sở dữ liệu
    public static Connection getConnection() throws DatabaseConnectionException {
        Connection connection = null;
        try {
            // Kiểm tra xem cơ sở dữ liệu đã tồn tại chưa, nếu chưa thì tạo mới
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            if (!isDatabaseExist(connection, DATABASE_NAME)) {
                createDatabase(connection);
                executeDumpSQL(connection);
            }
            // Kết nối vào cơ sở dữ liệu đã xác nhận tồn tại
            connection = DriverManager.getConnection(URL + DATABASE_NAME, USER, PASSWORD);
            if (connection != null && !connection.isClosed()) {
                return connection;
            } else {
                throw new DatabaseConnectionException("Không thể kết nối đến cơ sở dữ liệu.");
            }
        } catch (SQLException | IOException e) {
            throw new DatabaseConnectionException("Lỗi khi kết nối đến cơ sở dữ liệu: " + e.getMessage(), e);
        }
    }

    // Kiểm tra cơ sở dữ liệu đã tồn tại chưa
    private static boolean isDatabaseExist(Connection connection, String dbName) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SHOW DATABASES LIKE '" + dbName + "'");
        return rs.next();
    }

    // Tạo cơ sở dữ liệu
    private static void createDatabase(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();
        String createDbQuery = "CREATE DATABASE " + DATABASE_NAME;
        stmt.executeUpdate(createDbQuery);
    }

    // Thực thi file dump.sql để tạo các bảng và chèn dữ liệu
    private static void executeDumpSQL(Connection connection) throws SQLException, IOException {
        File dumpFile = new File(DUMP_FILE_PATH);
        try (BufferedReader br = new BufferedReader(new FileReader(dumpFile));
             Statement stmt = connection.createStatement()) {
            String line;
            StringBuilder query = new StringBuilder();
            while ((line = br.readLine()) != null) {
                query.append(line).append("\n");
                // Kiểm tra nếu đã đến cuối một câu lệnh SQL (có thể có dấu ";" kết thúc)
                if (line.trim().endsWith(";")) {
                    try {
                        stmt.execute(query.toString());  // Thực thi câu lệnh
                        query.setLength(0);  // Xóa bỏ nội dung cũ trong query
                    } catch (SQLException e) {
                        System.err.println("Lỗi khi thực thi câu lệnh SQL: " + query.toString());
                        System.err.println(e.getMessage());
                        throw e;  // Ném lỗi để có thể xử lý sau này
                    }
                }
            }
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
}
