import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
class DishesFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private Connection connection;

    public DishesFrame(Connection connection) {
        this.connection = connection;
        setTitle("Dishes Details");
        setSize(800, 400);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[]{"Dish ID", "Menu ID", "Name", "Price"}, 0);
        table = new JTable(tableModel);
        loadDishesData();

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);

        addButton.addActionListener(e -> addDish());
        updateButton.addActionListener(e -> updateDish());
        deleteButton.addActionListener(e -> deleteDish());
    }

    private void loadDishesData() {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM Dishes");
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("dish_id"),
                        rs.getInt("menu_id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addDish() {
        // Create a panel to hold the input fields
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Menu ID:"));
        JTextField menuIdField = new JTextField();
        panel.add(menuIdField);

        panel.add(new JLabel("Name:"));
        JTextField nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Price:"));
        JTextField priceField = new JTextField();
        panel.add(priceField);

        // Show the dialog with the panel
        int result = JOptionPane.showConfirmDialog(this, panel, "Enter Dish Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int menuId = Integer.parseInt(menuIdField.getText());
                String name = nameField.getText();
                double price = Double.parseDouble(priceField.getText());

                // Check for empty fields
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Dish Name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try (PreparedStatement pstmt = connection.prepareStatement("INSERT INTO Dishes (menu_id, name, price) VALUES (?, ?, ?)")) {
                    pstmt.setInt(1, menuId);
                    pstmt.setString(2, name);
                    pstmt.setDouble(3, price);
                    pstmt.executeUpdate();
                    loadDishesData();
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error adding dish: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input for Menu ID or Price.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateDish() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update.");
            return;
        }

        int dishId = (int) tableModel.getValueAt(selectedRow, 0);

        // Create a panel to hold the input fields with pre-filled data
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Menu ID:"));
        JTextField menuIdField = new JTextField(String.valueOf(tableModel.getValueAt(selectedRow, 1)));
        panel.add(menuIdField);

        panel.add(new JLabel("Name:"));
        JTextField nameField = new JTextField((String) tableModel.getValueAt(selectedRow, 2));
        panel.add(nameField);

        panel.add(new JLabel("Price:"));
        JTextField priceField = new JTextField(String.valueOf(tableModel.getValueAt(selectedRow, 3)));
        panel.add(priceField);

        // Show the dialog with the panel
        int result = JOptionPane.showConfirmDialog(this, panel, "Update Dish Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int menuId = Integer.parseInt(menuIdField.getText());
                String name = nameField.getText();
                double price = Double.parseDouble(priceField.getText());

                // Check for empty fields
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Dish Name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try (PreparedStatement pstmt = connection.prepareStatement("UPDATE Dishes SET menu_id = ?, name = ?, price = ? WHERE dish_id = ?")) {
                    pstmt.setInt(1, menuId);
                    pstmt.setString(2, name);
                    pstmt.setDouble(3, price);
                    pstmt.setInt(4, dishId);
                    pstmt.executeUpdate();
                    loadDishesData();
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error updating dish: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input for Menu ID or Price.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void deleteDish() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            return;
        }

        int dishId = (int) tableModel.getValueAt(selectedRow, 0);
        try (PreparedStatement pstmt = connection.prepareStatement("DELETE FROM Dishes WHERE dish_id = ?")) {
            pstmt.setInt(1, dishId);
            pstmt.executeUpdate();
            loadDishesData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
