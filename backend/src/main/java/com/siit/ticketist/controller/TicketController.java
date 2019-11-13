package com.siit.ticketist.controller;

import com.siit.ticketist.dto.TicketDTO;
import com.siit.ticketist.model.Ticket;
import com.siit.ticketist.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping(value = "/event/{id}")
    public ResponseEntity<List<TicketDTO>> findAllByEventId(@PathVariable("id") Long eventId) {
        List<Ticket> tickets = ticketService.findAllByEventId(eventId);
        List<TicketDTO> ticketsDTO = new ArrayList<>();
        for (Ticket ticket : tickets) {
            ticketsDTO.add(new TicketDTO(ticket));
        }
        return new ResponseEntity<>(ticketsDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/event/sector/{id}")
    public ResponseEntity<List<TicketDTO>> findAllByEventSectorId(@PathVariable("id") Long eventSectorId) {
        List<Ticket> tickets = ticketService.findAllByEventSectorId(eventSectorId);
        List<TicketDTO> ticketsDTO = new ArrayList<>();
        for (Ticket ticket : tickets) {
            ticketsDTO.add(new TicketDTO(ticket));
        }
        return new ResponseEntity<>(ticketsDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('REGISTERED_USER')")
    @GetMapping(value="/reservations")
    public ResponseEntity<List<TicketDTO>> findUsersReservations() {
        List<Ticket> tickets = ticketService.getUsersReservations();
        List<TicketDTO> ticketDTOList = new ArrayList<>();
        for(Ticket ticket : tickets) {
            ticketDTOList.add(new TicketDTO(ticket));
        }
        return new ResponseEntity<>(ticketDTOList, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('REGISTERED_USER')")
    @GetMapping()
    public ResponseEntity<List<TicketDTO>> findUsersTickets() {
        List<Ticket> tickets = ticketService.getUsersTickets();
        List<TicketDTO> ticketDTOList = new ArrayList<>();
        for(Ticket ticket : tickets) {
            ticketDTOList.add(new TicketDTO(ticket));
        }
        return new ResponseEntity<>(ticketDTOList, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('REGISTERED_USER')")
    @PostMapping()
    public ResponseEntity<Object> buyTickets(@Valid @RequestBody List<TicketDTO> tickets) {
        List<Ticket> ticketList = new ArrayList<>();
        for (TicketDTO ticketDTO : tickets) {
            ticketList.add(ticketDTO.convertToEntity());
        }
        return new ResponseEntity<>(ticketService.buyTickets(ticketList, true), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('REGISTERED_USER')")
    @PostMapping(value = "/reservations")
    public ResponseEntity<Object> makeReservations(@Valid @RequestBody List<TicketDTO> tickets) {
        List<Ticket> ticketList = new ArrayList<>();
        for (TicketDTO ticketDTO : tickets) {
            ticketList.add(ticketDTO.convertToEntity());
        }
        return new ResponseEntity<>(ticketService.buyTickets(ticketList, false), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('REGISTERED_USER')")
    @PostMapping(value="/reservations/accept")
    public ResponseEntity<Boolean> acceptReservations(@Valid @RequestBody List<Long> reservations) {
        return new ResponseEntity<>(ticketService.acceptOrCancelReservations(reservations, 1), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('REGISTERED_USER')")
    @PostMapping(value="/reservations/cancel")
    public ResponseEntity<Boolean> cancelReservations(@Valid @RequestBody List<Long> reservations) {
        return new ResponseEntity<>(ticketService.acceptOrCancelReservations(reservations, -1), HttpStatus.OK);
    }

}
