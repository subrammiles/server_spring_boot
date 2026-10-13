INSERT INTO course (name, version, created_at) VALUES ('Spring Boot in Depth', 0, CURRENT_TIMESTAMP);
INSERT INTO course (name, version, created_at) VALUES ('JPA and Hibernate', 0, CURRENT_TIMESTAMP);
INSERT INTO course (name, version, created_at) VALUES ('Microservices', 0, CURRENT_TIMESTAMP);

INSERT INTO review (rating, description, course_id, version) VALUES (5, 'Great course', 1, 0);
INSERT INTO review (rating, description, course_id, version) VALUES (4, 'Very practical', 1, 0);
INSERT INTO review (rating, description, course_id, version) VALUES (3, 'Average', 2, 0);

INSERT INTO passport (number, version) VALUES ('ERTYUII', 0);
INSERT INTO passport (number, version) VALUES ('RTYUIOP', 0);
INSERT INTO student (name, passport_id, version) VALUES ('Subbu', 1, 0);
INSERT INTO student (name, passport_id, version) VALUES ('Ravi', 2, 0);
INSERT INTO student_course (student_id, course_id) VALUES (1, 1);
INSERT INTO student_course (student_id, course_id) VALUES (1, 2);
INSERT INTO student_course (student_id, course_id) VALUES (2, 1);

INSERT INTO employee (employee_type, name, salary, version) VALUES ('FULL_TIME', 'Asha', 120000.00, 0);
INSERT INTO employee (employee_type, name, hourly_wage, version) VALUES ('PART_TIME', 'Kiran', 450.00, 0);
