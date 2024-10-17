package model;

import controller.DatabaseConnected;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;

public class Account {
    public int accountId;
    public String accountName;
    public String accountPassword;
    public BufferedImage accountImage;

    public final String defaultImage = "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";

    public Account(int accountId, String accountName, String accountPassword, String base64Image) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.accountPassword = accountPassword;
        this.accountImage = decodeBase64ToImage(base64Image);
    }

    public Account(int accountId, String accountName, String accountPassword) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.accountPassword = accountPassword;
        this.accountImage = decodeBase64ToImage(defaultImage);
    }

    public BufferedImage decodeBase64ToImage(String base64Image) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            InputStream inputStream = new ByteArrayInputStream(imageBytes);
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Phương thức cập nhật dữ liệu tài khoản trong DB
    public void updateAccountInDB() throws SQLException, DatabaseConnected.DatabaseConnectionException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnected.getConnection();

            if (connection != null) {
                String query = "UPDATE account SET username = ?, password = ?, image = ? WHERE id = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, this.accountName);
                statement.setString(2, this.accountPassword);
                statement.setString(3, encodeImageToBase64(this.accountImage));
                statement.setInt(4, this.accountId);

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new SQLException("Không thể cập nhật tài khoản vào DB", e);
        } finally {
            if (statement != null) statement.close();
            if (connection != null) DatabaseConnected.closeConnection(connection);
        }
    }

    // Phương thức mã hóa ảnh thành Base64 để lưu vào DB
    private String encodeImageToBase64(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi mã hóa ảnh", e);
        }
    }
}
