TRUNCATE TABLE ticketist.venues;
TRUNCATE TABLE ticketist.events;
TRUNCATE TABLE ticketist.tickets;

INSERT INTO ticketist.venues (id, is_active, name, street, city, latitude, longitude)
VALUES
	(1, true, "Spens", "ulica", "grad", 1, 1),
    (2, true, "Novi Sad Fair", "ulica", "grad", 1, 1),
    (3, true, "Startit Centar", "ulica", "grad", 1, 1);

INSERT INTO ticketist.events
	(id, name, description, start_date, end_date, reservation_deadline, reservation_limit, venue_id)
VALUES
	(4, "Event1", "Opis eventa 1", CURDATE(), CURDATE(), '2019-10-05', 3, 1),
    (5, "Event2", "Opis eventa 2", '2019-12-12', '2019-12-20', '2019-10-05', 3, 1),
    (6, "Event3", "Opis eventa 3", CURDATE(), CURDATE(), '2019-10-05', 3, 2),
    (7, "Event4", "Opis eventa 4", '2019-12-12', '2019-12-20', '2019-10-05', 3, 2),

    (8, "Spens event 1", "Opis spens eventa 1", "2019-11-11", '2019-11-11', "2019-11-10", 3, 1),
    (9, "Spens event 2", "Opis spens eventa 2", "2019-9-10", "2019-9-11", "2019-10-5", 3, 1),
    (10, "Spens event 3", "Opis spens eventa 3", "2019-8-10", "2019-8-11", "2019-10-5", 3, 1),
    (11, "Spens event 4", "Opis spens eventa 4", "2019-7-10", "2019-7-11", "2019-10-5", 3, 1),
    (12, "Spens event 5", "Opis spens eventa 5", "2019-6-10", "2019-6-11", "2019-10-5", 3, 1),		-- ova tri su istog meseca
    (13, "Spens event 6", "Opis spens eventa 6", "2019-6-10", "2019-6-11", "2019-10-5", 3, 1),		--
    (14, "Spens event 7", "Opis spens eventa 7", "2019-6-10", "2019-6-11", "2019-10-5", 3, 1);		--

INSERT INTO users
    (dtype, id, email, first_name, last_name, password, username, is_verified)
VALUES
    ('REGISTERED_USER', 23, 'kacjica@gmail.com', 'Katarina', 'Tukelic', 'kaca', 'kaca', 1);

INSERT INTO ticketist.tickets
	(id, is_paid, number_row, number_column, price, event_id, user_id)
VALUES
	(15, true, 2, 2, 20, 4, 23),
    (16, true, 2, 3, 20, 4, 23),
    (17, true, 2, 4, 20, 4, 23),
    (18, false, 5, 5, 50, 5, 23),	-- ako je is_paid false, radi se o rezervaciji

    (19, false, 2, 2, 20, 8, 23),
    (20, true, 2, 3, 20, 9, 23),
    (21, true, 2, 4, 20, 12, 23),
    (22, true, 2, 5, 20, 13, 23);


    
    

