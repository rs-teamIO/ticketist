package com.siit.ticketist.service;

import com.siit.ticketist.controller.exceptions.NotFoundException;
import com.siit.ticketist.dto.VenueDTO;
import com.siit.ticketist.model.Venue;
import com.siit.ticketist.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

@Service
public class VenueService {

    @Autowired
    private VenueRepository venueRepository;

    public List<VenueDTO> findAll() {
        List<Venue> venues = venueRepository.findAll();
        List<VenueDTO> venuesDTO = new ArrayList<>();
        for(Venue venue : venues) {
            venuesDTO.add(new VenueDTO(venue));
        }
        return venuesDTO;
    }

    public VenueDTO findOne(Long id) throws NotFoundException {
        return new VenueDTO(venueRepository.findById(id).orElseThrow(() -> new NotFoundException("Venue not found")));
    }

    public VenueDTO save(VenueDTO venue) {
        return new VenueDTO(venueRepository.save(venue.convertToEntity()));
    }

    /*
    --------------------
        Reports
    --------------------
    */
    //TODO sta ako je prazna lista
    public Map<String, BigDecimal> getAllVenueRevenues(){
        Map<String, BigDecimal> venueRevenue =
                venueRepository.getAllVenueRevenues()
                        .stream()
                        .collect(Collectors.toMap(
                                obj -> (String)obj[0],
                                obj -> (BigDecimal) obj[1]));
        return venueRevenue;
    }

}
