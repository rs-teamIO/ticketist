TRUNCATE TABLE venues;
TRUNCATE TABLE sectors;
TRUNCATE TABLE users;
TRUNCATE TABLE user_role;

INSERT INTO venues (id, is_active, name, street, city, latitude, longitude)
VALUES
	(1, true, 'Spens', 'Sutjeska 2', 'Novi Sad', 45.123, 25.123),
    (2, false, 'Arena', 'Puskinova 2', 'Beograd', 33.123, 19.123);

INSERT INTO sectors (id, columns_count, rows_count, max_capacity, name, start_column, start_row, venue_id)
VALUE
    (1, 2, 2, 4, 'Sever', 1, 1, 1),
    (2, 3, 2, 6, 'Zapad', 4, 4, 1),
    (3, 3, 3, 9, 'Sever', 1, 1, 2);

INSERT INTO users
    (dtype, id, email, first_name, last_name, password, username, is_verified, verification_code)
VALUES ('REGISTERED_USER', 1, 'kacjica+1@gmail.com', 'Katarina', 'Tukelic', '$2a$10$6HhA6auhomFftE468xtynuk40nllNZAbKLkkyyps/uX6QY1YqIbjO', 'user2020', 1, null),
       ('REGISTERED_USER', 3, 'rocky+1@gmail.com', 'Rocky', 'Balboa', 'rocky', 'rocky123', 1, null),
    ('REGISTERED_USER', 4, 'pip+1@gmail.com', 'Mark', 'Fly', 'pip', 'pip123', 1, null);

INSERT INTO users
    (dtype, id, email, first_name, last_name, password, username, is_verified, verification_code)
VALUES ('ADMIN', 2, 'f.ivkovic16+1@gmail.com', 'Filip', 'Ivkovic', '$2a$10$6HhA6auhomFftE468xtynuk40nllNZAbKLkkyyps/uX6QY1YqIbjO', 'filip', 1, null);

INSERT INTO user_role
VALUES
    (1, 'REGISTERED_USER'),
    (2, 'ADMIN');

