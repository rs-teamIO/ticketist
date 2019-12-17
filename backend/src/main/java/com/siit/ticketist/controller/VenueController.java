package com.siit.ticketist.controller;

import com.siit.ticketist.dto.VenueDTO;
import com.siit.ticketist.model.Venue;
import com.siit.ticketist.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    /**
     * GET /api/venues
     * Returns all venues
     *
     * @return {@link ResponseEntity} containing HttpStatus and a list of venues
     */
    @GetMapping
    public ResponseEntity<List<VenueDTO>> getVenues() {
        List<VenueDTO> venues = new ArrayList<>();
        venueService.findAll().stream()
                .map(VenueDTO::new)
                .forEachOrdered(venues::add);
        return new ResponseEntity<>(venues, HttpStatus.OK);
    }

    /**
     * GET /api/venues/{id}
     * Returns a {@link Venue} with the requested ID
     *
     * @param id ID of the {@link Venue}
     * @return {@link ResponseEntity} containing HttpStatus and content
     */
    @GetMapping(value="{id}")
    public ResponseEntity<Object> getVenue(@PathVariable("id") Long id) {
        Venue venue = venueService.findOne(id);
        return new ResponseEntity<>(new VenueDTO(venue), HttpStatus.OK);
    }

    /**
     * POST /api/venues
     * Creates a new {@link Venue}.
     *
     * @param venueDto DTO containing venue info.
     * @return {@link ResponseEntity} containing the info about the created Venue
     */
    @PostMapping
    //@PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> createVenue(@Valid @RequestBody VenueDTO venueDto) {
        Venue venueToBeCreated = venueDto.convertToEntity();
        Venue venue = venueService.save(venueToBeCreated);
        return new ResponseEntity<>(new VenueDTO(venue), HttpStatus.CREATED);
    }
}
