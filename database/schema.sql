-- =============================================================
-- DRRMS Database Schema
-- Disaster Relief Resource Management System
-- =============================================================
-- Run this script against an existing MySQL database (drrms_db)
-- to create all tables and triggers.
-- =============================================================

-- -----------------------------------------------------------
-- 1. LOCATIONS
-- -----------------------------------------------------------
USE drrms_db;

CREATE TABLE IF NOT EXISTS locations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    region VARCHAR(100),
    latitude DECIMAL(10, 6),
    longitude DECIMAL(10, 6),
    weather_condition VARCHAR(50),
    weather_alert ENUM('none', 'yellow', 'orange', 'red') DEFAULT 'none',
    last_weather_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------------
-- 2. SHELTERS
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS shelters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location_id BIGINT,
    capacity INT,
    current_occupancy INT DEFAULT 0,
    contact_number VARCHAR(15),
    FOREIGN KEY (location_id) REFERENCES locations(id)
);

-- -----------------------------------------------------------
-- 3. RESOURCES
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS resources (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(100) NOT NULL,
    quantity INT,
    unit VARCHAR(20),
    location_id BIGINT,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (location_id) REFERENCES locations(id)
);

-- -----------------------------------------------------------
-- 4. USERS
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'volunteer', 'citizen') NOT NULL,
    email VARCHAR(100),
    contact_number VARCHAR(15),
    is_requesting_help BOOLEAN DEFAULT FALSE
);

-- -----------------------------------------------------------
-- 5. REQUESTS
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    resource_id BIGINT,
    location_id BIGINT,
    quantity_requested INT,
    status ENUM('pending', 'approved', 'assigned', 'completed', 'fulfilled') DEFAULT 'pending',
    request_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarks TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (resource_id) REFERENCES resources(id),
    FOREIGN KEY (location_id) REFERENCES locations(id)
);

-- -----------------------------------------------------------
-- 6. VOLUNTEER REQUESTS
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS volunteer_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    volunteer_id BIGINT,
    request_id BIGINT NOT NULL,
    status ENUM('assigned', 'in_progress', 'completed', 'verified') DEFAULT 'assigned',
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    FOREIGN KEY (volunteer_id) REFERENCES users(id),
    FOREIGN KEY (request_id) REFERENCES requests(id)
);

-- -----------------------------------------------------------
-- 7. AUDIT LOG
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    action TEXT NOT NULL,
    performed_by BIGINT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (performed_by) REFERENCES users(id)
);

-- -----------------------------------------------------------
-- 8. RESOURCE AUDIT LOG
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS resource_audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    resource_id BIGINT,
    old_name VARCHAR(100),
    old_quantity INT,
    new_name VARCHAR(100),
    new_quantity INT,
    action_type VARCHAR(10),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------------
-- TRIGGERS
-- -----------------------------------------------------------

-- Auto-log resource updates
DELIMITER $$
CREATE TRIGGER log_resource_update
BEFORE UPDATE ON resources
FOR EACH ROW
BEGIN
    INSERT INTO resource_audit_log (
        resource_id, old_name, old_quantity,
        new_name, new_quantity, action_type
    )
    VALUES (
        OLD.id, OLD.name, OLD.quantity,
        NEW.name, NEW.quantity, 'UPDATE'
    );
END$$
DELIMITER ;

-- Auto-create volunteer_request when request status changes to approved
DELIMITER $$
CREATE TRIGGER after_request_assigned
AFTER UPDATE ON requests
FOR EACH ROW
BEGIN
    IF NEW.status = 'approved' AND OLD.status = 'pending' THEN
        INSERT INTO volunteer_requests (volunteer_id, request_id, status)
        VALUES (
            (SELECT performed_by FROM audit_log
             WHERE action LIKE CONCAT('%', NEW.id, '%')
             ORDER BY timestamp DESC LIMIT 1),
            NEW.id, 'assigned'
        );
    END IF;
END$$
DELIMITER ;

-- Auto-verify volunteer_request when admin fulfills
DELIMITER $$
CREATE TRIGGER after_task_verified
AFTER UPDATE ON requests
FOR EACH ROW
BEGIN
    IF NEW.status = 'fulfilled' AND OLD.status = 'completed' THEN
        UPDATE volunteer_requests
        SET status = 'verified', completed_at = CURRENT_TIMESTAMP
        WHERE request_id = NEW.id;
    END IF;
END$$
DELIMITER ;

-- Log status revert from fulfilled to completed
DELIMITER $$
CREATE TRIGGER log_fulfilled_to_completed
AFTER UPDATE ON requests
FOR EACH ROW
BEGIN
    IF OLD.status = 'fulfilled' AND NEW.status = 'completed' THEN
        INSERT INTO audit_log (action, performed_by)
        VALUES (
            CONCAT('Status reverted from fulfilled to completed for request ID ', NEW.id),
            NULL
        );
    END IF;
END$$
DELIMITER ;
