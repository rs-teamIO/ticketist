package com.siit.ticketist.service;

import com.siit.ticketist.dto.VenueBasicDTO;
import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.exceptions.NotFoundException;
import com.siit.ticketist.model.Sector;
import com.siit.ticketist.model.Venue;
import com.siit.ticketist.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class VenueService {

    private final VenueRepository venueRepository;

    @Autowired
    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    public Venue findOne(Long id) {
        return this.venueRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venue not found."));
    }

    public List<Venue> findAll() {
        return this.venueRepository.findAll();
    }

    public Page<Venue> findAll(Pageable page) { return this.venueRepository.findAll(page); }

    public List<Venue> getAllActiveVenues() {
        return venueRepository.findByIsActiveTrue();
    }

    public Venue findOneByName(String name) {
        return this.venueRepository.findOneByName(name).orElseThrow(() -> new NotFoundException("Venue with given name not found"));
    }

    public Venue save(Venue venue) {
        int overlap = 0;
        int sameNameCounter = 0;
        venue.setIsActive(true);
        venue.setId(null);
        checkVenueNameAndLocation(venue.getName(), venue.getStreet(), venue.getCity());
        if (venue.getSectors().size() == 0) throw new BadRequestException("Venue must contain at least 1 sector");
        for (Sector firstSector: venue.getSectors()) {
            overlap = 0;
            sameNameCounter = 0;
            for (Sector secondSector: venue.getSectors()) {
                if (!sectorCanBeDrawn(firstSector, secondSector)) {
                    overlap++;
                    if (overlap > 1) throw new BadRequestException("Sectors overlap!");
                }
                if (firstSector.getName().equals(secondSector.getName())) {
                    sameNameCounter++;
                    if (sameNameCounter > 1) throw new BadRequestException("Sector name is not unique!");
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
        return venueRepository.save(venue);
    }

    public Venue updateVenue(VenueBasicDTO venueInfo, Long venueID) {
        Venue venue = findOne(venueID);
        checkVenueNameAndLocation(venueInfo.getName(), venueInfo.getStreet(), venueInfo.getCity());
        venue.setCity(venueInfo.getCity());
        venue.setLatitude(venueInfo.getLatitude());
        venue.setLongitude(venueInfo.getLongitude());
        venue.setName(venueInfo.getName());
        venue.setStreet(venueInfo.getStreet());
        return venueRepository.save(venue);
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

    private void checkVenueNameAndLocation(String name, String street, String city) {
        List<Venue> venues = findAll();
        for(Venue ven : venues) {
            if(ven.getName().equals(name) && ven.getStreet().equals(street) && ven.getCity().equals(city)) {
                throw new BadRequestException("Venue name, street and city combination must be unique!");
            }
        }

    }

}
