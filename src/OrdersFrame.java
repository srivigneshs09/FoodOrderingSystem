import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

class OrdersFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private Connection connection;

    public OrdersFrame(Connection connection) {
        this.connection = connection;
        setTitle("Order Details");
        setSize(800, 400);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[]{"Order ID", "Customer ID", "Restaurant ID", "Order Date", "Total Amount", "Status"}, 0);
        table = new JTable(tableModel);
        loadOrderData();

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

        addButton.addActionListener(e -> addOrder());
        updateButton.addActionListener(e -> updateOrder());
        deleteButton.addActionListener(e -> deleteOrder());
    }

    private void loadOrderData() {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM Orders");
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("restaurant_id"),
                        rs.getTimestamp("order_date"),
                        rs.getDouble("total_amount"),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addOrder() {
        JPanel panel = new JPanel(new GridLayout(5, 2));
        JTextField customerIdField = new JTextField();
        JTextField restaurantIdField = new JTextField();
        JTextField orderDateField = new JTextField();
        JTextField totalAmountField = new JTextField();
        JTextField statusField = new JTextField();

        panel.add(new JLabel("Customer ID:"));
        panel.add(customerIdField);
        panel.add(new JLabel("Restaurant ID:"));
        panel.add(restaurantIdField);
        panel.add(new JLabel("Order Date (YYYY-MM-DD HH:MM:SS):"));
        panel.add(orderDateField);
        panel.add(new JLabel("Total Amount:"));
        panel.add(totalAmountField);
        panel.add(new JLabel("Status:"));
        panel.add(statusField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add Order", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int customerId = Integer.parseInt(customerIdField.getText());
            int restaurantId = Integer.parseInt(restaurantIdField.getText());
            String orderDate = orderDateField.getText();
            double totalAmount = Double.parseDouble(totalAmountField.getText());
            String status = statusField.getText();

            try (PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO Orders (customer_id, restaurant_id, order_date, total_amount, status) VALUES (?, ?, ?, ?, ?)")) {
                pstmt.setInt(1, customerId);
                pstmt.setInt(2, restaurantId);
                pstmt.setString(3, orderDate);
                pstmt.setDouble(4, totalAmount);
                pstmt.setString(5, status);
                pstmt.executeUpdate();
                loadOrderData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateOrder() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update.");
            return;
        }

        int orderId = (int) tableModel.getValueAt(selectedRow, 0);
        int customerId = Integer.parseInt(JOptionPane.showInputDialog("Enter Customer ID:", tableModel.getValueAt(selectedRow, 1)));
        int restaurantId = Integer.parseInt(JOptionPane.showInputDialog("Enter Restaurant ID:", tableModel.getValueAt(selectedRow, 2)));
        String orderDate = JOptionPane.showInputDialog("Enter Order Date (YYYY-MM-DD HH:MM:SS):", tableModel.getValueAt(selectedRow, 3));
        double totalAmount = Double.parseDouble(JOptionPane.showInputDialog("Enter Total Amount:", tableModel.getValueAt(selectedRow, 4)));
        String status = JOptionPane.showInputDialog("Enter Status:", tableModel.getValueAt(selectedRow, 5));

        try (PreparedStatement pstmt = connection.prepareStatement(
                "UPDATE Orders SET customer_id = ?, restaurant_id = ?, order_date = ?, total_amount = ?, status = ? WHERE order_id = ?")) {
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, restaurantId);
            pstmt.setString(3, orderDate);
            pstmt.setDouble(4, totalAmount);
            pstmt.setString(5, status);
            pstmt.setInt(6, orderId);
            pstmt.executeUpdate();
            loadOrderData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteOrder() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            return;
        }

        int orderId = (int) tableModel.getValueAt(selectedRow, 0);
        try (PreparedStatement pstmt = connection.prepareStatement("DELETE FROM Orders WHERE order_id = ?")) {
            pstmt.setInt(1, orderId);
            pstmt.executeUpdate();
            loadOrderData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
