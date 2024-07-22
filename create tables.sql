CREATE database ATM6070_db;

USE atm6070_db;

CREATE TABLE Client
	(client_code INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	full_name VARCHAR(30) NOT NULL,
	phone VARCHAR(15) NOT NULL,
	email VARCHAR(30),
	pin INT(4) unsigned NOT NULL
	);

ALTER TABLE client AUTO_INCREMENT = 100;
    
CREATE TABLE Account
	(account_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	client_code INT NOT NULL REFERENCES Client(client_code),
	account_type_id INT NOT NULL,
	balance FLOAT(8,2)
	);
ALTER TABLE account AUTO_INCREMENT = 1000;    
CREATE TABLE AccountType
	(account_type_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	account_type VARCHAR(15) NOT NULL
	);
    
CREATE TABLE TransactionType
	(trans_type_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	transaction_type VARCHAR(15) NOT NULL
	);
    
CREATE TABLE Transaction
	(transaction_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	account_id INT NOT NULL REFERENCES Account(account_id),
	trans_type_id INT NOT NULL REFERENCES TransactionType(trans_type_id),
	amount FLOAT(8,2) NOT NULL
	);
    
CREATE TABLE ATMBalance
	(atmbalance_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	atm_balance INT NOT NULL
	);
    
CREATE USER 'admin'@'localhost' identified by '6070';








INSERT INTO atmbalance (atm_balance) VALUES (20000);

INSERT INTO account (client_code, account_type_id, balance)
VALUES (100, 1, 0);
INSERT INTO account (client_code, account_type_id, balance)
VALUES (101, 1, 0);
INSERT INTO account (client_code, account_type_id, balance)
VALUES (101, 2, 0);
INSERT INTO account (client_code, account_type_id, balance)
VALUES (100, 4, 0);
INSERT INTO account (client_code, account_type_id, balance)
VALUES (100, 3, 0);