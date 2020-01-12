TRUNCATE TABLE venues;
TRUNCATE TABLE events;
TRUNCATE TABLE tickets;
TRUNCATE TABLE users;
TRUNCATE TABLE sectors;
TRUNCATE TABLE event_sectors;

INSERT INTO venues (id, is_active, name, street, city, latitude, longitude)
VALUES
	(1, true, 'Spens', 'Sutjeska 2', 'Novi Sad', 45.2470, 19.8453),
    (2, true, 'Novi Sad Fair', 'Hajduk Veljkova 11', 'Novi Sad', 45.267136, 19.833549),
    (3, true, 'Startit Centar', 'Miroslava Antica 2', 'Novi Sad', 45.267136, 19.833549);

INSERT INTO sectors (id, columns_count, rows_count, max_capacity, name, start_column, start_row, venue_id)
VALUE
    (1, 2, 2, 4, 'Sever', 10, 10, 1),
    (2, 3, 2, 6, 'Zapad', 20, 20, 1);

INSERT INTO events
	(id, name, description, category, start_date, end_date, reservation_deadline, reservation_limit, venue_id)
VALUES
	(1, 'Danasnji event', 'Opis eventa koji se odrzava danas na Spensu', 'SPORTS', CURDATE(), CURDATE(), CURDATE(), 3, 1),
    (2, 'Koncert Adila', 'Koncert Adila', 'ENTERTAINMENT', '2019-11-12', '2019-11-12', '2019-11-01', 3, 1),          -- potrebno dodati sate odrzavanja
    (3, 'Sajam knjiga', 'Novosadski sajam knjiga', 'CULTURAL', '2020-03-05', '2020-03-11', '2020-03-01', 3, 2),
    (4, 'Poljoprivredni sajam', 'Medjunarodni sajam poljoprivrede', 'CULTURAL', '2020-05-11', '2020-05-17', '2020-05-01', 3, 2),

    (5, 'Spens event 1', 'Opis spens eventa 1', 'SPORTS', '2019-11-12', '2019-11-12', '2019-11-11', 3, 1),
    (6, 'Spens event 2', 'Opis spens eventa 2', 'SPORTS', '2019-09-10', '2019-09-11', '2019-09-5', 3, 1),
    (7, 'Spens event 3', 'Opis spens eventa 3', 'SPORTS', '2019-08-10', '2019-08-11', '2019-08-5', 3, 1),
    (8, 'Spens event 4', 'Opis spens eventa 4', 'CULTURAL', '2019-07-10', '2019-07-11', '2019-07-5', 3, 1),
    (9, 'Spens event 5', 'Opis spens eventa 5', 'CULTURAL', '2019-06-10', '2019-06-11', '2019-06-5', 3, 1),
    (10, 'Spens event 6', 'Opis spens eventa 6', 'ENTERTAINMENT', '2019-06-12', '2019-06-13', '2019-06-5', 3, 1),
    (11, 'Spens event 7', 'Opis spens eventa 7', 'ENTERTAINMENT', '2019-06-14', '2019-06-15', '2019-06-5', 3, 1),
    (12, 'Spens event 8', 'Opis spens eventa 8', 'ENTERTAINMENT', '2020-06-14', '2020-06-15', '2020-06-5', 3, 1);

INSERT INTO event_sectors (id, capacity, date, numerated_seats, ticket_price, event_id, sector_id)
VALUES
    (1, 4, '2020-06-14', 1, 35.00, 12, 1),
    (2, 4, '2020-06-15', 0, 35.00, 12, 2);

INSERT INTO tickets (id, number_column, number_row, price, status, event_id, event_sector_id, user_id)
VALUES
    (1,1,1,35.00,2,1,1,1),
    (2,1,2,35.00,1,1,1,1),
    (3,2,1,35.00,0,1,1,null),
    (4,2,2,35.00,0,1,1,null),
    (5,-1,-1,35.00,1,1,2,1),
    (6,-1,-1,35.00,2,1,2,1),
    (7,-1,-1,35.00,0,1,2,null),
    (8,-1,-1,35.00,0,1,2,null);


INSERT INTO users
    (dtype, id, email, first_name, last_name, password, username, is_verified, verification_code)
VALUES
    ('REGISTERED_USER', 1, 'kacjica+1@gmail.com', 'Katarina', 'Tukelic', 'kaca', 'kaca', 1, null);

INSERT INTO users
    (dtype, id, email, first_name, last_name, password, username)
VALUES
    ('ADMIN', 2, 'f.ivkovic16+1@gmail.com', 'Filip', 'Ivkovic', 'filip', 'filip');

INSERT INTO user_role
VALUES
    (1, 'REGISTERED_USER'),
    (2, 'ADMIN');


    
    

