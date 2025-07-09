CREATE DATABASE banking_db;
USE banking_db;

CREATE TABLE Branch (
    branch_id INT PRIMARY KEY,
    branch_name VARCHAR(50) NOT NULL,
    location VARCHAR(100) NOT NULL
);

CREATE TABLE Customer (
    customer_id INT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    address VARCHAR(100),
    phone VARCHAR(15)
);

CREATE TABLE Account (
    account_number INT PRIMARY KEY,
    customer_id INT,
    branch_id INT,
    account_type ENUM('Savings', 'Checking') NOT NULL,
    balance DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id),
    FOREIGN KEY (branch_id) REFERENCES Branch(branch_id)
);

CREATE TABLE Transaction (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    account_number INT,
    transaction_type ENUM('Deposit', 'Withdrawal', 'Transfer') NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    transaction_date DATETIME NOT NULL,
    FOREIGN KEY (account_number) REFERENCES Account(account_number)
);

CREATE INDEX idx_customer_id ON Account(customer_id);
CREATE INDEX idx_account_number ON Transaction(account_number);
CREATE INDEX idx_transaction_date ON Transaction(transaction_date);

-- Insert Sample Data
INSERT INTO Branch (branch_id, branch_name, location) VALUES
(1, 'Main Branch', '123 Main St, City'),
(2, 'Downtown Branch', '456 Elm St, City');

INSERT INTO Customer (customer_id, first_name, last_name, address, phone) VALUES
(1, 'John', 'Doe', '789 Oak St', '555-0101'),
(2, 'Jane', 'Smith', '321 Pine St', '555-0102');

INSERT INTO Account (account_number, customer_id, branch_id, account_type, balance) VALUES
(1001, 1, 1, 'Savings', 5000.00),
(1002, 2, 2, 'Checking', 2000.00);

INSERT INTO Transaction (account_number, transaction_type, amount, transaction_date) VALUES
(1001, 'Deposit', 1000.00, '2025-05-01 10:00:00'),
(1001, 'Withdrawal', 500.00, '2025-05-02 12:00:00'),
(1002, 'Deposit', 1500.00, '2025-05-03 09:00:00');
