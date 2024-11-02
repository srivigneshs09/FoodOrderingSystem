import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
class MenuFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private Connection connection;

    public MenuFrame(Connection connection) {
        this.connection = connection;
        setTitle("Menu Details");
        setSize(800, 400);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[]{"Menu ID", "Restaurant ID"}, 0);
        table = new JTable(tableModel);
        loadMenuData();

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

        addButton.addActionListener(e -> addMenu());
        updateButton.addActionListener(e -> updateMenu());
        deleteButton.addActionListener(e -> deleteMenu());
    }

    private void loadMenuData() {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM Menu");
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("menu_id"),
                        rs.getInt("restaurant_id")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addMenu() {
        String restaurantIdStr = JOptionPane.showInputDialog("Enter Restaurant ID:");
        int restaurantId = Integer.parseInt(restaurantIdStr);

        try (PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO Menu (restaurant_id) VALUES (?)")) {
            pstmt.setInt(1, restaurantId);
            pstmt.executeUpdate();
            loadMenuData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateMenu() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update.");
            return;
        }

        int menuId = (int) tableModel.getValueAt(selectedRow, 0);
        String restaurantIdStr = JOptionPane.showInputDialog("Enter Restaurant ID:", tableModel.getValueAt(selectedRow, 1));
        int restaurantId = Integer.parseInt(restaurantIdStr);

        try (PreparedStatement pstmt = connection.prepareStatement(
                "UPDATE Menu SET restaurant_id = ? WHERE menu_id = ?")) {
            pstmt.setInt(1, restaurantId);
            pstmt.setInt(2, menuId);
            pstmt.executeUpdate();
            loadMenuData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteMenu() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            return;
        }

        int menuId = (int) tableModel.getValueAt(selectedRow, 0);
        try (PreparedStatement pstmt = connection.prepareStatement("DELETE FROM Menu WHERE menu_id = ?")) {
            pstmt.setInt(1, menuId);
            pstmt.executeUpdate();
            loadMenuData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
