-- =============================================================
-- DRRMS Seed Data
-- Disaster Relief Resource Management System
-- =============================================================
-- Run this AFTER schema.sql to populate the database with
-- sample data for development and testing.
--
-- NOTE: All passwords are BCrypt-hashed for Spring Security.
--   admin@example.com    → adminpass
--   raj@example.com      → volpass
--   deepa@example.com    → deeppass
--   kumar@example.com    → kumarpass
--   vijay@example.com    → vijaypass
-- =============================================================

-- Locations
INSERT INTO locations (name, region, latitude, longitude, weather_condition, weather_alert)
VALUES
    ('Chennai',    'Tamil Nadu', 13.0827, 80.2707, 'Heavy Rain',     'orange'),
    ('Cuddalore',  'Tamil Nadu', 11.7463, 79.7714, 'Cyclone Alert',  'red'),
    ('Madurai',    'Tamil Nadu',  9.9252, 78.1198, 'Cloudy',         'yellow'),
    ('Coimbatore', 'Tamil Nadu', 11.0168, 76.9558, 'Clear',          'none');

-- Shelters
INSERT INTO shelters (name, location_id, capacity, current_occupancy, contact_number)
VALUES
    ('Relief Camp A',              1, 200, 150, '9876543210'),
    ('School Shelter Cuddalore',   2, 300, 275, '9876501234'),
    ('Community Hall Madurai',     3, 150,  90, '9876567890');

-- Resources
INSERT INTO resources (name, type, quantity, unit, location_id)
VALUES
    ('Rice Bags',              'food',     500,  'kg',     1),
    ('Drinking Water Bottles', 'water',   1000,  'liters', 1),
    ('First Aid Kits',         'medical',   50,  'boxes',  2),
    ('Blankets',               'clothing', 300,  'units',  3),
    ('Sanitary Pads',          'other',    400,  'packs',  1);

-- Users (BCrypt-hashed passwords, cost factor 12)
INSERT INTO users (username, password, role, email, contact_number, is_requesting_help)
VALUES
    ('admin1',         '$2a$12$xvmxGV..Fobb7VNi28IIIuUCOoRNi2Y5/4yNXpHjP.SplxJHGDvZG', 'admin',     'admin@example.com',  '9000000001', false),
    ('volunteer_raj',  '$2a$12$ef7Wuke/lQP9m7oYEWjW7uG6D.6aJ8ASO311qG5hQ6cM6djOb7x6S', 'volunteer', 'raj@example.com',    '9000000003', false),
    ('deepa_citizen',  '$2a$12$gxS5r4PpV/VbYWTCtqEyrOE9XtL7t.87.0RzvcFm4FRpUBvaRQl0O', 'citizen',   'deepa@example.com',  '9000000004', true),
    ('kumar_citizen',  '$2a$12$0QBbgseSV90am4cIxyYo..RdcmEUaOW3M7VrmS5do.yLRX4p0lQWu', 'citizen',   'kumar@example.com',  '9000000005', false),
    ('vijay',          '$2a$12$iVoUmWjN1hMP6z1tVz3MDunkyOjsw432kh0W4zBGfElJvFPRSJWey', 'volunteer', 'vijay@example.com',  '7871648764', false);

-- Requests
INSERT INTO requests (user_id, resource_id, location_id, quantity_requested, status, remarks)
VALUES
    (3, 1, 1,  50, 'pending', 'Need rice for 5 families'),
    (4, 2, 1, 100, 'pending', 'Water shortage in area'),
    (3, 3, 2,  20, 'pending', 'Medical help for cyclone relief');

-- Audit Log
INSERT INTO audit_log (action, performed_by)
VALUES
    ('Approved request ID 2', 1),
    ('Updated resource quantities in Chennai', 1),
    ('Fulfilled request ID 3', 1),
    ('Manually inserted log 1 - resource updated', 1),
    ('Manually inserted log 2 - request assigned', 2),
    ('Manually inserted log 3 - shelter capacity updated', 1),
    ('Manually inserted log 4 - weather alert changed', 2),
    ('Manually inserted log 5 - citizen request approved', 1);
