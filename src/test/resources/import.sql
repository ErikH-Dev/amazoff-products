-- Insert Users
INSERT INTO "User" (oauthId, oauthProvider, user_type) VALUES
(101, 1, 'BUYER'),
(102, 2, 'BUYER'),
(103, 3, 'VENDOR'),
(104, 4, 'VENDOR');

-- Insert Buyers
INSERT INTO BUYER (oauthId, firstName, lastName, email) VALUES
(101, 'John', 'Doe', 'john.doe@example.com'),
(102, 'Bopke', 'Billekoek', 'bopke.billekoek@example.com');

-- Insert Vendors
INSERT INTO VENDOR (oauthId, storeName) VALUES
(103, 'Tech Store'),
(104, 'Gadget Hub');

-- Insert Addresses (for Buyers)
INSERT INTO addresses (id, oauthId, street, city, state, postal_code, country) VALUES 
(101, 101, '123 Main St', 'Springfield', 'IL', '62704', 'USA'),
(102, 102, '456 Elm St', 'Springfield', 'IL', '62705', 'USA');

-- Insert Products
INSERT INTO products (id, name, price, description) VALUES
(101, 'Laptop', 1200.00, 'High-performance laptop'),
(102, 'Smartphone', 800.00, 'Latest model smartphone');
