TRUNCATE TABLE venues;
TRUNCATE TABLE events;
TRUNCATE TABLE tickets;

INSERT INTO venues (id, is_active, name)
VALUES 
	(1, true, "Spens"),
    (2, true, "Novi Sad Fair"),
    (3, true, "Startit Centar");
    
INSERT INTO events
	(id, name, description, start_date, end_date, reservation_deadline, reservation_limit, venue_id)
VALUES
	(4, "Event1", "Opis eventa 1", CURDATE(), CURDATE(), '2019-10-05', 3, 1),
    (5, "Event2", "Opis eventa 2", '2019-12-12', '2019-12-20', '2019-10-05', 3, 1),
    (6, "Event3", "Opis eventa 3", CURDATE(), CURDATE(), '2019-10-05', 3, 2),
    (7, "Event4", "Opis eventa 4", '2019-12-12', '2019-12-20', '2019-10-05', 3, 2);
    
INSERT INTO tickets
	(id, is_paid, number_row, number_column, price, event_id)
VALUES
	(8, true, 2, 2, 20, 4),
    (9, true, 2, 3, 20, 4),
    (10, true, 2, 4, 20, 4),
    (11, false, 5, 5, 50, 5);	-- ako je is_paid false, radi se o rezervaciji
    
    

