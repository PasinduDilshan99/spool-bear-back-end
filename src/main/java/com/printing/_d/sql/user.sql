use 3d_printing;

-- 1. common_status
CREATE TABLE common_status (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  description VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  created_by INT,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by INT,
  terminated_at TIMESTAMP NULL,
  terminated_by INT,
  PRIMARY KEY (id)
);

-- 2. country
CREATE TABLE country (
  country_id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(255),
  status INT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  created_by INT,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by INT,
  terminated_at TIMESTAMP NULL,
  terminated_by INT,
  PRIMARY KEY (country_id),
  CONSTRAINT country_fk_status
    FOREIGN KEY (status) REFERENCES common_status(id)
);

-- 3. address
CREATE TABLE address (
  address_id INT NOT NULL AUTO_INCREMENT,
  number VARCHAR(50),
  address_line1 VARCHAR(255),
  address_line2 VARCHAR(255),
  city VARCHAR(100),
  district VARCHAR(100),
  province VARCHAR(100),
  country_id INT,
  postal_code VARCHAR(20),
  PRIMARY KEY (address_id),
  CONSTRAINT address_fk_country
    FOREIGN KEY (country_id) REFERENCES country(country_id)
);

-- 4. gender
CREATE TABLE gender (
  gender_id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  description VARCHAR(255),
  status INT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  created_by INT,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by INT,
  terminated_at TIMESTAMP NULL,
  terminated_by INT,
  PRIMARY KEY (gender_id),
  CONSTRAINT gender_fk_status
    FOREIGN KEY (status) REFERENCES common_status(id)
);

-- 5. user_type
CREATE TABLE user_type (
  user_type_id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(255),
  projects_access VARCHAR(255),
  status INT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  created_by INT,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by INT,
  terminated_at TIMESTAMP NULL,
  terminated_by INT,
  PRIMARY KEY (user_type_id),
  CONSTRAINT user_type_fk_status
    FOREIGN KEY (status) REFERENCES common_status(id)
);

-- 6. roles
CREATE TABLE roles (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (name)
);

-- 7. privileges
CREATE TABLE privileges (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (name)
);

-- 8. user
CREATE TABLE user (
  user_id INT NOT NULL AUTO_INCREMENT,
  username VARCHAR(100) NOT NULL,
  password VARCHAR(255) NOT NULL,
  first_name VARCHAR(100),
  middle_name VARCHAR(100),
  last_name VARCHAR(100),
  address_id INT,
  nic VARCHAR(20),
  gender_id INT,
  email VARCHAR(150),
  mobile_number VARCHAR(20),
  country_id INT,
  date_of_birth DATE,
  image_url VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  status INT,
  benefits_points_count INT DEFAULT 0,
  user_type_id INT,
  PRIMARY KEY (user_id),
  UNIQUE (username),
  UNIQUE (nic),
  UNIQUE (email),
  CONSTRAINT user_fk_address
    FOREIGN KEY (address_id) REFERENCES address(address_id),
  CONSTRAINT user_fk_gender
    FOREIGN KEY (gender_id) REFERENCES gender(gender_id),
  CONSTRAINT user_fk_country
    FOREIGN KEY (country_id) REFERENCES country(country_id),
  CONSTRAINT user_fk_status
    FOREIGN KEY (status) REFERENCES common_status(id),
  CONSTRAINT user_fk_user_type
    FOREIGN KEY (user_type_id) REFERENCES user_type(user_type_id)
);

-- 9. user_roles
CREATE TABLE user_roles (
  user_id INT NOT NULL,
  role_id INT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT user_roles_fk_user
    FOREIGN KEY (user_id) REFERENCES user(user_id),
  CONSTRAINT user_roles_fk_role
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- 10. role_privileges
CREATE TABLE role_privileges (
  role_id INT NOT NULL,
  privilege_id INT NOT NULL,
  PRIMARY KEY (role_id, privilege_id),
  CONSTRAINT role_privileges_fk_role
    FOREIGN KEY (role_id) REFERENCES roles(id),
  CONSTRAINT role_privileges_fk_privilege
    FOREIGN KEY (privilege_id) REFERENCES privileges(id)
);
