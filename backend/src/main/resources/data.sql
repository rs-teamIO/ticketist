TRUNCATE TABLE venues;
TRUNCATE TABLE events;
TRUNCATE TABLE tickets;
TRUNCATE TABLE users;

INSERT INTO venues (id, is_active, name, street, city, latitude, longitude)
VALUES
	(1, true, 'Spens', 'Sutjeska 2', 'Novi Sad', 45.2470, 19.8453),
    (2, true, 'Novi Sad Fair', 'Hajduk Veljkova 11', 'Novi Sad', 45.267136, 19.833549),
    (3, true, 'Startit Centar', 'Miroslava Antića 2', 'Novi Sad', 45.267136, 19.833549);

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
    (11, 'Spens event 7', 'Opis spens eventa 7', 'ENTERTAINMENT', '2019-06-14', '2019-06-15', '2019-06-5', 3, 1);

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

INSERT INTO tickets
	(id, is_paid, number_row, number_column, price, event_id, user_id)
VALUES
	(1, true, 2, 2, 20, 1, 1),
    (2, true, 2, 3, 20, 1, 1),
    (3, true, 2, 4, 20, 1, 1),
    (4, false, 5, 5, 50, 2, 1),	  -- rezervacija

    (5, false, 2, 2, 20, 5, 1),   -- rezervacija
    (6, true, 2, 3, 20, 6, 1),
    (7, true, 2, 4, 20, 9, 1),
    (8, true, 2, 5, 20, 10, 1);


    
    

