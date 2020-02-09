package com.siit.ticketist.controller;

import com.siit.ticketist.dto.SectorDTO;
import com.siit.ticketist.dto.VenueBasicDTO;
import com.siit.ticketist.dto.VenueDTO;
import com.siit.ticketist.dto.VenuePageDTO;
import com.siit.ticketist.model.Sector;
import com.siit.ticketist.model.Venue;
import com.siit.ticketist.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Venues REST controller.
 */
@RestController
@RequestMapping("/api/venues")
public class VenueController {

    private final VenueService venueService;

    @Autowired
    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @GetMapping(value="{id}")
    public ResponseEntity<VenueDTO> getVenue(@PathVariable("id") Long id) {
        Venue venue = venueService.findOne(id);
        return new ResponseEntity<>(new VenueDTO(venue), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<VenueDTO>> getVenues() {
        List<VenueDTO> venues = new ArrayList<>();
        venueService.findAll().stream()
                .map(VenueDTO::new)
                .forEachOrdered(venues::add);
        return new ResponseEntity<>(venues, HttpStatus.OK);
    }

    @GetMapping(value="/paged")
    public ResponseEntity<VenuePageDTO> getVenues(Pageable pageable) {
        List<VenueDTO> venues = new ArrayList<>();
        venueService.findAll(pageable).stream()
                .map(VenueDTO::new)
                .forEachOrdered(venues::add);
        return new ResponseEntity<>(new VenuePageDTO(venues, venueService.findAll(pageable).getTotalElements()), HttpStatus.OK);
    }

    @GetMapping(value="/active")
    public ResponseEntity<List<VenueDTO>> getActiveVenues() {
        List<Venue> venues = venueService.getAllActiveVenues();
        List<VenueDTO> venueDTOS = new ArrayList<>();
        for(Venue venue : venues) {
            venueDTOS.add(new VenueDTO(venue));
        }

        return new ResponseEntity<>(venueDTOS, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<VenueDTO> createVenue(@Valid @RequestBody VenueDTO venueDto) {
        Venue venueToBeCreated = venueDto.convertToEntity();
        Venue venue = venueService.save(venueToBeCreated);
        return new ResponseEntity<>(new VenueDTO(venue), HttpStatus.CREATED);
    }

    @PutMapping(value = "/change-status/{venueID}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Boolean> changeActiveStatus(@PathVariable("venueID") Long id) {
        Venue venue = venueService.changeActiveStatus(id);
        return new ResponseEntity<>(venue.getIsActive(), HttpStatus.OK);
    }

    @PutMapping(value = "{venueID}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<VenueDTO> updateVenue(@PathVariable("venueID") Long venueID, @Valid @RequestBody VenueBasicDTO venueBasic) {
        Venue venue = venueService.updateVenue(venueBasic, venueID);
        return new ResponseEntity<>(new VenueDTO(venue), HttpStatus.OK);
    }

    @PostMapping(value = "/sector/{venueID}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<VenueDTO> addSectorToVenue(@PathVariable("venueID") Long venueID, @Valid @RequestBody SectorDTO sectorDTO) {
        Sector sector = sectorDTO.convertToEntity();
        sector.setId(null);
        Venue venue = venueService.addSectorToVenue(sector, venueID);
        return new ResponseEntity<>(new VenueDTO(venue), HttpStatus.OK);
    }

}
