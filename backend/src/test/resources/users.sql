TRUNCATE TABLE venues;
TRUNCATE TABLE events;
TRUNCATE TABLE tickets;
TRUNCATE TABLE users;
TRUNCATE TABLE sectors;
TRUNCATE TABLE event_sectors;
TRUNCATE TABLE reservations;

INSERT INTO users
    (dtype, id, email, first_name, last_name, password, username, is_verified, verification_code)
VALUES
    ('REGISTERED_USER', 1, 'kacjica+1@gmail.com', 'Katarina', 'Tukelic', 'kaca', 'kaca', 1, null),
    ('REGISTERED_USER', 3, 'rocky+1@gmail.com', 'Rocky', 'Balboa', 'rocky', 'rocky123', 1, null),
    ('REGISTERED_USER', 4, 'pip+1@gmail.com', 'Mark', 'Fly', 'pip', 'pip123', 1, null);

INSERT INTO users
    (dtype, id, email, first_name, last_name, password, username)
VALUES
    ('ADMIN', 2, 'f.ivkovic16+1@gmail.com', 'Filip', 'Ivkovic', 'filip', 'filip');

INSERT INTO user_role
VALUES
    (1, 'REGISTERED_USER'),
    (2, 'ADMIN');


    
    

