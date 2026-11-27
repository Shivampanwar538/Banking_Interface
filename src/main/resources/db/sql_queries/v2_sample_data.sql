-- branch table
insert into branch (branch_id, name, ifsc_code, address, city, state, postal_code) values
    (1,'Main Branch', 'MNBR0001234','1 Sec17 c', 'Chandigarh', 'Chandigarh', '160017'),
    (2,'Branch 1', 'MHBR0001122','4 sec34', 'Mohali', 'Punjab', '160055'),
    (3,'Branch 2', 'PKBR0001133','3 Sec23 c', 'Panchkula', 'Haryana', '134109');

-- customer table
insert into customer (customer_id, first_name, last_name, email, phone_number, address, date_of_birth) values
    (1,'Vijay', 'Singh','vijay@example.com', '9876543210', '123 Main St, Chandigarh', '1990-01-01'),
    (2,'Amit', 'Kumar','amit@example.com', '9876543211', '456 Oak Ave, Mohali', '1985-05-15'),
    (3,'Sneha', 'Sharma','sneha@example.com', '9876543212', '789 Pine Rd, Panchkula', '1992-08-20'),
    (4,'Rohit', 'Verma','rohit@example.com', '9876543213', '101 Elm St, Chandigarh', '1988-12-10'),
    (5,'Priya', 'Gupta','priya@example.com', '9876543214', '202 Maple Dr, Panchkula', '1991-03-25');

-- account table
insert into account (account_id, customer_id, branch_id, account_number, balance, status) values
    (1, 1, 1, 'ACC1000001', 1000.00, 'ACTIVE'),
    (2, 2, 2, 'ACC1000002', 1500.00, 'ACTIVE'),
    (3, 3, 3, 'ACC1000003', 2000.00, 'ACTIVE'),
    (4, 4, 1, 'ACC1000004', 2500.00, 'ACTIVE'),
    (5, 5, 2, 'ACC1000005', 3000.00, 'ACTIVE'),
    (6, 1, 3, 'ACC1000006', 3500.00, 'ACTIVE'),
    (7, 2, 1, 'ACC1000007', 4000.00, 'ACTIVE'),
    (8, 3, 2, 'ACC1000008', 4500.00, 'ACTIVE'),
    (9, 4, 3, 'ACC1000009', 5000.00, 'ACTIVE'),
    (10, 5, 1, 'ACC1000010', 5500.00, 'ACTIVE'),
    (11, 1, 1, 'ACC1000011', 10000.00, 'ACTIVE'),
    (12, 2, 2, 'ACC2000014', 5000.00, 'ACTIVE'),
    (13, 3, 3, 'ACC3000001', 800.00, 'INACTIVE'),
    (14, 4, 1, 'ACC3000002', 800.00, 'FROZEN'),
    (15, 5, 2, 'ACC3000003', 0.00, 'CLOSED');