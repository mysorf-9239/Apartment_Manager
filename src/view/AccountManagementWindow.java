package view;

import controller.DatabaseConnected;
import model.Account;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;

public class AccountManagementWindow extends JPanel {

    private JLabel imageLabel;
    private JTextField nameField;
    private JLabel idLabel;
    private JButton editImageButton, saveButton;
    private Account account;

    BufferedImage bufferedImage;

    public AccountManagementWindow() {
        setLayout(null);

        account = MainWindow.account; // Lấy thông tin tài khoản từ MainWindow

        // Tiêu đề
        JLabel titleLabel = new JLabel("Quản lý tài khoản", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(0, 0, 859, 30);
        add(titleLabel);

        // Hiển thị ID tài khoản (không chỉnh sửa)
        idLabel = new JLabel("Tài khoản ID: " + account.accountId);
        idLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        idLabel.setBounds(50, 100, 300, 30);
        add(idLabel);

        // Hiển thị ảnh tài khoản
        imageLabel = new JLabel(new ImageIcon(account.accountImage));
        imageLabel.setBounds(50, 150, 100, 100);
        add(imageLabel);

        // Nút để chỉnh sửa ảnh tài khoản
        editImageButton = new JButton("Chỉnh sửa ảnh");
        editImageButton.setBounds(170, 200, 150, 30);
        add(editImageButton);

        // Hiển thị và chỉnh sửa tên tài khoản
        JLabel nameLabel = new JLabel("Tên tài khoản:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        nameLabel.setBounds(50, 300, 300, 30);
        add(nameLabel);

        nameField = new JTextField(account.accountName);
        nameField.setBounds(200, 300, 200, 30);
        add(nameField);

        // Hiển thị và chỉnh sửa tên tài khoản
        JLabel passLabel = new JLabel("Mật khẩu:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        passLabel.setBounds(50, 350, 300, 30);
        add(passLabel);

        nameField = new JTextField(account.accountPassword);
        nameField.setBounds(200, 350, 200, 30);
        add(nameField);

        // Nút lưu thay đổi
        saveButton = new JButton("Lưu thay đổi");
        saveButton.setBounds(150, 400, 120, 30);
        add(saveButton);

        editImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    try {
                        bufferedImage = ImageIO.read(fileChooser.getSelectedFile());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    if (bufferedImage != null) {
                        // Kích thước mong muốn (ví dụ: 128x128)
                        int targetWidth = 128;
                        int targetHeight = 128;

                        // Tạo một hình ảnh mới với kích thước đã được thay đổi
                        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2d = resizedImage.createGraphics();

                        // Chuyển đổi hình ảnh ban đầu thành kích thước mới
                        g2d.drawImage(bufferedImage, 0, 0, targetWidth, targetHeight, null);
                        g2d.dispose();

                        account.accountImage = resizedImage;  // Lưu hình ảnh đã được cắt/scale vào account
                        imageLabel.setIcon(new ImageIcon(resizedImage));  // Cập nhật hình ảnh trên giao diện
                    } else {
                        JOptionPane.showMessageDialog(null, "Không thể tải ảnh.");
                    }
                }
            }
        });

        // Xử lý sự kiện lưu thay đổi
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newName = nameField.getText().trim();
                if (!newName.isEmpty()) {
                    account.accountName = newName;
                }

                // Cập nhật thông tin tài khoản vào DB
                updateAccount();
            }
        });
    }

    // Phương thức cập nhật tài khoản vào DB
    private void updateAccount() {
        try {
            account.updateAccountInDB();
            JOptionPane.showMessageDialog(null, "Thông tin tài khoản đã được cập nhật thành công.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi cập nhật tài khoản: " + e.getMessage());
        } catch (DatabaseConnected.DatabaseConnectionException e) {
            throw new RuntimeException(e);
        }
    }
}
