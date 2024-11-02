import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

class RestaurantFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private Connection connection;

    public RestaurantFrame(Connection connection) {
        this.connection = connection;
        setTitle("Restaurant Details");
        setSize(800, 400);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Location"}, 0);
        table = new JTable(tableModel);
        loadRestaurantData();

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

        addButton.addActionListener(e -> addRestaurant());
        updateButton.addActionListener(e -> updateRestaurant());
        deleteButton.addActionListener(e -> deleteRestaurant());
    }

    private void loadRestaurantData() {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM Restaurant");
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("restaurant_id"),
                        rs.getString("name"),
                        rs.getString("location")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addRestaurant() {
        JDialog dialog = new JDialog(this, "Add Restaurant", true);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);

        JPanel dialogPanel = new JPanel(new GridLayout(0, 2));
        dialogPanel.add(new JLabel("Name:"));
        JTextField nameField = new JTextField();
        dialogPanel.add(nameField);

        dialogPanel.add(new JLabel("Location:"));
        JTextField locationField = new JTextField();
        dialogPanel.add(locationField);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            String name = nameField.getText();
            String location = locationField.getText();

            try (PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO Restaurant (name, location) VALUES (?, ?)")) {
                pstmt.setString(1, name);
                pstmt.setString(2, location);
                pstmt.executeUpdate();
                loadRestaurantData();
                dialog.dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        dialogPanel.add(okButton);
        dialog.getContentPane().add(dialogPanel);
        dialog.setVisible(true);
    }

    private void updateRestaurant() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update.");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String name = JOptionPane.showInputDialog("Enter Name:", tableModel.getValueAt(selectedRow, 1));
        String location = JOptionPane.showInputDialog("Enter Location:", tableModel.getValueAt(selectedRow, 2));

        try (PreparedStatement pstmt = connection.prepareStatement(
                "UPDATE Restaurant SET name = ?, location = ? WHERE restaurant_id = ?")) {
            pstmt.setString(1, name);
            pstmt.setString(2, location);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
            loadRestaurantData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteRestaurant() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        try (PreparedStatement pstmt = connection.prepareStatement("DELETE FROM Restaurant WHERE restaurant_id = ?")) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            loadRestaurantData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
