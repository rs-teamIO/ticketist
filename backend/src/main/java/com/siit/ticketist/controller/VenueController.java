package com.siit.ticketist.controller;

import com.siit.ticketist.controller.exceptions.NotFoundException;
import com.siit.ticketist.dto.VenueDTO;
import com.siit.ticketist.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/venues")
public class VenueController {

    @Autowired
    private VenueService venueService;

    @GetMapping()
    public ResponseEntity<List<VenueDTO>> getVenues() {
        return new ResponseEntity<>(venueService.findAll(), HttpStatus.OK);
    }

    @GetMapping(value="/{id}")
    public ResponseEntity<Object> getVenue(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(venueService.findOne(id), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping()
    public ResponseEntity<Object> createVenue(@Valid @RequestBody VenueDTO venue) {
        try {
            return new ResponseEntity<>(venueService.save(venue), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


}
