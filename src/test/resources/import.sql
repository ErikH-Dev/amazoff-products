-- Insert Users (Base Table)
INSERT INTO `User` (oauthId, oauthProvider, user_type) VALUES 
(1, 101, 'BUYER'),  -- Buyer user
(2, 102, 'ADMIN'),  -- Admin user
(3, 103, 'VENDOR'); -- Vendor user

-- Insert Buyers
INSERT INTO `User` (oauthId, oauthProvider, user_type, firstName, lastName, email) VALUES
(1, 101, 'BUYER', 'John', 'Doe', 'john.doe@example.com');

-- Insert Admins
-- Admins do not have additional fields, so no extra data is needed beyond the base `User` table.

-- Insert Vendors
INSERT INTO `User` (oauthId, oauthProvider, user_type, storeName) VALUES
(3, 103, 'VENDOR', 'Tech Store');

-- Insert Addresses (for Buyers)
INSERT INTO addresses (id, user_id, street, city, state, postal_code, country) VALUES 
(1, 1, '123 Main St', 'Springfield', 'IL', '62704', 'USA'), -- Address for Buyer with oauthId 1
(2, 1, '456 Elm St', 'Springfield', 'IL', '62705', 'USA'); -- Another address for Buyer with oauthId 1

-- Insert Products
INSERT INTO products (id, name, price, description) VALUES
(1, 'Laptop', 1200.00, 'High-performance laptop'), -- Valid product
(2, 'Smartphone', 800.00, 'Latest model smartphone'); -- Another valid product