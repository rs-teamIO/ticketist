package com.siit.ticketist.repository;

import com.siit.ticketist.model.Event;
import com.siit.ticketist.model.Sector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
