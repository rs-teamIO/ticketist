package com.siit.ticketist.controller;

import com.siit.ticketist.controller.exceptions.NotFoundException;
import com.siit.ticketist.dto.EventSectorDTO;
import com.siit.ticketist.dto.TicketDTO;
import com.siit.ticketist.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping(value = "/event/{id}")
    public ResponseEntity<List<TicketDTO>> findAllByEventId(@PathVariable("id") Long eventId) {
        return new ResponseEntity<>(ticketService.findAllByEventId(eventId), HttpStatus.OK);
    }

    @GetMapping(value = "/event/sector/{id}")
    public ResponseEntity<List<TicketDTO>> findAllByEventSectorId(@PathVariable("id") Long eventSectorId) {
        return new ResponseEntity<>(ticketService.findAllByEventSectorId(eventSectorId), HttpStatus.OK);
    }


    //@PreAuthorize("hasRole('REGISTERED_USER')")
    @PostMapping(value = "/reserve")
    public ResponseEntity<Object> makeReservations(@Valid @RequestBody List<TicketDTO> tickets) {
        try {
            return new ResponseEntity<>(ticketService.makeReservations(tickets), HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
