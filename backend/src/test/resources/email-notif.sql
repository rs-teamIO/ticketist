TRUNCATE TABLE venues;
TRUNCATE TABLE events;
TRUNCATE TABLE tickets;
TRUNCATE TABLE users;

INSERT INTO venues (id, is_active, name, street, city, latitude, longitude)
VALUES
	(1, true, 'Spens', 'Sutjeska 2', 'Novi Sad', 45.2470, 19.8453);

INSERT INTO events
	(id, name, description, category, start_date, end_date, reservation_deadline, reservation_limit, venue_id)
VALUES
	(1, 'Danasnji event', 'Opis eventa koji se odrzava danas na Spensu', 'SPORTS', CURDATE(), CURDATE(), CURDATE(), 3, 1),
    (2, 'Koncert Adila', 'Koncert Adila', 'ENTERTAINMENT', ADDDATE(CURDATE(), 3), ADDDATE(CURDATE(), 3), ADDDATE(CURDATE(), 3), 3, 1),
    (3, 'Colin koncert', 'Colin koncert', 'ENTERTAINMENT', ADDDATE(CURDATE(), 4), ADDDATE(CURDATE(), 4), ADDDATE(CURDATE(), 4), 3, 1);

INSERT INTO tickets (id, number_column, number_row, price, status, event_id, user_id)
VALUES
    (1,1,1,100.00,1,1,1),		-- reservation
    (2,1,1,100.00,2,1,2),		-- ticket

    (3,1,1,100.00,2,2,2),		-- ticket
    (4,1,1,100.00,2,2,2);		-- ticket

INSERT INTO users
    (dtype, id, email, first_name, last_name, password, username, is_verified, verification_code)
VALUES
    ('REGISTERED_USER', 1, 'user1@gmail.com', 'Petar', 'Petrovic', 'pera', 'pera', 1, null),
    ('REGISTERED_USER', 2, 'user2@gmail.com', 'Ivan', 'Ivanovic', 'ivan', 'ivan', 1, null);

INSERT INTO user_role
VALUES
    (1, 'REGISTERED_USER'),
    (2, 'REGISTERED_USER');