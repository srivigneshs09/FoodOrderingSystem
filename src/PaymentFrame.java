import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

class PaymentFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private Connection connection;

    public PaymentFrame(Connection connection) {
        this.connection = connection;
        setTitle("Payment Details");
        setSize(800, 400);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[]{"Payment ID", "Order ID", "Amount", "Payment Date", "Method"}, 0);
        table = new JTable(tableModel);
        loadPaymentData();

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

        addButton.addActionListener(e -> addPayment());
        updateButton.addActionListener(e -> updatePayment());
        deleteButton.addActionListener(e -> deletePayment());
    }

    private void loadPaymentData() {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM Payment");
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("payment_id"),
                        rs.getInt("order_id"),
                        rs.getDouble("amount"),
                        rs.getTimestamp("payment_date"),
                        rs.getString("method")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addPayment() {
        JTextField orderIdField = new JTextField();
        JTextField amountField = new JTextField();
        JTextField paymentDateField = new JTextField();
        JTextField methodField = new JTextField();

        Object[] message = {
                "Order ID:", orderIdField,
                "Amount:", amountField,
                "Payment Date (YYYY-MM-DD HH:MM:SS):", paymentDateField,
                "Payment Method:", methodField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Payment", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            int orderId = Integer.parseInt(orderIdField.getText());
            double amount = Double.parseDouble(amountField.getText());
            String paymentDate = paymentDateField.getText();
            String method = methodField.getText();

            try (PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO Payment (order_id, amount, payment_date, method) VALUES (?, ?, ?, ?)")) {
                pstmt.setInt(1, orderId);
                pstmt.setDouble(2, amount);
                pstmt.setString(3, paymentDate);
                pstmt.setString(4, method);
                pstmt.executeUpdate();
                loadPaymentData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updatePayment() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update.");
            return;
        }

        int paymentId = (int) tableModel.getValueAt(selectedRow, 0);
        JTextField orderIdField = new JTextField(String.valueOf(tableModel.getValueAt(selectedRow, 1)));
        JTextField amountField = new JTextField(String.valueOf(tableModel.getValueAt(selectedRow, 2)));
        JTextField paymentDateField = new JTextField(String.valueOf(tableModel.getValueAt(selectedRow, 3)));
        JTextField methodField = new JTextField(String.valueOf(tableModel.getValueAt(selectedRow, 4)));

        Object[] message = {
                "Order ID:", orderIdField,
                "Amount:", amountField,
                "Payment Date (YYYY-MM-DD HH:MM:SS):", paymentDateField,
                "Payment Method:", methodField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Update Payment", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            int orderId = Integer.parseInt(orderIdField.getText());
            double amount = Double.parseDouble(amountField.getText());
            String paymentDate = paymentDateField.getText();
            String method = methodField.getText();

            try (PreparedStatement pstmt = connection.prepareStatement(
                    "UPDATE Payment SET order_id = ?, amount = ?, payment_date = ?, method = ? WHERE payment_id = ?")) {
                pstmt.setInt(1, orderId);
                pstmt.setDouble(2, amount);
                pstmt.setString(3, paymentDate);
                pstmt.setString(4, method);
                pstmt.setInt(5, paymentId);
                pstmt.executeUpdate();
                loadPaymentData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void deletePayment() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            return;
        }

        int paymentId = (int) tableModel.getValueAt(selectedRow, 0);
        try (PreparedStatement pstmt = connection.prepareStatement("DELETE FROM Payment WHERE payment_id = ?")) {
            pstmt.setInt(1, paymentId);
            pstmt.executeUpdate();
            loadPaymentData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
