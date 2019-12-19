package com.siit.ticketist.service;

import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.exceptions.NotFoundException;
import com.siit.ticketist.model.Venue;
import com.siit.ticketist.repository.EventRepository;
import com.siit.ticketist.repository.VenueRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final VenueRepository venueRepository;
    private final EventRepository eventRepository;

    public ReportService(VenueRepository venueRepository, EventRepository eventRepository) {
        this.venueRepository = venueRepository;
        this.eventRepository = eventRepository;
    }

    /* Initial report */

    public Map<String, BigDecimal> getAllVenueRevenues() {
        return venueRepository.getAllVenueRevenues()
                .stream()
                .collect(Collectors.toMap(obj -> (String) obj[0], obj -> (BigDecimal) obj[1]));
    }

    public List<Object[]> findAllEventsReport() {
        return eventRepository.findAllEventsReport();
    }


    /* Specific venue report */

    public List<Object[]> findAllEventReportsByVenue(Long venueId) {
        return eventRepository.findAllEventReportsByVenue(venueId);
    }

    public Map<Integer, Float> getVenueChart(String criteria, Long venueId) {
        Venue venue = venueRepository.findById(venueId).orElseThrow(() -> new NotFoundException(String.format("Venue with id %d doesn't exist", venueId)));
        if (criteria.equalsIgnoreCase("TICKETS"))
            return getVenueTicketReports(venue.getId());
        else if (criteria.equalsIgnoreCase("REVENUE"))
            return getVenueRevenueReport(venue.getId());
        else
            throw new BadRequestException("Invalid venue report criteria [TICKETS|REVENUE]");
    }

    private Map<Integer, Float> getVenueTicketReports(Long venueId) {
        return eventRepository.getVenueTicketsReport(venueId)
                .stream()
                .collect(Collectors.toMap(obj -> (Integer) obj[0], obj -> ((BigInteger) obj[1]).floatValue()));
    }

    private Map<Integer, Float> getVenueRevenueReport(Long venueId) {
        return eventRepository.getVenueRevenueReport(venueId)
                .stream()
                .collect(Collectors.toMap(obj -> (Integer) obj[0], obj -> ((BigDecimal) obj[1]).floatValue()));
    }


}
