package com.siit.ticketist.service;

import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.exceptions.NotFoundException;
import com.siit.ticketist.model.Sector;
import com.siit.ticketist.model.Venue;
import com.siit.ticketist.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Venue service layer.
 */
@Service
public class VenueService {

    private final VenueRepository venueRepository;

    @Autowired
    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    /**
     * Returns a list of all available venues
     *
     * @return {@link List} of all {@link Venue} objects
     */
    public List<Venue> findAll() {
        return this.venueRepository.findAll();
    }

    /**
     * Finds a {@link Venue} with given ID.
     * If no such venue is found, the method throws a {@link NotFoundException}
     *
     * @param id ID of the venue
     * @return {@link Venue} instance
     * @throws NotFoundException Exception thrown in case no venue with given ID is found.
     */
    public Venue findOne(Long id) {
        return this.venueRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venue not found."));
    }

    public Venue save(Venue venue) {
        int overlap = 0;
        for(Sector sector: venue.getSectors()){
            for(Sector sectorSave: venue.getSectors()){
                    if(!sectorCanBeDrawn(sector,sectorSave)){
                        overlap++;
                        if(overlap>venue.getSectors().size())
                            throw new BadRequestException("Cant add overlaping sectors");
                    }
            }
        }

        return venueRepository.save(venue);
    }

    private boolean sectorCanBeDrawn(Sector sector, Sector sectorSave) {
        if(sector.getStartRow() > sectorSave.getStartRow() + sectorSave.getRowsCount()) return true;
        if(sector.getStartRow() + sector.getRowsCount() < sectorSave.getStartRow()) return true;
        if(sector.getStartColumn() > sectorSave.getStartColumn() + sectorSave.getColumnsCount()) return true;
        if(sector.getStartColumn() + sector.getColumnsCount() < sectorSave.getStartColumn()) return true;
        return false;
    }
}
