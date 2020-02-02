package com.siit.ticketist.controller;

import com.siit.ticketist.dto.InitialReportDTO;
import com.siit.ticketist.dto.VenueReportDTO;
import com.siit.ticketist.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    private final ReportService reportService;

    public ReportsController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * GET /api/reports
     *
     * Returns the initial report
     * @return {@link ResponseEntity} containing HttpStatus and message of the operation result
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity generateInitialReport(){
        InitialReportDTO dto = new InitialReportDTO(reportService.getAllVenueRevenues(), reportService.findAllEventsReport());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * GET /api/reports/{venueId}/{criteria}
     * Returns the report for a specific venue by given criteria
     *
     * @return {@link ResponseEntity} containing HttpStatus and message of the operation result
     */
    @GetMapping(value="/{venueId}/{criteria}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity generateVenueReport(@PathVariable("venueId") Long venueId,
                                              @PathVariable("criteria") String criteria) {
        VenueReportDTO dto = new VenueReportDTO(reportService.getVenueChart(criteria, venueId), reportService.findAllEventReportsByVenue(venueId));
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
