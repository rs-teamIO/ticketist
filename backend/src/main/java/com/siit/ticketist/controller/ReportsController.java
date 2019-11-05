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

@RestController
@RequestMapping("api/reports")
public class ReportsController {

    @Autowired
    private EventService eventService;

    @Autowired
    private VenueService venueService;


    @GetMapping
    public ResponseEntity generateInitialReport(){
        ReportDTO dto = new ReportDTO(
                venueService.getAllVenueRevenues(), eventService.findAllEventsReport());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping(value="/{venueName}/{criteria}")
    public ResponseEntity generateVenueReport(@PathVariable("venueName") String venueName,
                                              @PathVariable("criteria") String criteria){
        //TODO
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    }

}
