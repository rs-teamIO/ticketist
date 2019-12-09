package com.siit.ticketist.controller;

import com.siit.ticketist.exceptions.NotFoundException;
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

@RestController
@RequestMapping("/api/venues")
public class VenueController {

    @Autowired
    private VenueService venueService;

    @GetMapping()
    public ResponseEntity<List<VenueDTO>> getVenues() {
        List<Venue> venues = venueService.findAll();
        List<VenueDTO> venuesDTO = new ArrayList<>();
        for(Venue venue : venues) {
            venuesDTO.add(new VenueDTO(venue));
        }
        return new ResponseEntity<>(venuesDTO, HttpStatus.OK);
    }

    @GetMapping(value="/{id}")
    public ResponseEntity<Object> getVenue(@PathVariable("id") Long id) {
        try {
            Venue venue = venueService.findOne(id);
            return new ResponseEntity<>(new VenueDTO(venue), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

//    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping()
    public ResponseEntity<Object> createVenue(@Valid @RequestBody VenueDTO venueDTO) {
        try {
            Venue venue = venueDTO.convertToEntity();
            return new ResponseEntity<>(new VenueDTO(venueService.save(venue)), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


}
