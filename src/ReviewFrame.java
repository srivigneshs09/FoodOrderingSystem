import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

class ReviewFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private Connection connection;

    public ReviewFrame(Connection connection) {
        this.connection = connection;
        setTitle("Review Details");
        setSize(800, 400);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[]{"Review ID", "Order ID", "Customer ID", "Rating", "Comments"}, 0);
        table = new JTable(tableModel);
        loadReviewData();

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

        addButton.addActionListener(e -> addReview());
        updateButton.addActionListener(e -> updateReview());
        deleteButton.addActionListener(e -> deleteReview());
    }

    private void loadReviewData() {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM Review");
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("review_id"),
                        rs.getInt("order_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("rating"),
                        rs.getString("comments")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addReview() {
        // Custom dialog to get all review details at once
        JPanel panel = new JPanel(new GridLayout(0, 2));
        JTextField orderIdField = new JTextField();
        JTextField customerIdField = new JTextField();
        JTextField ratingField = new JTextField();
        JTextField commentsField = new JTextField();

        panel.add(new JLabel("Order ID:"));
        panel.add(orderIdField);
        panel.add(new JLabel("Customer ID:"));
        panel.add(customerIdField);
        panel.add(new JLabel("Rating (1-5):"));
        panel.add(ratingField);
        panel.add(new JLabel("Comments:"));
        panel.add(commentsField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Review", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int orderId = Integer.parseInt(orderIdField.getText());
                int customerId = Integer.parseInt(customerIdField.getText());
                int rating = Integer.parseInt(ratingField.getText());
                String comments = commentsField.getText();

                try (PreparedStatement pstmt = connection.prepareStatement(
                        "INSERT INTO Review (order_id, customer_id, rating, comments) VALUES (?, ?, ?, ?)")) {
                    pstmt.setInt(1, orderId);
                    pstmt.setInt(2, customerId);
                    pstmt.setInt(3, rating);
                    pstmt.setString(4, comments);
                    pstmt.executeUpdate();
                    loadReviewData();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for Order ID, Customer ID, and Rating.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateReview() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update.");
            return;
        }

        int reviewId = (int) tableModel.getValueAt(selectedRow, 0);
        int orderId = Integer.parseInt(JOptionPane.showInputDialog("Enter Order ID:", tableModel.getValueAt(selectedRow, 1)));
        int customerId = Integer.parseInt(JOptionPane.showInputDialog("Enter Customer ID:", tableModel.getValueAt(selectedRow, 2)));
        int rating = Integer.parseInt(JOptionPane.showInputDialog("Enter Rating (1-5):", tableModel.getValueAt(selectedRow, 3)));
        String comments = JOptionPane.showInputDialog("Enter Comments:", tableModel.getValueAt(selectedRow, 4));

        try (PreparedStatement pstmt = connection.prepareStatement(
                "UPDATE Review SET order_id = ?, customer_id = ?, rating = ?, comments = ? WHERE review_id = ?")) {
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, customerId);
            pstmt.setInt(3, rating);
            pstmt.setString(4, comments);
            pstmt.setInt(5, reviewId);
            pstmt.executeUpdate();
            loadReviewData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteReview() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            return;
        }

        int reviewId = (int) tableModel.getValueAt(selectedRow, 0);
        try (PreparedStatement pstmt = connection.prepareStatement("DELETE FROM Review WHERE review_id = ?")) {
            pstmt.setInt(1, reviewId);
            pstmt.executeUpdate();
            loadReviewData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
