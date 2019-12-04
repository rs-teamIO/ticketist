package com.siit.ticketist.repository;

import com.siit.ticketist.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(value = "select * from events e where e.venue_id = ?1",nativeQuery = true)
    List<Event> findEventsByVenueId(Long id);

    /*
        Returns all events,
        venue in which that event is happening,
        number of sold tickets for the event and
        total revenue.
        Used for initial report.
     */
    @Query( value =
            "SELECT e.name, venue_name, COUNT(t.id), COALESCE(SUM(t.price), 0) " +
            "FROM events as e LEFT OUTER JOIN (SELECT t.id, t.price, t.event_id FROM tickets as t where t.status) as t " +
            "ON e.id = t.event_id " +
            "LEFT OUTER JOIN (SELECT v.name as venue_name, v.id from venues as v) as v " +
            "ON e.venue_id = v.id " +
            "GROUP BY e.id " +
            "ORDER BY SUM(t.price) desc", nativeQuery = true)
    List<Object[]> findAllEventsReport();

    /*  TODO
        Returns all events for wanted venue,
        number of sold tickets for the event and
        total revenue.
        Used for specific venue report.
    */
    @Query( "SELECT e.name, v.name, COUNT(t.id), COALESCE(SUM(t.price), 0) " +
            "FROM Event e JOIN e.venue as v JOIN e.tickets as t " +
            "WHERE v.id = ?1 AND t.status = 1 " +
            "GROUP BY e.id")
    List<Object[]> findAllEventReportsByVenue(Long venueId);


    /*  TODO
        Returns month (in last 12 months) and
        number of tickets sold in that month
        for wanted venue.
        Used for specific venue report.
     */
    @Query(value =
            "SELECT MONTH(e.start_date), COUNT(t.id) " +
            "FROM events e JOIN tickets t ON e.id = t.event_id " +
            "WHERE e.venue_id = :venueId " +
                    "AND e.start_date BETWEEN DATE_SUB(CURDATE(), INTERVAL 12 MONTH) " +
                    "AND CURDATE() AND t.status = 1 " +
            "GROUP BY MONTH(e.start_date)", nativeQuery = true)
    List<Object[]> getVenueTicketsReport(@Param("venueId") Long venueId);

    /*  TODO
        Returns month (in last 12 months) and
        total revenue for that month
        for wanted venue.
        Used for specific venue report.
     */
    @Query(value =
            "SELECT MONTH(e.start_date), SUM(t.price) " +
                    "FROM events e JOIN tickets t ON e.id = t.event_id " +
                    "WHERE e.venue_id = :venueId " +
                    "AND e.start_date BETWEEN DATE_SUB(CURDATE(), INTERVAL 12 MONTH) " +
                    "AND CURDATE() AND t.status = 1 " +
                    "GROUP BY MONTH(e.start_date)", nativeQuery = true)
    List<Object[]> getVenueRevenueReport(@Param("venueId") Long venueId);

    //TODO
    @Query(value =
            "SELECT * " +
            "FROM events e JOIN venues v ON e.venue_id = v.id " +
            "WHERE LOWER(e.name) LIKE concat('%', LOWER(:eventName), '%') " +
            "AND IFNULL(LOWER(e.category), '') LIKE concat('%', LOWER(:category), '%') " +
            "AND LOWER(v.name) LIKE concat('%', LOWER(:venueName), '%') " +
            "AND COALESCE( (e.start_date >= :startDate), true) AND COALESCE( (e.start_date <= :endDate), true)",
            nativeQuery = true)
    List<Event> search(@Param("eventName") String eventName,
                       @Param("category") String category,
                       @Param("venueName") String venueName,
                       @Param("startDate") Date startDate,
                       @Param("endDate") Date endDate);


}
