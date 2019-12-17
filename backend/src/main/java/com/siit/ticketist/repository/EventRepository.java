package com.siit.ticketist.repository;

import com.siit.ticketist.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    
    List<Event> findByVenueId(Long id);

    /**
     * Return all events, venue in which the event is happening, number of sold tickets for the event and total revenue.
     * Used for initial report
     * @return List of result objects
     */
    @Query( value =
            "SELECT e.name, venue_name, COUNT(t.id), COALESCE(SUM(t.price), 0) " +
            "FROM events as e LEFT OUTER JOIN (SELECT t.id, t.price, t.event_id FROM tickets as t where t.status = 2) as t " +
            "ON e.id = t.event_id " +
            "LEFT OUTER JOIN (SELECT v.name as venue_name, v.id from venues as v) as v " +
            "ON e.venue_id = v.id " +
            "GROUP BY e.id " +
            "ORDER BY SUM(t.price) desc", nativeQuery = true)
    List<Object[]> findAllEventsReport();

    /**
     * Returns all events for wanted venue, number of sold tickets for the event and total revenue.
     * Used for specific venue report.
     * @param venueId ID of the Venue
     * @return List of result objects
     */
    @Query( value =
            "SELECT e.name, v.name as venue_name, COUNT(t.id), COALESCE(SUM(t.price), 0) " +
            "FROM events as e LEFT OUTER JOIN venues as v " +
            "ON e.venue_id = v.id " +
            "LEFT OUTER JOIN (SELECT id, price, event_id FROM tickets WHERE status = 2) as t " +
            "ON e.id = t.event_id " +
            "WHERE v.id = :venueId " +
            "GROUP BY e.id", nativeQuery = true)
    List<Object[]> findAllEventReportsByVenue(@Param("venueId") Long venueId);

    /**
     * Returns month (in the last 12 months) and number of tickets sold in that month for wanted venue.
     * Used for specific venue report.
     * @param venueId ID of the Venue
     * @return List of result objects
     */
    @Query(value =
            "SELECT MONTH(e.start_date), COUNT(t.id) " +
            "FROM events e JOIN tickets t ON e.id = t.event_id " +
            "WHERE e.venue_id = :venueId " +
            "AND e.start_date BETWEEN DATE_SUB(CURDATE(), INTERVAL 12 MONTH) " +
            "AND CURDATE() AND t.status = 2 " +
            "GROUP BY MONTH(e.start_date)", nativeQuery = true)
    List<Object[]> getVenueTicketsReport(@Param("venueId") Long venueId);

    /**
     * TODO: getVenueRevenueReport
     * Returns month (in the last 12 months) and total revenue for that month for wanted venue.
     * Used for specific venue report.
     * @param venueId ID of the Venue
     * @return List of result objects
     */
    @Query(value =
            "SELECT MONTH(e.start_date), SUM(t.price) " +
            "FROM events e JOIN tickets t ON e.id = t.event_id " +
            "WHERE e.venue_id = :venueId " +
            "AND e.start_date BETWEEN DATE_SUB(CURDATE(), INTERVAL 12 MONTH) " +
            "AND CURDATE() AND t.status = 2 " +
            "GROUP BY MONTH(e.start_date)", nativeQuery = true)
    List<Object[]> getVenueRevenueReport(@Param("venueId") Long venueId);

    /**
     * TODO: Na enddate se dodaje jedan dan da bi bilo inkluzivno
     * Used to search events by specific parameters.
     * @param eventName Name of the event
     * @param category Category of the event
     * @param venueName Name of the Venue
     * @param startDate Start date of the event
     * @param endDate End date of the event
     * @return List of Event objects that satisfy search criteria
     */
    @Query(value =
            "SELECT * " +
            "FROM events e JOIN venues v ON e.venue_id = v.id " +
            "WHERE LOWER(e.name) LIKE concat('%', LOWER(:eventName), '%') " +
            "AND IFNULL(LOWER(e.category), '') LIKE concat('%', LOWER(:category), '%') " +
            "AND LOWER(v.name) LIKE concat('%', LOWER(:venueName), '%') " +
            "AND COALESCE( (e.start_date >= :startDate), true) AND COALESCE( (e.start_date <= ((:endDate)+INTERVAL 1 DAY)), true)",
            nativeQuery = true)
    List<Event> search(@Param("eventName") String eventName,
                       @Param("category") String category,
                       @Param("venueName") String venueName,
                       @Param("startDate") Date startDate,
                       @Param("endDate") Date endDate);
}
