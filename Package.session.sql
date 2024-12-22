CREATE TABLE packages (
    id SERIAL PRIMARY KEY,                -- Unique ID for each package
    sender_name VARCHAR(100) NOT NULL,    -- Name of the sender
    receiver_name VARCHAR(100) NOT NULL,  -- Name of the receiver
    destination VARCHAR(200) NOT NULL,    -- Destination address
    weight NUMERIC(10, 2) NOT NULL,       -- Weight of the package in kg
    shipping_date DATE NOT NULL           -- Shipping date of the package
);
