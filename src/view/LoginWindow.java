package view;

import controller.DatabaseConnection;
import controller.DatabaseConnectionException;
import model.Account;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginWindow extends JPanel {

    public LoginWindow() {
        setLayout(null);

        int windowWidth = 1080;
        int windowHeight = 720;

        // Chia screen
        int leftWidth = (int) (windowWidth * 2.0 / 3.0);
        int rightWidth = (int) (windowWidth * 1.0 / 3.0);

        // Phần bên trái: Hình nền
        JPanel leftPanel = new BackgroundPanel("/img/apartment_draw.jpg", 0.9f);
        leftPanel.setBounds(0, 0, leftWidth, windowHeight);
        leftPanel.setLayout(null);

        // Phần bên phải: Chứa các trường nhập và nút đăng nhập
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(200, 232, 200));
        rightPanel.setBounds(leftWidth, 0, rightWidth, windowHeight);
        rightPanel.setLayout(null);

        // Chữ "ĐĂNG NHẬP"
        JLabel loginLabel = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        loginLabel.setFont(new Font("Arial", Font.BOLD, 36));
        loginLabel.setBounds(30, 250, rightWidth - 60, 100);
        rightPanel.add(loginLabel);

        // Các thành phần đăng nhập
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(30, 350, 100, 30);
        JTextField userText = new JTextField();
        userText.setBounds(130, 350, 150, 30);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(30, 400, 100, 30);
        JPasswordField passText = new JPasswordField();
        passText.setBounds(130, 400, 150, 30);

        JButton loginButton = new JButton("LOGIN");
        loginButton.setBounds(130, 450, 100, 30);

        // Thêm các thành phần vào rightPanel
        rightPanel.add(userLabel);
        rightPanel.add(userText);
        rightPanel.add(passLabel);
        rightPanel.add(passText);
        rightPanel.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userText.getText();
                String password = new String(passText.getPassword());

                MainWindow.account = getUserAccount(username, password);

                if (MainWindow.account != null) {
                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(LoginWindow.this);
                    parentFrame.getContentPane().removeAll();
                    parentFrame.add(new MainWindow());
                    parentFrame.revalidate();
                    parentFrame.repaint();
                } else {
                    JOptionPane.showMessageDialog(null, "Thông tin đăng nhập không chính xác");
                }
            }
        });

        add(leftPanel);
        add(rightPanel);
    }

    // Phương thức kiểm tra thông tin đăng nhập và tạo đối tượng Account
    private Account getUserAccount(String username, String password) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();

            if (connection != null) {
                String query = "SELECT id, username, password, image FROM account WHERE username = ? AND password = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, username.trim());
                statement.setString(2, password.trim());

                resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    int accountId = resultSet.getInt("id");
                    String accountName = resultSet.getString("username");
                    String accountPassword = resultSet.getString("password");
                    String base64Image = resultSet.getString("image");

                    if (base64Image == null || base64Image.isEmpty()) {
                        return new Account(accountId, accountName, accountPassword);
                    } else {
                        return new Account(accountId, accountName, accountPassword, base64Image);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (DatabaseConnectionException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) DatabaseConnection.closeConnection(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}