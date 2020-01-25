TRUNCATE TABLE venues;
TRUNCATE TABLE sectors;

INSERT INTO venues (id, is_active, name, street, city, latitude, longitude)
VALUES
	(1, true, 'Spens', 'Sutjeska 2', 'Novi Sad', 45.123, 25.123),
    (2, false, 'Arena', 'Puskinova 2', 'Beograd', 33.123, 19.123);

INSERT INTO sectors (id, columns_count, rows_count, max_capacity, name, start_column, start_row, venue_id)
VALUE
    (1, 2, 2, 4, 'Sever', 1, 1, 1),
    (2, 3, 2, 6, 'Zapad', 4, 4, 1),
    (3, 3, 3, 9, 'Sever', 1, 1, 2);
