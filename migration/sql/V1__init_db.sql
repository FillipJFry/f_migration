/* CREATE DATABASE megasoft; */
DROP TABLE IF EXISTS project_worker;
DROP TABLE IF EXISTS project;
DROP TABLE IF EXISTS client;
DROP TABLE IF EXISTS worker;

CREATE TABLE worker(
 id INTEGER AUTO_INCREMENT PRIMARY KEY,
 name TEXT(1000) NOT NULL,
 birthday DATE NOT NULL,
 level ENUM('Trainee','Junior','Middle','Senior') NOT NULL,
 salary DECIMAL(7, 2) NOT NULL);

ALTER TABLE worker ADD CONSTRAINT name_min_length CHECK (char_length(name) >= 2);
ALTER TABLE worker ADD CONSTRAINT birthday_lower_limit CHECK (birthday >= '1901-01-01');
ALTER TABLE worker ADD CONSTRAINT salary_range CHECK (100 < salary);

CREATE TABLE client(
 id INTEGER AUTO_INCREMENT PRIMARY KEY,
 name TEXT(1000) NOT NULL,
 CONSTRAINT client_name_min_length CHECK (char_length(name) >= 2));

CREATE TABLE project(
 id INTEGER NOT NULL PRIMARY KEY,
 name VARCHAR(150) NOT NULL,
 client_id INTEGER NOT NULL,
 start_date DATE NOT NULL,
 finish_date DATE NOT NULL,
 CONSTRAINT proj_dates_valid CHECK (start_date <= finish_date),
 CONSTRAINT proj_duration CHECK (DATEDIFF(MONTH, start_date, finish_date) BETWEEN 1 AND 99),
 FOREIGN KEY(client_id) REFERENCES client(id));

CREATE TABLE project_worker(
 project_id INTEGER NOT NULL,
 worker_id INTEGER NOT NULL,
 PRIMARY KEY(project_id, worker_id),
 FOREIGN KEY(project_id) REFERENCES project(id),
 FOREIGN KEY(worker_id) REFERENCES worker(id));
