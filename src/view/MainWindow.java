package view;

import util.ImageLoader;
import view.fees.FeesWindow;
import view.households.HouseholdsWindow;
import view.payments.PaymentWindow;
import view.residents.ResidentsWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class MainWindow extends JPanel {

    private JPanel sidePanel, contentPanel;
    private CardLayout cardLayout;
    private JLabel featureTitle;
    private String currentFeature = "Quản lý chung cư";
    public static BufferedImage logoImage, searchImage, editImage, deleteImage;

    public MainWindow() {
        setLayout(null);
        loadImages(); // Load images

        // Thanh bên (side panel)
        sidePanel = new JPanel();
        sidePanel.setBackground(Color.LIGHT_GRAY);
        sidePanel.setBounds(0, 0, 216, 720);
        sidePanel.setLayout(null);

        // Logo Box
        JPanel logoBox = new JPanel();
        logoBox.setBounds(0, 0, 216, 90);
        logoBox.setLayout(null);

        // Custom Logo Panel
        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (logoImage != null) {
                    g.drawImage(logoImage, 10, 5, 80, 80, this);
                }
            }
        };
        logoPanel.setBounds(0, 0, 100, 100);
        logoBox.add(logoPanel);

        // Tên ứng dụng
        JLabel appName = new JLabel("App Name");
        appName.setFont(new Font("Arial", Font.BOLD, 18));
        appName.setBounds(100, 30, 100, 30);
        appName.setHorizontalAlignment(SwingConstants.LEFT);
        logoBox.add(appName);

        // Thêm LogoBox vào thanh bên
        sidePanel.add(logoBox);

        // Các nút chức năng
        String[] featureNames = {"Quản lý nhân khẩu", "Quản lý hộ khẩu", "Quản lý khoản phí", "Quản lý khoản thu"};
        for (int i = 0; i < featureNames.length; i++) {
            JButton button = getjButton(featureNames, i);
            sidePanel.add(button);
        }

        add(sidePanel);

        // Nội dung chính (content panel)
        contentPanel = new JPanel();
        contentPanel.setBounds(221, 0, 859, 720);
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Tên tính năng hiện tại
        featureTitle = new JLabel(currentFeature, SwingConstants.CENTER);
        featureTitle.setBounds(0, 0, 859, 30);
        contentPanel.add(featureTitle);

        // Create and add feature panels
        contentPanel.add(new ResidentsWindow(), "Residents");
        contentPanel.add(new HouseholdsWindow(), "Households");
        contentPanel.add(new FeesWindow(), "Fees");
        contentPanel.add(new PaymentWindow(), "Payments");

        // Initially set to a blank panel
        JPanel blankPanel = new JPanel();
        contentPanel.add(blankPanel, "Blank");

        add(contentPanel);

        // Initially show the blank panel
        cardLayout.show(contentPanel, "Blank");
    }

    private void loadImages() {
        logoImage = ImageLoader.loadImage("/img/logo.png", 80, 80);
        searchImage = ImageLoader.loadImage("/img/search.png", 30, 30);
        editImage = ImageLoader.loadImage("/img/edit.png", 30, 30);
        deleteImage = ImageLoader.loadImage("/img/delete.png", 30, 30);
    }

    private JButton getjButton(String[] featureNames, int i) {
        final String featureName = featureNames[i];
        JButton button = new JButton(featureName);
        button.setBounds(0, 90 + (i * 50), 216, 50);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMargin(new Insets(0, 20, 0, 0));

        // Thêm ActionListener để cập nhật tên tính năng hiện tại
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentFeature = featureName;
                updateFeatureTitle();
                switchToFeaturePanel(featureName);
            }
        });
        return button;
    }

    private void updateFeatureTitle() {
        featureTitle.setText(currentFeature);
    }

    private void switchToFeaturePanel(String featureName) {
        String cardName;
        switch (featureName) {
            case "Quản lý nhân khẩu":
                cardName = "Residents";
                break;
            case "Quản lý hộ khẩu":
                cardName = "Households";
                break;
            case "Quản lý khoản phí":
                cardName = "Fees";
                break;
            case "Quản lý khoản thu":
                cardName = "Payments";
                break;
            default:
                cardName = "Blank";
        }
        cardLayout.show(contentPanel, cardName);
    }
}
