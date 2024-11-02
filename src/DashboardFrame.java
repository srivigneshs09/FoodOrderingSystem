import javax.swing.*;
import java.awt.*;

class DashboardFrame extends JFrame {
    public DashboardFrame(FoodOrderingSystemGUI mainApp) {
        setTitle("Dashboard - Food Ordering System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 3, 10, 10));

        // Add buttons for each table with action listeners to open the respective frames
        JButton customerButton = new JButton("Customer Details");
        customerButton.addActionListener(e -> new CustomerFrame(mainApp.getConnection()));

        JButton restaurantButton = new JButton("Restaurant Details");
        restaurantButton.addActionListener(e -> new RestaurantFrame(mainApp.getConnection()));

        JButton menuButton = new JButton("Menu Details");
        menuButton.addActionListener(e -> new MenuFrame(mainApp.getConnection()));

        JButton dishesButton = new JButton("Dishes Details");
        dishesButton.addActionListener(e -> new DishesFrame(mainApp.getConnection()));

        JButton ordersButton = new JButton("Order Details");
        ordersButton.addActionListener(e -> new OrdersFrame(mainApp.getConnection()));

        JButton deliveryPersonButton = new JButton("Delivery Person Details");
        deliveryPersonButton.addActionListener(e -> new DeliveryPersonFrame(mainApp.getConnection()));

        JButton addressButton = new JButton("Address Details");
        addressButton.addActionListener(e -> new AddressFrame(mainApp.getConnection()));

        JButton paymentButton = new JButton("Payment Details");
        paymentButton.addActionListener(e -> new PaymentFrame(mainApp.getConnection()));

        JButton reviewButton = new JButton("Review Details");
        reviewButton.addActionListener(e -> new ReviewFrame(mainApp.getConnection()));

        // Add all buttons to the panel
        panel.add(customerButton);
        panel.add(restaurantButton);
        panel.add(menuButton);
        panel.add(dishesButton);
        panel.add(ordersButton);
        panel.add(deliveryPersonButton);
        panel.add(addressButton);
        panel.add(paymentButton);
        panel.add(reviewButton);

        add(panel);
        setVisible(true);
    }
}

