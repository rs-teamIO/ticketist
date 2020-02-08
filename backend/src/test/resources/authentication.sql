TRUNCATE TABLE venues;
TRUNCATE TABLE events;
TRUNCATE TABLE tickets;
TRUNCATE TABLE users;
TRUNCATE TABLE sectors;
TRUNCATE TABLE event_sectors;
TRUNCATE TABLE reservations;
TRUNCATE TABLE media_files;
TRUNCATE TABLE user_role;

INSERT INTO users
    (dtype, id, email, first_name, last_name, password, username, is_verified, verification_code)
VALUES
    ('REGISTERED_USER', 1, 'verified_user@ticketist.com', 'Verified', 'User', '$2a$10$.r0LQFe.zjeijYLxAYQ5aul3auRCWxDbOBJSjitOogGBMpVqhQn3a', 'verified_user', 1, null),
    ('REGISTERED_USER', 2, 'unverified_user@ticketist.com', 'Unverified', 'User', '$2a$10$qS8my7rtSELTzzT9zrIrFeiMGjYznP2R9jEE8cW7TbRFH.xAr5lDa', 'unverified_user', 0, '15f1b452-a23c-4f2b-9b23-2746f73d5b2b');

INSERT INTO users
    (dtype, id, email, first_name, last_name, password, username)
VALUES
        ('ADMIN', 3, 'admin@ticketist.com', 'Ticketist', 'Admin', '$2a$10$MsMRF/aDcLNneJhglHDevOXIUw//2GMqt.sLC77YAaFCr78GAMsyC', 'admin');

INSERT INTO user_role
VALUES
    (1, 'REGISTERED_USER'),
    (3, 'ADMIN');