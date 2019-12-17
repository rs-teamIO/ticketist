package com.siit.ticketist.controller;

import com.siit.ticketist.dto.SuccessResponse;
import com.siit.ticketist.model.Ticket;
import com.siit.ticketist.dto.TicketDTO;
import com.siit.ticketist.model.TicketStatus;
import com.siit.ticketist.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Tickets REST controller.
 */
@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    /**
     * GET /api/tickets/event/{id}
     * Returns all tickets for an event with given id.
     *
     * @param eventId Id of the Event
     * @return {@link ResponseEntity} containing HttpStatus and a list of tickets
     */
    @GetMapping(value = "/event/{id}")
    public ResponseEntity<List<TicketDTO>> findAllByEventId(@PathVariable("id") Long eventId) {
        List<TicketDTO> tickets = new ArrayList<>();
        ticketService.findAllByEventId(eventId).stream()
                .map(TicketDTO::new)
                .forEachOrdered(tickets::add);
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    /**
     * GET /api/tickets/event/sector/{id}
     * Returns all tickets for an event sector with given id.
     *
     * @param eventSectorId Id of the Event Sector
     * @return {@link ResponseEntity} containing HttpStatus and a list of tickets
     */
    @GetMapping(value = "/event/sector/{id}")
    public ResponseEntity<List<TicketDTO>> findAllByEventSectorId(@PathVariable("id") Long eventSectorId) {
        List<TicketDTO> tickets = new ArrayList<>();
        ticketService.findAllByEventSectorId(eventSectorId).stream()
                .map(TicketDTO::new)
                .forEachOrdered(tickets::add);
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    /**
     * GET /api/tickets/reservations
     * Returns all reserved tickets for current user
     *
     * @return {@link ResponseEntity} containing HttpStatus and a list of tickets
     */
    @GetMapping(value="/reservations")
    @PreAuthorize("hasAuthority('REGISTERED_USER')")
    public ResponseEntity<List<TicketDTO>> findUserReservations() {
        List<TicketDTO> tickets = new ArrayList<>();
        ticketService.getUsersReservations().stream()
                .map(TicketDTO::new)
                .forEachOrdered(tickets::add);
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    /**
     * GET /api/tickets/
     * Returns all purchased tickets for current user
     *
     * @return {@link ResponseEntity} containing HttpStatus and a list of tickets
     */
    @GetMapping
    @PreAuthorize("hasAuthority('REGISTERED_USER')")
    public ResponseEntity<List<TicketDTO>> findUsersTickets() {
        List<TicketDTO> tickets = new ArrayList<>();
        ticketService.getUsersTickets().stream()
                .map(TicketDTO::new)
                .forEachOrdered(tickets::add);
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('REGISTERED_USER')")
    public ResponseEntity<Object> buyTickets(@Valid @RequestBody List<Long> tickets) {
        List<Ticket> ticket = ticketService.buyTickets(tickets,true);
        List<TicketDTO> ticketsDTO = new ArrayList<>();
        for (Ticket t : ticket) {
            ticketsDTO.add(new TicketDTO(t));
        }
        return new ResponseEntity<>(ticketsDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('REGISTERED_USER')")
    @PostMapping(value = "/reservations")
    public ResponseEntity<Object> makeReservations(@Valid @RequestBody List<Long> tickets) {
        List<Ticket> ticket = ticketService.buyTickets(tickets,false);
        List<TicketDTO> ticketsDTO = new ArrayList<>();
        for (Ticket t : ticket) {
            ticketsDTO.add(new TicketDTO(t));
        }
        return new ResponseEntity<>(ticketsDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('REGISTERED_USER')")
    @PostMapping(value="/reservations/accept")
    public ResponseEntity<Boolean> acceptReservations(@Valid @RequestBody List<Long> reservations) {
        return new ResponseEntity<>(ticketService.acceptOrCancelReservations(reservations, TicketStatus.PAID), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('REGISTERED_USER')")
    @PostMapping(value="/reservations/cancel")
    public ResponseEntity<Boolean> cancelReservations(@Valid @RequestBody List<Long> reservations) {
        return new ResponseEntity<>(ticketService.acceptOrCancelReservations(reservations, TicketStatus.FREE), HttpStatus.OK);
    }

    /**
     * GET /api/tickets/scan?ticketId=1
     * Endpoint used for {@link Ticket} scanning
     *
     * @param ticketId ID of the ticket
     * @return {@link ResponseEntity} containing HttpStatus and message of the operation result
     */
    @GetMapping(value="scan")
    public ResponseEntity scanTicket(@RequestParam("ticketId") Long ticketId) {
        this.ticketService.scanTicket(ticketId);
        return new ResponseEntity<>(new SuccessResponse("Ticket scanned successfully."), HttpStatus.OK);
    }
}
