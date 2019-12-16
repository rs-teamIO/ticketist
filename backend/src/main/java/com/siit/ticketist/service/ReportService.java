package com.siit.ticketist.service;

import com.siit.ticketist.controller.exceptions.BadRequestException;
import com.siit.ticketist.repository.EventRepository;
import com.siit.ticketist.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private EventRepository eventRepository;

    /*
        Initial report
     */
    public Map<String, BigDecimal> getAllVenueRevenues() {
        Map<String, BigDecimal> venueRevenue =
                venueRepository.getAllVenueRevenues()
                        .stream()
                        .collect(Collectors.toMap(
                                obj -> (String) obj[0],
                                obj -> (BigDecimal) obj[1]));
        return venueRevenue;
    }

    public List<Object[]> findAllEventsReport() {
        return eventRepository.findAllEventsReport();
    }

    /*
        Specific venue report
     */

    public List<Object[]> findAllEventReportsByVenue(Long venueId) {
        return eventRepository.findAllEventReportsByVenue(venueId);
    }

    public Map<Integer, Float> getVenueChart(String criteria, Long venueId) {
        //TODO napisi metodu koja proverava da li venue sa tim id postoji
        if (criteria.toUpperCase().equals("TICKETS"))
            return getVenueTicketReports(venueId);
        else if (criteria.toUpperCase().equals("REVENUE"))
            return getVenueRevenueReport(venueId);
        else
            throw new BadRequestException("Invalid venue report criteria [TICKETS|REVENUE]");
    }

    private Map<Integer, Float> getVenueTicketReports(Long venueId) {
        List<Object[]> lista = eventRepository.getVenueTicketsReport(venueId);
        Map<Integer, Float> venueTicketSales =
                eventRepository.getVenueTicketsReport(venueId)
                        .stream()
                        .collect(Collectors.toMap(
                                obj -> (Integer) obj[0],
                                obj -> ((BigInteger) obj[1]).floatValue()));
        return venueTicketSales;
    }

    private Map<Integer, Float> getVenueRevenueReport(Long venueId) {
        List<Object[]> lista = eventRepository.getVenueRevenueReport(venueId);

        Map<Integer, Float> venueRevenues =
                eventRepository.getVenueRevenueReport(venueId)
                        .stream()
                        .collect(Collectors.toMap(
                                obj -> (Integer) obj[0],
                                obj -> ((BigDecimal) obj[1]).floatValue()));
        return venueRevenues;
    }


}
