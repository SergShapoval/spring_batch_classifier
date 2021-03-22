CREATE SCHEMA userTest;
USE
userTest;
CREATE TABLE User
(
    id     INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name   VARCHAR(255) NOT NULL,
    salary INT(11) NOT NULL
);
INSERT INTO User(name, salary)
VALUES ('name1', 100),
       ('name2', 200),
       ('name3', 300),
       ('name4', 400),
       ('name5', 500),
       ('name6', 600),
       ('name7', 700),
       ('name8', 800),
       ('name9', 900),
       ('name10', 1000),
       ('name11', 1100),
       ('name12', 1200),
       ('name13', 1300);
