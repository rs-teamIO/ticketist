package com.siit.ticketist.controller;

import com.siit.ticketist.dto.InitialReportDTO;
import com.siit.ticketist.dto.VenueReportDTO;
import com.siit.ticketist.service.ReportService;
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

    // TODO: Change do constructor DI
    @Autowired
    private ReportService reportService;


    /**
     * GET /api/reports
     * TODO: generateInitialReport
     *
     * @return TODO
     */
    @GetMapping
    //@PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity generateInitialReport(){
        InitialReportDTO dto = new InitialReportDTO(reportService.getAllVenueRevenues(), reportService.findAllEventsReport());
        return new ResponseEntity<>(dto, HttpStatus.OK);
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
        VenueReportDTO dto = new VenueReportDTO(reportService.getVenueChart(criteria, venueId), reportService.findAllEventReportsByVenue(venueId));
        return new ResponseEntity(dto, HttpStatus.OK);
    }
}
