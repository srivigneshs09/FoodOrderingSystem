CREATE DATABASE FoodOrderingSystem;

USE FoodOrderingSystem;

-- Customer Table
CREATE TABLE Customer (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(15),
    address_id INT,
    FOREIGN KEY (address_id) REFERENCES Address(address_id)
);

-- Restaurant Table
CREATE TABLE Restaurant (
    restaurant_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    location VARCHAR(200)
);

-- Menu Table
CREATE TABLE Menu (
    menu_id INT PRIMARY KEY AUTO_INCREMENT,
    restaurant_id INT,
    FOREIGN KEY (restaurant_id) REFERENCES Restaurant(restaurant_id)
);

-- Dishes Table
CREATE TABLE Dishes (
    dish_id INT PRIMARY KEY AUTO_INCREMENT,
    menu_id INT,
    name VARCHAR(100),
    price DECIMAL(10, 2),
    FOREIGN KEY (menu_id) REFERENCES Menu(menu_id)
);

-- Order Table
CREATE TABLE Orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT,
    restaurant_id INT,
    order_date DATETIME,
    total_amount DECIMAL(10, 2),
    status VARCHAR(50),
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id),
    FOREIGN KEY (restaurant_id) REFERENCES Restaurant(restaurant_id)
);

-- DeliveryPerson Table
CREATE TABLE DeliveryPerson (
    delivery_person_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    phone VARCHAR(15)
);

-- Address Table
CREATE TABLE Address (
    address_id INT PRIMARY KEY AUTO_INCREMENT,
    street VARCHAR(100),
    city VARCHAR(50),
    zip_code VARCHAR(10)
);

-- Payment Table
CREATE TABLE Payment (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    amount DECIMAL(10, 2),
    payment_date DATETIME,
    method VARCHAR(50),
    FOREIGN KEY (order_id) REFERENCES Orders(order_id)
);

-- Review Table
CREATE TABLE Review (
    review_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    customer_id INT,
    rating INT CHECK(rating BETWEEN 1 AND 5),
    comments TEXT,
    FOREIGN KEY (order_id) REFERENCES Orders(order_id),
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id)
);

select * from customer;
select * from restaurant;
select * from address;
select * from deliveryperson;
select * from dishes;
select * from menu;
select * from orders;
select * from payment;
select * from review;

DELETE FROM orders;

SET SQL_SAFE_UPDATES = 0;


-- Insert sample customers
INSERT INTO Customer (name, email, phone) VALUES 
('John Doe', 'john@example.com', '1234567890'),
('Jane Smith', 'jane@example.com', '0987654321'),
('Alice Johnson', 'alice@example.com', '2345678901');

-- Insert sample restaurants
INSERT INTO Restaurant (name, location) VALUES 
('Pasta Palace', 'Downtown'),
('Burger Bonanza', 'Uptown'),
('Sushi Station', 'Midtown');

-- Insert sample menus for each restaurant
INSERT INTO Menu (restaurant_id) VALUES 
(1), -- Pasta Palace
(2), -- Burger Bonanza
(3); -- Sushi Station

-- Insert sample dishes for the menus (Assuming there is a Dishes table)
INSERT INTO Dishes (menu_id, name, price) VALUES 
(1, 'Spaghetti Carbonara', 12.99),
(1, 'Fettuccine Alfredo', 11.99),
(2, 'Cheeseburger', 9.99),
(2, 'Veggie Burger', 8.99),
(3, 'California Roll', 7.99),
(3, 'Spicy Tuna Roll', 8.99);

-- Insert sample orders
INSERT INTO Orders (order_id, customer_id, restaurant_id, order_date, total_amount, status) VALUES
(1, 1, 1, '2024-11-01 10:00:00', 45.99, 'Completed'),
(2, 2, 1, '2024-11-02 11:00:00', 32.50, 'Completed'),
(3, 3, 2, '2024-11-03 12:00:00', 78.20, 'Completed'),
(4, 4, 2, '2024-11-04 13:00:00', 12.99, 'Completed'),
(5, 5, 3, '2024-11-05 14:00:00', 60.75, 'Completed'),
(6, 6, 3, '2024-11-06 15:00:00', 45.00, 'Completed'),
(7, 7, 4, '2024-11-07 16:00:00', 88.40, 'Completed'),
(8, 8, 4, '2024-11-08 17:00:00', 25.00, 'Completed'),
(9, 9, 5, '2024-11-09 18:00:00', 99.99, 'Completed'),
(10, 10, 5, '2024-11-10 19:00:00', 55.55, 'Completed');


-- Insert sample reviews
INSERT INTO Review (order_id, customer_id, rating, comments) VALUES 
(1, 1, 5, 'Absolutely delicious! Will order again.'),
(2, 2, 4, 'Good, but the burger was a bit cold.'),
(3, 3, 5, 'Best sushi in town!');

INSERT INTO Address (street, city, zip_code) VALUES
('123 Maple Street', 'Springfield', '12345'),
('456 Oak Avenue', 'Riverdale', '23456'),
('789 Pine Road', 'Hill Valley', '34567'),
('101 Elm Street', 'Metropolis', '45678'),
('202 Birch Blvd', 'Gotham', '56789'),
('303 Cedar Lane', 'Smallville', '67890'),
('404 Willow Drive', 'Star City', '78901'),
('505 Ash Court', 'Sunnydale', '89012'),
('606 Walnut Place', 'Twin Peaks', '90123'),
('707 Poplar Way', 'Hawkins', '01234');


INSERT INTO DeliveryPerson (name, phone) VALUES
('John Doe', '555-1234'),
('Jane Smith', '555-5678'),
('Mike Johnson', '555-8765'),
('Emily Brown', '555-4321'),
('Chris Davis', '555-3456'),
('Patricia Taylor', '555-7890'),
('Robert Wilson', '555-2345'),
('Linda Thompson', '555-6789'),
('David Martinez', '555-8901'),
('Barbara Garcia', '555-9012');

INSERT INTO Payment (order_id, amount, payment_date, method) VALUES
(1, 45.99, '2024-11-01 14:30:00', 'Credit Card'),
(2, 32.50, '2024-11-02 10:15:00', 'Cash'),
(3, 78.20, '2024-11-03 18:45:00', 'Debit Card'),
(4, 12.99, '2024-11-04 12:00:00', 'Credit Card'),
(5, 60.75, '2024-11-05 16:30:00', 'Online Transfer'),
(6, 45.00, '2024-11-06 09:45:00', 'Cash'),
(7, 88.40, '2024-11-07 19:15:00', 'Credit Card'),
(8, 25.00, '2024-11-08 13:30:00', 'Debit Card'),
(9, 99.99, '2024-11-09 11:00:00', 'Online Transfer'),
(10, 55.55, '2024-11-10 15:45:00', 'Credit Card');


