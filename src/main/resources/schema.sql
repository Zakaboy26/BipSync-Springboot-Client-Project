DROP TABLE IF EXISTS authorities;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS checklist_tasks;
drop table if exists onboarding_employees;

CREATE TABLE IF NOT EXISTS users
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    enabled  BOOLEAN NOT NULL DEFAULT TRUE,
    captcha_verified TINYINT(1) NOT NULL DEFAULT 0,
    email VARCHAR(128) UNIQUE,
    reset_token VARCHAR(100)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS authorities (
                                           username VARCHAR(50) NOT NULL,
                                           authority VARCHAR(50) NOT NULL,
                                           FOREIGN KEY (username) REFERENCES users(username)
) ENGINE = InnoDB;

create table if not exists onboarding_employees
(
    id                 bigint auto_increment primary key,
    first_name         varchar(50) not null,
    last_name          varchar(50) not null,
    email_address      varchar(128) UNIQUE NOT NULL CHECK (email_address REGEXP '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$'),
    address            varchar(128),
    phone_number       varchar(20),
    emergency_contact_name  varchar(128),
    emergency_contact_phone_number  varchar(20),
    date_of_birth      DATE,
    bank_sort_code     INT(6) CHECK (bank_sort_code BETWEEN 100000 AND 999999),
    bank_account_no    INT(8) CHECK (bank_account_no BETWEEN 10000000 AND 99999999),
    national_insurance_no varchar(9) CHECK (national_insurance_no REGEXP '^$|^[A-Z]{2}[0-9]{6}[A-Z]$'),
    role               varchar(128),
    employment_status  ENUM ('Full Time', 'Part Time', 'Contractor', 'Intern'),
    start_date         DATE,
    Notes              varchar(2048),
    induction_complete boolean default false,
    image_file_path VARCHAR(255) NOT NULL,
    task_creation_email BOOLEAN NOT NULL DEFAULT FALSE,
    task_edit_email BOOLEAN NOT NULL DEFAULT FALSE,
    task_reminder_email BOOLEAN NOT NULL DEFAULT FALSE
    ) engine = InnoDB;

CREATE TABLE IF NOT EXISTS checklist_tasks
(
    id             bigint auto_increment primary key,
    title          varchar(60) not null,
    description    varchar(255),
    type           varchar(60),
    deadline       date,
    created_at     datetime not null default now(),
    started_at     datetime,
    completed_at   datetime,
    employee_id    bigint not null,
    FOREIGN KEY (employee_id) REFERENCES onboarding_employees(id) ON DELETE CASCADE
) engine = InnoDB;

DROP TABLE IF EXISTS tasks_library;
CREATE TABLE IF NOT EXISTS tasks_library
(
    id          bigint auto_increment primary key,
    title       varchar(60) not null,
    description varchar(255),
    type        varchar(60)
) engine = InnoDB;



