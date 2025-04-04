-- Insert Users
INSERT INTO "User" (oauthId, oauthProvider, user_type) VALUES
(101, 1, 'BUYER'),
(102, 2, 'BUYER'),
(103, 3, 'VENDOR'),
(104, 4, 'VENDOR'),
-- (105, 5, 'VENDOR'),
(106, 5, 'ADMIN');

-- Insert Buyers
INSERT INTO BUYER (oauthId, firstName, lastName, email) VALUES
(101, 'John', 'Doe', 'john.doe@example.com'),
(102, 'Jane', 'Smith', 'jane.smith@example.com');

-- Insert Vendors
INSERT INTO VENDOR (oauthId, storeName) VALUES
(103, 'Tech Store'),
(104, 'Gadget Hub');
(106, 'Dummy Vendor');

-- Insert Addresses (for Buyers)
INSERT INTO addresses (id, oauthId, street, city, state, postal_code, country) VALUES 
(201, 101, '123 Main St', 'Springfield', 'IL', '62704', 'USA'),
(202, 102, '456 Elm St', 'Springfield', 'IL', '62705', 'USA');

-- Insert Products
INSERT INTO products (id, name, price, description, oauthId) VALUES
(301, 'Laptop', 1200.00, 'High-performance laptop', 103),
(302, 'Smartphone', 800.00, 'Latest model smartphone', 104);

-- Insert Orders
-- Note: The foreign key column for the Buyer is "oauthId" based on the @JoinColumn annotation in the Order entity.
INSERT INTO "Order" (id, oauthId, status, orderDate) VALUES
(401, 101, 'PENDING', '2025-04-01T10:00:00'),
(402, 102, 'DELIVERED', '2025-03-30T15:30:00');

-- Insert OrderItems
-- Note: The product details are now stored as snapshots in the OrderItem table.
INSERT INTO OrderItem (id, order_id, productName, productPrice, productDescription, quantity, priceAtPurchase) VALUES
(501, 401, 'Laptop', 1200.00, 'High-performance laptop', 1, 1200.00),
(502, 402, 'Smartphone', 800.00, 'Latest model smartphone', 2, 1600.00);

-- Insert Payments
-- Note: The foreign key column for Order is "order_id" based on the @JoinColumn annotation in the Payment entity.
INSERT INTO Payment (id, order_id, stripePaymentId, amount, status, paymentDate) VALUES
(601, 401, 'pi_123456789', 1200.00, 'PENDING', '2025-04-01T10:30:00'),
(602, 402, 'pi_987654321', 1600.00, 'COMPLETED', '2025-03-30T16:00:00');