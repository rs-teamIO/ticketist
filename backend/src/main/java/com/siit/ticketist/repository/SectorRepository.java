package com.siit.ticketist.repository;

import com.siit.ticketist.model.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SectorRepository extends JpaRepository<Sector, Long> {

    @Query(value = "select * from sectors s where s.venue_id = ?1", nativeQuery = true)
    List<Sector> findSectorsByVenueId(Long id);

}
