package com.siit.ticketist.repository;

import com.siit.ticketist.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    //TODO
    @Query( "SELECT e.name, v.name, COUNT(t), COALESCE(SUM(t.price), 0) " +
            "FROM Event e JOIN e.venue v JOIN e.tickets t " +
            "WHERE v.id = :venueId")
    List<Object[]> findAllEventReportsByVenue(Long venueId);

    @Query( value =
            "SELECT e.name, venue_name, COUNT(t.id) as broj_prodatih_karata, COALESCE(SUM(t.price), 0) as ukupna_zarada " +
            "FROM ticketist.events as e LEFT OUTER JOIN (SELECT t.id, t.price, t.event_id FROM ticketist.tickets as t where t.is_paid) as t " +
            "ON e.id = t.event_id " +
            "LEFT OUTER JOIN (SELECT v.name as venue_name, v.id from ticketist.venues as v) as v " +
            "ON e.venue_id = v.id " +
            "GROUP BY e.id", nativeQuery = true)
    List<Object[]> findAllEventsReport();


}
