import view.LoginWindow;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Apartment");
        window.setSize(new Dimension(1080, 720));

        LoginWindow mainWindow = new LoginWindow();
        window.add(mainWindow);

        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}