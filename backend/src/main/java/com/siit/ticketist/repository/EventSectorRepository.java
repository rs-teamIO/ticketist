package com.siit.ticketist.repository;

import com.siit.ticketist.model.EventSector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventSectorRepository extends JpaRepository<EventSector, Long> {
}
