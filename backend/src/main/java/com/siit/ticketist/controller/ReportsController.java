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

@RestController
@RequestMapping("api/reports")
public class ReportsController {

    @Autowired
    private ReportService reportService;


    @GetMapping
    //@PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity generateInitialReport(){
        return new ResponseEntity<>(new InitialReportDTO(reportService.getAllVenueRevenues(), reportService.findAllEventsReport()),
                HttpStatus.OK);
    }

    @GetMapping(value="/{venueId}/{criteria}")
    //@PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity generateVenueReport(@PathVariable("venueId") Long venueId,
                                              @PathVariable("criteria") String criteria){
        return new ResponseEntity(new VenueReportDTO(reportService.getVenueChart(criteria, venueId), reportService.findAllEventReportsByVenue(venueId)),
                HttpStatus.OK);
    }

}
