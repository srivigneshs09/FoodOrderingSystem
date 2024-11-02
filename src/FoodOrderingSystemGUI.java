import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FoodOrderingSystemGUI extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/FoodOrderingSystem";
    private static final String DB_USER = "root"; // Update with your MySQL username
    private static final String DB_PASSWORD = "Sri@09123"; // Update with your MySQL password

    private Connection connection;

    public FoodOrderingSystemGUI() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connected!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to database.");
            System.exit(1);
        }

        // Show login screen
        new LoginFrame(this);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FoodOrderingSystemGUI());
    }

    // Method to get connection (could be used in other classes)
    public Connection getConnection() {
        return connection;
    }
}

class LoginFrame extends JFrame {
    public LoginFrame(FoodOrderingSystemGUI mainApp) {
        setTitle("Login - Food Ordering System");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(new JLabel());
        panel.add(loginButton);

        add(panel);
        setVisible(true);

        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            if (username.equals("admin") && password.equals("password")) { // Placeholder credentials
                dispose();
                new DashboardFrame(mainApp);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
            }
        });
    }
}
