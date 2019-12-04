package com.siit.ticketist.repository;

import com.siit.ticketist.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VenueRepository extends JpaRepository<Venue, Long> {

    @Query( value =
            "SELECT v.name, COALESCE(SUM(t.price), 0) " +
            "FROM venues as v LEFT OUTER JOIN events as e " +
            "ON v.id = e.venue_id " +
            "LEFT OUTER JOIN (SELECT * from tickets as t WHERE t.status = 1) as t " +
            "ON e.id = t.event_id " +
            "GROUP BY v.id", nativeQuery = true)
    List<Object[]> getAllVenueRevenues();

}
