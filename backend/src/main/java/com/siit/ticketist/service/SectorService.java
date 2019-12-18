package com.siit.ticketist.service;

import com.siit.ticketist.exceptions.NotFoundException;
import com.siit.ticketist.model.Sector;
import com.siit.ticketist.model.Venue;
import com.siit.ticketist.repository.SectorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Sector service layer.
 */
@Service
public class SectorService {

    private final SectorRepository sectorRepository;

    public SectorService(SectorRepository sectorRepository) {
        this.sectorRepository = sectorRepository;
    }

    /**
     * Finds a {@link Sector} with given ID.
     * If no such sector is found, the method throws a {@link NotFoundException}
     *
     * @param id ID of the sector
     * @return {@link Sector} instance
     * @throws NotFoundException Exception thrown in case no sector with given ID is found.
     */
    public Sector findOne(Long id) {
        return this.sectorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sector not found."));
    }

    /**
     * Returns all sectors of a {@link Venue}
     *
     * @param id ID of the venue
     * @return List of {@link Sector} objects containing a reference to the venue with given ID
     */
    public List<Sector> findSectorsByVenueId(Long id) {
        return this.sectorRepository.findSectorsByVenueId(id);
    }
}
