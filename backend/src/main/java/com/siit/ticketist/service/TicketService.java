package com.siit.ticketist.service;

import com.siit.ticketist.controller.exceptions.BadRequestException;
import com.siit.ticketist.dto.TicketDTO;
import com.siit.ticketist.model.EventSector;
import com.siit.ticketist.model.RegisteredUser;
import com.siit.ticketist.model.Sector;
import com.siit.ticketist.model.Ticket;
import com.siit.ticketist.repository.EventSectorRepository;
import com.siit.ticketist.repository.SectorRepository;
import com.siit.ticketist.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EventSectorRepository eventSectorRepository;

    @Autowired
    private SectorRepository sectorRepository;

    public List<TicketDTO> findAllByEventId(Long id) {
        List<Ticket> tickets = ticketRepository.findTicketsByEventId(id);
        List<TicketDTO> ticketsDTO = new ArrayList<>();
        for (Ticket ticket : tickets) {
            ticketsDTO.add(new TicketDTO(ticket));
        }
        return ticketsDTO;
    }

    public List<TicketDTO> findAllByEventSectorId(Long id) {
        List<Ticket> tickets = ticketRepository.findTicketsByEventSectorId(id);
        List<TicketDTO> ticketsDTO = new ArrayList<>();
        for (Ticket ticket : tickets) {
            ticketsDTO.add(new TicketDTO(ticket));
        }
        return ticketsDTO;
    }

    @Transactional
    public List<Ticket> buyTickets (List<Ticket> ticketsToBuy) {
        checkTicketsAndReservations(ticketsToBuy);

        RegisteredUser registeredUser = (RegisteredUser) userService.findCurrentUser();

        List<Ticket> boughtTickets = new ArrayList<>();

        for(Ticket ticket : ticketsToBuy) {
            ticket.setUser(registeredUser);
            ticket.setIsPaid(true);
            ticketRepository.save(ticket);
            boughtTickets.add(ticket);
        }

        return boughtTickets;
    }

    @Transactional
    public List<Ticket> makeReservations (List<Ticket> reservations) throws BadRequestException {
        checkTicketsAndReservations(reservations);

        RegisteredUser registeredUser = (RegisteredUser) userService.findCurrentUser();

        List<Ticket> reservedTickets = new ArrayList<>();

        for(Ticket ticket : reservations) {
            ticket.setUser(registeredUser);
            ticket.setIsPaid(false);
            ticketRepository.save(ticket);
            reservedTickets.add(ticket);
        }

        return reservedTickets;
    }

    @Transactional
    public Boolean acceptReservations (List<Long> reservations) {
        RegisteredUser registeredUser = (RegisteredUser) userService.findCurrentUser();
        List<Ticket> tickets = ticketRepository.findTicketsByIdGroup(reservations, registeredUser.getId());
        if(tickets.size() != reservations.size()) throw new BadRequestException("Some of tickets cannot be found/already sold");
        Optional<Ticket> ticket;
        for(Long ticketId : reservations) {
            ticket = ticketRepository.findById(ticketId);
            ticket.ifPresent(value -> value.setIsPaid(true));
        }
        return true;
    }

    @Transactional
    public Boolean cancelReservations (List<Long> reservations) {
        RegisteredUser registeredUser = (RegisteredUser) userService.findCurrentUser();
        List<Ticket> tickets = ticketRepository.findTicketsByIdGroup(reservations, registeredUser.getId());
        if(tickets.size() != reservations.size()) throw new BadRequestException("Some of tickets cannot be found/already sold");
        Optional<Ticket> ticket;
        for(Long ticketId : reservations) {
            ticket = ticketRepository.findById(ticketId);
            ticket.ifPresent(value -> ticketRepository.delete(value));
        }
        return true;
    }

    public List<Ticket> getUsersReservations() {
        RegisteredUser registeredUser = (RegisteredUser) userService.findCurrentUser();
        return ticketRepository.findUsersReservations(registeredUser.getId());
    }

    public List<Ticket> getUsersTickets() {
        RegisteredUser registeredUser = (RegisteredUser) userService.findCurrentUser();
        return ticketRepository.findUsersTickets(registeredUser.getId());
    }

    private boolean checkSeatsAvailability(List<Ticket> reservations) {
        boolean check = false;

        for (Ticket ticket : reservations) {
            check = checkTicketExistence(ticket.getEventSector().getId(), ticket.getNumberColumn(), ticket.getNumberRow());
            if(check) break;
        }

        return !check;
    }

    private void checkTicketsAndReservations(List<Ticket> tickets) {
        boolean check = checkSeatsValidation(tickets);

        if(!check) {
            throw new BadRequestException("Seat numeration is incorrect");
        }

        check = checkSeatsAvailability(tickets);

        if(!check) {
            throw new BadRequestException("Tickets are already taken");
        }
    }

    private boolean checkSeatsValidation(List<Ticket> reservations) {
        boolean check = true;
        Optional<EventSector> eventSector;
        Optional<Sector> sector;

        for (Ticket ticket : reservations) {
            eventSector = eventSectorRepository.findById(ticket.getEventSector().getId());
            if(eventSector.isPresent() && eventSector.get().getNumeratedSeats()) {
                sector = sectorRepository.findById(eventSector.get().getSector().getId());
                if(sector.isPresent()) {
                    if(ticket.getNumberRow() > sector.get().getRowsCount() || ticket.getNumberColumn() > sector.get().getColumnsCount()) {
                        check = false;
                    }
                }
                if(!check) break;
            }
        }

        return check;
    }

    private boolean checkTicketExistence(Long eventSectorId, Integer col, Integer row) {
        Optional<Ticket> ticket = ticketRepository.checkTicketExistence(eventSectorId, col, row);
        return ticket.isPresent();
    }

}
