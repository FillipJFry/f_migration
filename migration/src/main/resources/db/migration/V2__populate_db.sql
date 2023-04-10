DELETE FROM project_worker;
DELETE FROM project;
DELETE FROM client;
DELETE FROM worker;

INSERT INTO worker(name, birthday, level, salary) VALUES
 ('Vasya', '1995-02-03', 'Trainee', 100.01),
 ('Fry', '1996-02-29', 'Trainee', 100.02),
 ('Дід Панас', '1901-01-01', 'Senior', 99999.98),
 ('Teamlead', '1988-11-15', 'Senior', 99999.99),
 ('Richard Stallman', '1953-03-16', 'Middle', 85000.15),
 ('Steve Wozniak', '1950-08-11', 'Middle', 99999.98),
 ('Bob', '1982-07-26', 'Junior', 11000.00),
 ('Alice', '2001-04-19', 'Junior', 10000.00),
 ('Carol', '2001-04-19', 'Junior', 10000.00),
 ('Eve', '2001-04-19', 'Junior', 10000.00),
 ('Trent', '1982-09-01', 'Middle', 75000.67);

INSERT INTO client(name) VALUES('Simon'), ('Microsoft'), ('SpaceX'), ('UN'), ('ЗСУ');
SET @simon = (SELECT id FROM client WHERE name = 'Simon');
SET @microsoft = (SELECT id FROM client WHERE name = 'Microsoft');
SET @spacex = (SELECT id FROM client WHERE name = 'SpaceX');
SET @un = (SELECT id FROM client WHERE name = 'UN');
SET @zsu = (SELECT id FROM client WHERE name = 'ЗСУ');

INSERT INTO project VALUES
 (1, 'Strong XOR cryptography', @simon, '2023-01-01', '2023-07-31'),
 (2, 'Windows 12', @microsoft, '2023-01-01', '2023-07-31'),
 (3, 'bing search engine', @microsoft, '2020-08-02', '2023-12-31'),
 (4, 'Falcon 9 avionics', @spacex, '2005-02-12', '2008-12-20'),
 (5, 'Glaciers orbital observation', @un, '2001-10-01', '2002-04-16'),
 (6, 'assault drone', @zsu, '2014-07-01', '2022-07-01'),
 (7, 'Starship flight emulation system', @spacex, '2019-03-01', '2026-12-31'),
 (8, 'Minesweeper 3D', @microsoft, '2021-09-01', '2021-10-01'),
 (9, 'Next generation text editor', @un, '1999-01-15', '2007-04-07'),
 (10, 'Starlink satelites firmware', @spacex, '2019-03-01', '2026-12-20');

BEGIN TRANSACTION;
CREATE UNIQUE INDEX worker_name_tmp_idx ON worker(name);

/* Temporary tables won't work with foreign keys */
CREATE TEMPORARY TABLE proj_worker_name(
 project_id INTEGER NOT NULL,
 worker_name TEXT(1000) NOT NULL,
 PRIMARY KEY(project_id, worker_name),
 FOREIGN KEY(project_id) REFERENCES project(id));

INSERT INTO proj_worker_name VALUES
 (1, 'Vasya'), (1, 'Fry'),
 (2, 'Teamlead'), (2, 'Дід Панас'), (2, 'Trent'),
 (3, 'Teamlead'), (3, 'Bob'), (3, 'Alice'), (3, 'Fry'),
 (4, 'Steve Wozniak'),
 (5, 'Steve Wozniak'), (5, 'Eve'),
 (6, 'Дід Панас'), (6, 'Richard Stallman'), (6, 'Alice'),
 (7, 'Steve Wozniak'), (7, 'Teamlead'),
 (8, 'Teamlead'), (8, 'Дід Панас'), (8, 'Trent'),
 (9, 'Richard Stallman'),
 (10, 'Steve Wozniak'), (10, 'Teamlead');

INSERT INTO project_worker
 SELECT p.project_id, w.id
 FROM proj_worker_name p INNER JOIN worker w ON p.worker_name = w.name;

/* check if there are invalid worker names */
INSERT INTO project_worker
 SELECT p.project_id, NULL
 FROM proj_worker_name p LEFT JOIN worker w ON p.worker_name = w.name
 WHERE w.name IS NULL;

DROP TABLE proj_worker_name;
DROP INDEX worker_name_tmp_idx ON worker;
COMMIT;
