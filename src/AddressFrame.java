import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
class AddressFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private Connection connection;

    public AddressFrame(Connection connection) {
        this.connection = connection;
        setTitle("Address Details");
        setSize(800, 400);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[]{"Address ID", "Street", "City", "Zip Code"}, 0);
        table = new JTable(tableModel);
        loadAddressData();

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

        addButton.addActionListener(e -> addAddress());
        updateButton.addActionListener(e -> updateAddress());
        deleteButton.addActionListener(e -> deleteAddress());
    }

    private void loadAddressData() {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM Address");
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("address_id"),
                        rs.getString("street"),
                        rs.getString("city"),
                        rs.getString("zip_code")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addAddress() {
        // Create a panel to hold the input fields
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Street:"));
        JTextField streetField = new JTextField();
        panel.add(streetField);

        panel.add(new JLabel("City:"));
        JTextField cityField = new JTextField();
        panel.add(cityField);

        panel.add(new JLabel("Zip Code:"));
        JTextField zipCodeField = new JTextField();
        panel.add(zipCodeField);

        // Show the dialog with the panel
        int result = JOptionPane.showConfirmDialog(this, panel, "Enter Address Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String street = streetField.getText();
            String city = cityField.getText();
            String zipCode = zipCodeField.getText();

            // Check for empty fields
            if (street.isEmpty() || city.isEmpty() || zipCode.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled out.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (PreparedStatement pstmt = connection.prepareStatement("INSERT INTO Address (street, city, zip_code) VALUES (?, ?, ?)")) {
                pstmt.setString(1, street);
                pstmt.setString(2, city);
                pstmt.setString(3, zipCode);
                pstmt.executeUpdate();
                loadAddressData();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding address: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateAddress() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update.");
            return;
        }

        int addressId = (int) tableModel.getValueAt(selectedRow, 0);

        // Create a panel to hold the input fields with pre-filled data
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Street:"));
        JTextField streetField = new JTextField((String) tableModel.getValueAt(selectedRow, 1));
        panel.add(streetField);

        panel.add(new JLabel("City:"));
        JTextField cityField = new JTextField((String) tableModel.getValueAt(selectedRow, 2));
        panel.add(cityField);

        panel.add(new JLabel("Zip Code:"));
        JTextField zipCodeField = new JTextField((String) tableModel.getValueAt(selectedRow, 3));
        panel.add(zipCodeField);

        // Show the dialog with the panel
        int result = JOptionPane.showConfirmDialog(this, panel, "Update Address Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String street = streetField.getText();
            String city = cityField.getText();
            String zipCode = zipCodeField.getText();

            // Check for empty fields
            if (street.isEmpty() || city.isEmpty() || zipCode.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled out.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (PreparedStatement pstmt = connection.prepareStatement("UPDATE Address SET street = ?, city = ?, zip_code = ? WHERE address_id = ?")) {
                pstmt.setString(1, street);
                pstmt.setString(2, city);
                pstmt.setString(3, zipCode);
                pstmt.setInt(4, addressId);
                pstmt.executeUpdate();
                loadAddressData();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating address: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void deleteAddress() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            return;
        }

        int addressId = (int) tableModel.getValueAt(selectedRow, 0);
        try (PreparedStatement pstmt = connection.prepareStatement("DELETE FROM Address WHERE address_id = ?")) {
            pstmt.setInt(1, addressId);
            pstmt.executeUpdate();
            loadAddressData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
