-- 1. common_status
INSERT INTO common_status (id, name, description) VALUES
(1, 'ACTIVE', 'Active record'),
(2, 'INACTIVE', 'Inactive record'),
(3, 'TERMINATED', 'Terminate record');

-- 2. country
INSERT INTO country (country_id, name, description, status) VALUES
(1, 'Sri Lanka', 'Sri Lanka country', 1),
(2, 'India', 'India country', 1);

-- 3. address
INSERT INTO address (address_id, number, address_line1, city, district, province, country_id, postal_code) VALUES
(1, '123', 'Galle Road', 'Colombo', 'Colombo', 'Western', 1, '00300'),
(2, '45B', 'Main Street', 'Kandy', 'Kandy', 'Central', 1, '20000');

-- 4. gender
INSERT INTO gender (gender_id, name, description, status) VALUES
(1, 'Male', 'Male gender', 1),
(2, 'Female', 'Female gender', 1);

-- 5. user_type
INSERT INTO user_type (user_type_id, name, description, projects_access, status) VALUES
(1, 'ADMIN', 'System administrator', 'ALL', 1),
(2, 'CUSTOMER', 'Application user', 'LIMITED', 1);

-- 6. roles
INSERT INTO roles (id, name) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_USER');

-- 7. privileges
INSERT INTO privileges (id, name) VALUES
(1, 'USER_CREATE'),
(2, 'USER_UPDATE'),
(3, 'USER_DELETE'),
(4, 'USER_VIEW'),
(5, 'ROLE_MANAGE');

INSERT INTO role_privileges (role_id, privilege_id) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5), -- ADMIN has all privileges
(2, 4); -- USER has view only
