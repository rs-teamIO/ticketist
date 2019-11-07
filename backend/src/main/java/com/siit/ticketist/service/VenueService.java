package com.siit.ticketist.service;

import com.siit.ticketist.controller.exceptions.NotFoundException;
import com.siit.ticketist.model.Venue;
import com.siit.ticketist.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueService {

    @Autowired
    private VenueRepository venueRepository;

    public List<Venue> findAll() {
        return venueRepository.findAll();
    }

    public Venue findOne(Long id) throws NotFoundException {
        return venueRepository.findById(id).orElseThrow(() -> new NotFoundException("Venue not found"));
    }

    public Venue save(Venue venue) {
        return venueRepository.save(venue);
    }

}
