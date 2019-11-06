package com.siit.ticketist.service;

import com.siit.ticketist.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VenueService {

    @Autowired
    private VenueRepository venueRepository;

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
