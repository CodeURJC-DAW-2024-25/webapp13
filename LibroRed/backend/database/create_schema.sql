-- CREATE BASIC SCHEMA for the Librored Database
-- 1. Create the database
CREATE DATABASE IF NOT EXISTS librored;
USE librored;

-- 2. User Table
CREATE TABLE User (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(100) NOT NULL,
                      email VARCHAR(100) UNIQUE,
                      password VARCHAR(255) NOT NULL,
                      profile_pic VARCHAR(255),
                      role ENUM('Anonymous', 'Registered', 'Admin') NOT NULL
);

-- 3. Book Table
CREATE TABLE Book (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      owner_id INT,  -- The user who owns the book
                      title VARCHAR(255) NOT NULL,
                      author VARCHAR(255),
                      genre ENUM('Fiction', 'Non-Fiction', 'Mystery & Thriller', 'SciFi & Fantasy', 'Romance', 'Historical Fiction', 'Horror'),
                      description TEXT,
                      cover_pic VARCHAR(255),
                      FOREIGN KEY (owner_id) REFERENCES User(id) ON DELETE SET NULL
);

-- 4. Loan Table
CREATE TABLE Loan (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      book_id INT NOT NULL,
                      lender_id INT NOT NULL,
                      borrower_id INT NOT NULL,
                      start_date DATE NOT NULL,
                      end_date DATE,
                      status ENUM('Active', 'Completed') DEFAULT 'Active',
                      FOREIGN KEY (book_id) REFERENCES Book(id) ON DELETE CASCADE,
                      FOREIGN KEY (lender_id) REFERENCES User(id) ON DELETE CASCADE,
                      FOREIGN KEY (borrower_id) REFERENCES User(id) ON DELETE CASCADE
);

-- 5. Review Table
CREATE TABLE Review (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        book_id INT NOT NULL,
                        user_id INT NOT NULL,
                        rating ENUM('1', '2', '3', '4', '5') NOT NULL,  -- Using ENUM instead of CHECK
                        comment TEXT,
                        review_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- Fix for MySQL compatibility
                        FOREIGN KEY (book_id) REFERENCES Book(id) ON DELETE CASCADE,
                        FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE
);


-- WARNING!!! CUIDADO!!! ATTENTION!!!
-- DO NOT EXECUTE ANY OF THE FOLLOWING QUERIES IF YOU DON'T KNOW WHAT YOU ARE DOING
-- THESE QUERIES WILL DELETE ALL THE DATA IN THE DATABASE

-- DROP SCHEMA for the Librored Database
-- Drop tables if they already exist
DROP TABLE IF EXISTS Review;
DROP TABLE IF EXISTS Loan;
DROP TABLE IF EXISTS Book;
DROP TABLE IF EXISTS User;
