package com.siit.ticketist.service;

import com.siit.ticketist.dto.VenueBasicDTO;
import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.exceptions.NotFoundException;
import com.siit.ticketist.model.Sector;
import com.siit.ticketist.model.Venue;
import com.siit.ticketist.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        venue.setIsActive(true);
        for (Sector firstSector: venue.getSectors()) {
            overlap = 0;
            for (Sector secondSector: venue.getSectors()) {
                if (!sectorCanBeDrawn(firstSector, secondSector)) {
                    overlap++;
                    if (overlap > 1) throw new BadRequestException("Sectors overlap!");
                }
            }
        }

        return venueRepository.save(venue);
    }

    public boolean checkIsActive(Long venueID) {
        Venue venue = findOne(venueID);
        return venue.getIsActive();
    }

    public Venue changeActiveStatus(Long venueID) {
        Venue venue = findOne(venueID);
        venue.setIsActive(!venue.getIsActive());
        venueRepository.save(venue);
        return venue;
    }

    public List<Venue> getAllActiveVenues() {
        return venueRepository.findByIsActiveTrue();
    }

    public Venue updateVenue(VenueBasicDTO venueInfo, Long venueID) {
        Venue venue = findOne(venueID);
        venue.setCity(venueInfo.getCity());
        venue.setLatitude(venueInfo.getLatitude());
        venue.setLongitude(venueInfo.getLongitude());
        venue.setName(venueInfo.getName());
        venue.setStreet(venueInfo.getStreet());
        venueRepository.save(venue);
        return venue;
    }

    public Venue addSectorToVenue(Sector sector, Long venueID) {
        Venue venue = findOne(venueID);
        if(!checkSectorName(venue.getSectors(), sector.getName())) throw new BadRequestException("Sector name already exists!");
        for (Sector secondSector: venue.getSectors()) {
            if (!sectorCanBeDrawn(sector, secondSector)) {
                throw new BadRequestException("Sectors overlap!");
            }
        }
        venue.getSectors().add(sector);
        return venueRepository.save(venue);
    }

    private boolean sectorCanBeDrawn(Sector fixedSector, Sector newSector) {
        if (fixedSector.getStartRow() >= newSector.getStartRow() + newSector.getRowsCount()) return true;
        if (fixedSector.getStartRow() + fixedSector.getRowsCount() <= newSector.getStartRow()) return true;
        if (fixedSector.getStartColumn() >= newSector.getStartColumn() + newSector.getColumnsCount()) return true;
        return fixedSector.getStartColumn() + fixedSector.getColumnsCount() <= newSector.getStartColumn();
    }

    private boolean checkSectorName(Set<Sector> sectors, String sectorName) {
        boolean check = true;
        for(Sector sec : sectors) {
            if (sec.getName().equals(sectorName)) {
                check = false;
                break;
            }
        }
        return check;
    }

}
