import view.LoginWindow;
import view.MainWindow;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Apartment");
        window.setSize(new Dimension(1080, 720));

        MainWindow mainWindow = new MainWindow();
        window.add(mainWindow);

        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}