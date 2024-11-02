import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
class DeliveryPersonFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private Connection connection;

    public DeliveryPersonFrame(Connection connection) {
        this.connection = connection;
        setTitle("Delivery Person Details");
        setSize(800, 400);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[]{"Delivery Person ID", "Name", "Phone"}, 0);
        table = new JTable(tableModel);
        loadDeliveryPersonData();

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

        addButton.addActionListener(e -> addDeliveryPerson());
        updateButton.addActionListener(e -> updateDeliveryPerson());
        deleteButton.addActionListener(e -> deleteDeliveryPerson());
    }

    private void loadDeliveryPersonData() {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM DeliveryPerson");
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("delivery_person_id"),
                        rs.getString("name"),
                        rs.getString("phone")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addDeliveryPerson() {
        // Create a panel to hold the input fields
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Name:"));
        JTextField nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Phone:"));
        JTextField phoneField = new JTextField();
        panel.add(phoneField);

        // Show the dialog with the panel
        int result = JOptionPane.showConfirmDialog(this, panel, "Enter Delivery Person Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String phone = phoneField.getText();

            // Check for empty fields
            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled out.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (PreparedStatement pstmt = connection.prepareStatement("INSERT INTO DeliveryPerson (name, phone) VALUES (?, ?)")) {
                pstmt.setString(1, name);
                pstmt.setString(2, phone);
                pstmt.executeUpdate();
                loadDeliveryPersonData();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding delivery person: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateDeliveryPerson() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update.");
            return;
        }

        int deliveryPersonId = (int) tableModel.getValueAt(selectedRow, 0);

        // Create a panel to hold the input fields with pre-filled data
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Name:"));
        JTextField nameField = new JTextField((String) tableModel.getValueAt(selectedRow, 1));
        panel.add(nameField);

        panel.add(new JLabel("Phone:"));
        JTextField phoneField = new JTextField((String) tableModel.getValueAt(selectedRow, 2));
        panel.add(phoneField);

        // Show the dialog with the panel
        int result = JOptionPane.showConfirmDialog(this, panel, "Update Delivery Person Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String phone = phoneField.getText();

            // Check for empty fields
            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled out.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (PreparedStatement pstmt = connection.prepareStatement("UPDATE DeliveryPerson SET name = ?, phone = ? WHERE delivery_person_id = ?")) {
                pstmt.setString(1, name);
                pstmt.setString(2, phone);
                pstmt.setInt(3, deliveryPersonId);
                pstmt.executeUpdate();
                loadDeliveryPersonData();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating delivery person: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void deleteDeliveryPerson() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            return;
        }

        int deliveryPersonId = (int) tableModel.getValueAt(selectedRow, 0);
        try (PreparedStatement pstmt = connection.prepareStatement("DELETE FROM DeliveryPerson WHERE delivery_person_id = ?")) {
            pstmt.setInt(1, deliveryPersonId);
            pstmt.executeUpdate();
            loadDeliveryPersonData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
