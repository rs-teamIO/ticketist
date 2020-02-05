TRUNCATE TABLE venues;
TRUNCATE TABLE events;
TRUNCATE TABLE tickets;
TRUNCATE TABLE users;
TRUNCATE TABLE sectors;
TRUNCATE TABLE event_sectors;
TRUNCATE TABLE reservations;

INSERT INTO venues (id, is_active, name, street, city, latitude, longitude)
VALUES
	(1, true, 'Spens', 'Sutjeska 2', 'Novi Sad', 45.2470, 19.8453),
    (2, true, 'Novi Sad Fair', 'Hajduk Veljkova 11', 'Novi Sad', 45.267136, 19.833549),
    (3, false, 'Startit Centar', 'Miroslava Antica 2', 'Novi Sad', 45.267136, 19.833549);

INSERT INTO sectors (id, columns_count, rows_count, max_capacity, name, start_column, start_row, venue_id)
VALUE
    (1, 2, 2, 4, 'Sever', 1, 1, 1),
    (2, 3, 2, 6, 'Zapad', 10, 10, 1),
    (3, 3, 3, 9, 'Istok', 20, 20, 1),
    (4, 4, 4, 16, 'Jug', 30, 30, 1),
    (5, 2, 2, 4, 'Sever', 10, 10, 2),
    (6, 2, 2, 4, 'Zapad', 20, 20, 3);

INSERT INTO events
	(id, name, description, category, start_date, end_date, reservation_deadline, reservation_limit, venue_id)
VALUES
    (1, 'EXIT Festival', 'Cool music!', 'ENTERTAINMENT', '2020-06-14', '2020-06-15', '2020-06-5', 5, 1),
    (2, 'NBA', 'NBA Playoff 1/4!', 'SPORTS', '2019-06-14', '2019-06-14', '2019-06-5', 3, 3);

INSERT INTO event_sectors (id, capacity, date, numerated_seats, ticket_price, event_id, sector_id)
VALUES
    (1, 4, '2020-06-14', 1, 35.0, 1, 1),
    (2, 4, '2020-06-15', 0, 25.0, 1, 1),
    (3, 4, '2020-06-14', 1, 45.0, 2, 6);

INSERT INTO reservations (id, event_id, user_id)
VALUES
    (1, 1, 1),
    (2, 1, 1),
    (3, 1, 2);

INSERT INTO tickets (id, number_column, number_row, price, status, event_id, event_sector_id, user_id, version, reservation_id)
VALUES
    (1,1,1,35.0,1,1,1,1,0,1),
    (2,1,2,35.0,1,1,1,1,0,1),
    (3,2,1,35.0,0,1,1,null,0,null),
    (4,2,2,35.0,0,1,1,null,0,null),

    (5,-1,-1,25.0,1,1,2,1,0,2),
    (6,-1,-1,25.0,1,1,2,1,0,2),
    (7,-1,-1,25.0,2,1,2,1,0,null),
    (8,-1,-1,25.0,0,1,2,null,0,null),

    (9,1,1,45.0,2,2,3,1,0,null),
    (10,1,2,45.0,2,2,3,1,0,null),
    (11,2,1,45.0,0,2,3,null,0,null),
    (12,2,2,45.0,0,2,3,null,0,null);

INSERT INTO users
    (dtype, id, email, first_name, last_name, password, username, is_verified, verification_code)
VALUES
    ('REGISTERED_USER', 1, 'kacjica+1@gmail.com', 'Katarina', 'Tukelic', '$2a$10$6HhA6auhomFftE468xtynuk40nllNZAbKLkkyyps/uX6QY1YqIbjO', 'kaca', 1, null),
    ('ADMIN', 2, 'f.ivkovic16+1@gmail.com', 'Filip', 'Ivkovic', '$2a$10$6HhA6auhomFftE468xtynuk40nllNZAbKLkkyyps/uX6QY1YqIbjO', 'filip', 1, null),
    ('REGISTERED_USER', 3, 'rocky+1@gmail.com', 'Rocky', 'Balboa', 'rocky', 'rocky123', 1, null);

INSERT INTO user_role
VALUES
    (1, 'REGISTERED_USER'),
    (2, 'ADMIN');





