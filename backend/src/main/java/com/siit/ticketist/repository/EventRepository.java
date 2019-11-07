package com.siit.ticketist.repository;

import com.siit.ticketist.model.Event;
import com.siit.ticketist.model.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(value = "select * from events e where e.venue_id = ?1",nativeQuery = true)
    List<Event> findEventsByVenueId(Long id);
}
