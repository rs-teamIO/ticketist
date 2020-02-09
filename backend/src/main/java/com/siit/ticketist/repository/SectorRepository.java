package com.siit.ticketist.repository;

import com.siit.ticketist.model.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SectorRepository extends JpaRepository<Sector, Long> {

    @Query(value = "SELECT * FROM sectors s WHERE s.venue_id = :id", nativeQuery = true)
    List<Sector> findSectorsByVenueId(Long id);

}
