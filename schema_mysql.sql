-- Only needed if you switch from SQLite to MySQL (see README, "Using MySQL")
CREATE DATABASE IF NOT EXISTS librarydb;
USE librarydb;

CREATE TABLE IF NOT EXISTS books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    author VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    total INT NOT NULL,
    available INT NOT NULL
);
CREATE TABLE IF NOT EXISTS members (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    type VARCHAR(10) NOT NULL
);
CREATE TABLE IF NOT EXISTS issues (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    member_id INT NOT NULL,
    issue_date VARCHAR(10) NOT NULL,
    due_date VARCHAR(10) NOT NULL,
    return_date VARCHAR(10),
    fine DOUBLE DEFAULT 0,
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (member_id) REFERENCES members(id)
);
