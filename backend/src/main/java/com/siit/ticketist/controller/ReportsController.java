package com.siit.ticketist.controller;

import com.siit.ticketist.dto.ReportDTO;
import com.siit.ticketist.service.EventService;
import com.siit.ticketist.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Reports REST controller.
 */
@RestController
@RequestMapping("api/reports")
public class ReportsController {

    private final EventService eventService;
    private final VenueService venueService;

    @Autowired
    public ReportsController(EventService eventService, VenueService venueService) {
        this.eventService = eventService;
        this.venueService = venueService;
    }

    /**
     * GET /api/reports
     * TODO generateInitialReport
     *
     * @return TODO
     */
    @GetMapping
    //@PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity generateInitialReport() {
        ReportDTO dto = new ReportDTO();
        return new ResponseEntity<>(dto.generateInitialReport(venueService.getAllVenueRevenues(), eventService.findAllEventsReport()),
                HttpStatus.OK);
    }

    /**
     * GET /api/reports/{venueId}/{criteria}
     * TODO: generateVenueReport
     *
     * @return TODO
     */
    @GetMapping(value="/{venueId}/{criteria}")
    //@PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity generateVenueReport(@PathVariable("venueId") Long venueId,
                                              @PathVariable("criteria") String criteria) {
        ReportDTO dto = new ReportDTO();
        return new ResponseEntity<>(dto.generateSpecificReport(eventService.getVenueChart(criteria, venueId),
                eventService.findAllEventReportsByVenue(venueId)), HttpStatus.OK);
    }
}
