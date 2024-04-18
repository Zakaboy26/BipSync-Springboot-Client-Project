delete from onboarding_employees;
INSERT INTO onboarding_employees (
                                  first_name,
                                  last_name,
                                  email_address,
                                  address,
                                  phone_number,
                                  emergency_contact_name,
                                  emergency_contact_phone_number,
                                  date_of_birth,
                                  bank_sort_code,
                                  bank_account_no,
                                  national_insurance_no,
                                  role,
                                  employment_status,
                                  start_date,
                                  Notes,
                                  induction_complete,
                                  image_file_path
    ) VALUES
        ( 'Brian', 'Busch','zaka_aap@hotmail.com', '123 Main St, London', '+44 20 1234 5678', 'Jane Doe', '+44 20 8765 4321', '1990-01-15', 123456, 98765432, 'AB123456C', 'Developer', 'Full Time', '2023-01-01', null, false, 'employee1_test_pic.jpg'),
        ( 'John', 'Dahl','johndahl@email.com', '456 Oak St, Manchester', '+44 161 987 6543', 'Bob Smith', '+44 161 123 4567', '1985-05-20', 654321, 12345678, 'XY987654A', 'Designer', 'Part Time', '2023-02-15', 'Requires a wheelchair-accessible workspace. Elevator access is preferred.', false,'employee2_test_pic.jpg'),
        ( 'Zakariya', 'Aden','zakariyaabdirizak@outlook.com', '789 Pine St, Birmingham', '+44 121 345 6789', 'Mary Johnson', '+44 121 876 5432', '1982-11-08', 333444, 87654321, 'CD345678B', 'Manager', 'Full Time', '2023-03-10', null, false, 'employee3_test_pic.jpg');

delete from checklist_tasks;
insert into checklist_tasks (title, description, type, deadline, created_at, started_at, completed_at, employee_id) values ( 'test task', 'currently just test data for a task','IT' ,'2023-11-01', '2023-10-13', '2023-10-15','2023-10-23', 1);
insert into checklist_tasks (title, description, type, deadline, created_at, started_at, completed_at, employee_id) values ( 'test task', 'currently just test data for a task','IT' ,'2023-11-01', '2023-10-13', '2023-10-15','2023-10-23', 2);
insert into checklist_tasks (title, description, type, deadline, created_at, started_at, completed_at, employee_id) values ( 'test task 2', 'currently just test data for a task','IT' ,'2023-11-01', '2023-10-13',null,null, 3);

DELETE FROM tasks_library;
INSERT INTO tasks_library (title, description, type) VALUES
     ('Employee Documentation', 'Collect and verify all necessary documents from the new employee', 'HR'),
     ('Benefits Enrollment', 'Guide the new employee through the benefits enrollment process', 'HR'),
     ('Policy Briefing', 'Brief the new employee about company policies', 'HR'),
     ('Payroll Setup', 'Setup the new employee in the payroll system', 'HR'),
     ('Employee Onboarding', 'Conduct a comprehensive onboarding process for the new employee', 'HR'),
     ('Setup Workstation', 'Setup the new employee workstation with necessary equipment and software', 'IT'),
     ('Work email', 'setup up work email address for the new employee', 'IT'),
     ('Security Training', 'Allow the new employee to complete security training', 'Security'),
     ('Access Fob', 'Setup access fob for the new employee', 'Security');


INSERT INTO users (username, password, enabled, captcha_verified, email, reset_token) VALUES ('adminuser', 'adminpassword', TRUE, 0, 'zakariyaabdirizak@outlook.com', null);
INSERT INTO authorities (username, authority) VALUES ('adminuser', 'ROLE_ADMIN');


UPDATE users SET password = '$2a$10$MZae.giqS8H3WytB9vaL7eQlUJOWfzmtNuW9SoXQ62PqQL5xHp0TW' WHERE username = 'adminuser';


INSERT INTO users (username, password, enabled, captcha_verified, email, reset_token) VALUES ('ituser', 'itpassword', TRUE, 0, 'zaka_aap@hotmail.com', null);
INSERT INTO authorities (username, authority) VALUES ('ituser', 'ROLE_IT');


UPDATE users SET password = '$2a$10$k1IeGH7uXR8BIKRajevD/.xj4jcAzQSZLeZJGKABtZYDOv4DGBMea' WHERE username = 'ituser';

INSERT INTO users (username, password, enabled, captcha_verified, email, reset_token) VALUES ('hruser', 'hrpassword', TRUE, 0, 'somalitech123@gmail.com', null);
INSERT INTO authorities (username, authority) VALUES ('hruser', 'ROLE_HR');

UPDATE users SET password = '$2a$10$dv3a0GnTQsE21sQJ67PfluXS5IuDdNOsZJFkX.GGTWGV0pP26GVVG' WHERE username = 'hruser';

