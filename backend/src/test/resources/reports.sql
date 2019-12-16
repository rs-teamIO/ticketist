TRUNCATE TABLE venues;
TRUNCATE TABLE events;
TRUNCATE TABLE tickets;
TRUNCATE TABLE users;

INSERT INTO venues (id, is_active, name, street, city, latitude, longitude)
VALUES
	(1, true, 'Spens', 'Sutjeska 2', 'Novi Sad', 45.2470, 19.8453),							-- tickets and reservations
    (2, true, 'Novi Sad Fair', 'Hajduk Veljkova 11', 'Novi Sad', 45.267136, 19.833549),		-- only reservations => revenue = 0
    (3, true, 'Startit Centar', 'Miroslava Antica 2', 'Novi Sad', 45.267136, 19.833549),	-- no tickets and no reservations => revenue = 0
    (4, true, 'Venue without events', 'Mise Dimitrijevica 2', 'Novi Sad', 45.267136, 19.833549);	-- no events

INSERT INTO events
	(id, name, description, category, start_date, end_date, reservation_deadline, reservation_limit, venue_id)
VALUES
	(1, 'Event 1', 'Opis eventa koji se odrzava danas na Spensu', 'SPORTS', '2019-11-10', '2019-11-11', '2019-11-10', 3, 1),
	(2, 'Event 2', 'Opis eventa koji se odrzava danas na Spensu', 'SPORTS', CURDATE()-INTERVAL 12 MONTH-INTERVAL 1 DAY, CURDATE()-INTERVAL 12 MONTH, CURDATE()-INTERVAL 13 MONTH, 3, 1),		-- event out of the 12 month interval
    (3, 'Event 3', 'Koncert Adila', 'ENTERTAINMENT', '2019-11-12', '2019-11-12', '2019-11-01', 3, 2),
    (4, 'Event 4', 'Novosadski sajam knjiga', 'CULTURAL', '2020-03-05', '2020-03-11', '2020-03-01', 3, 3);

INSERT INTO tickets (id, number_column, number_row, price, status, event_id, user_id)
VALUES
    (1,1,1,40,1,1,1),		-- first spens event has one res and two tickets sold
    (2,1,1,40,1,1,1),
    (3,1,1,40,0,1,1),

    (4,1,1,50,1,2,1),		-- second spens event has one res and one ticket sold
    (5,1,1,50,0,2,1),

    (6,1,1,50,0,3,1),		-- third event has two reservations
    (7,1,1,60,0,3,1),

    (8,1,1,60,-1,3,1),      -- fourth event has no tickets or reservations
    (9,1,1,60,-1,3,1);



INSERT INTO users
    (dtype, id, email, first_name, last_name, password, username, is_verified, verification_code)
VALUES
    ('REGISTERED_USER', 1, 'kacjica+1@gmail.com', 'Katarina', 'Tukelic', 'kaca', 'kaca', 1, null);

INSERT INTO ticketist.user_role
VALUES
    (1, 'REGISTERED_USER');