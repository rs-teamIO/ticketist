package com.siit.ticketist.service;

import com.siit.ticketist.controller.exceptions.BadRequestException;
import com.siit.ticketist.controller.exceptions.OptimisticLockException;
import com.siit.ticketist.model.*;
import com.siit.ticketist.repository.EventRepository;
import com.siit.ticketist.repository.EventSectorRepository;
import com.siit.ticketist.repository.SectorRepository;
import com.siit.ticketist.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.util.*;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserService userService;

    public List<Ticket> findAllByEventId(Long id) {
        return ticketRepository.findTicketsByEventId(id);
    }

    public List<Ticket> findAllByEventSectorId(Long id) {
        return ticketRepository.findTicketsByEventSectorId(id);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OptimisticLockException.class)
    public List<Ticket> buyTickets(List<Long> ticketIDS, boolean isBuy) {

        // Basic checks
        checkNumberOfTickets(ticketIDS);
        checkTicketDuplicates(ticketIDS);

        // Check tickets and reservations
        checkTicketsAndReservationsAvailability(ticketIDS);

        if(!isBuy) {
            //Check reservation rules
            checkReservationDate(ticketIDS.get(0));
            checkMaxNumberOfReservationsPerUser(ticketIDS);
        } else {
            checkBuyTicketsDate(ticketIDS.get(0));
        }

        //Initialization
        RegisteredUser registeredUser = (RegisteredUser) userService.findCurrentUser();
        List<Ticket> resultTickets = new ArrayList<>();
        Optional<Ticket> dbTicket;

        for(Long ticketID : ticketIDS) {
            dbTicket = ticketRepository.findOneById(ticketID);

            if (dbTicket.isPresent()) {
                dbTicket.get().setUser(registeredUser);
                if (isBuy) {
                    dbTicket.get().setStatus(1);
                } else {
                    dbTicket.get().setStatus(0);
                }
                resultTickets.add(dbTicket.get());
            }
        }
        return resultTickets;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public Boolean acceptOrCancelReservations (List<Long> reservations, Integer newStatus) {
        checkNumberOfTickets(reservations);

        RegisteredUser registeredUser = (RegisteredUser) userService.findCurrentUser();
        List<Ticket> tickets = ticketRepository.findTicketsByIdGroup(reservations, registeredUser.getId());

        if(tickets.size() != reservations.size()) throw new BadRequestException("Some of tickets cannot be found/already sold");

        Optional<Ticket> ticket;

        for(Long ticketId : reservations) {
            ticket = ticketRepository.findOneById(ticketId);
            if(ticket.isPresent()) {
                ticket.get().setStatus(newStatus);
                if(newStatus == -1) {
                    ticket.get().setUser(null);
                }
            }
        }

        return true;
    }

    public List<Ticket> getUsersReservations() {
        RegisteredUser registeredUser = (RegisteredUser) userService.findCurrentUser();
        return ticketRepository.findAllReservationsByUser(registeredUser.getId());
    }

    public List<Ticket> getUsersTickets() {
        RegisteredUser registeredUser = (RegisteredUser) userService.findCurrentUser();
        return ticketRepository.findUsersTickets(registeredUser.getId());
    }

    private void checkNumberOfTickets(List<Long> tickets) {
        if(tickets.size() == 0) throw new BadRequestException("Ticket array is empty!");
    }

    private void checkMaxNumberOfReservationsPerUser(List<Long> reservations) {
        Optional<Ticket> ticket = ticketRepository.findById(reservations.get(0));
        if(ticket.isPresent()) {
            int numberOfReservations = ticketRepository.findUsersReservationsByEvent(userService.findCurrentUser().getId(), ticket.get().getEvent().getId()).size() + reservations.size();
            if(numberOfReservations > ticket.get().getEvent().getReservationLimit()) {
                throw new BadRequestException("Event limit of reservations is " + ticket.get().getEvent().getReservationLimit());
            }
        }
    }

    private void checkReservationDate(Long reservationID) {
        Optional<Ticket> ticket = ticketRepository.findById(reservationID);
        if(ticket.isPresent()) {
            if(new Date().after(ticket.get().getEvent().getReservationDeadline())) {
                throw new BadRequestException("Reservation deadline passed! Now you can only buy a ticket");
            }
        }
    }

    private void checkBuyTicketsDate(Long ticketID) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketID);
        if(ticket.isPresent()) {
            if(new Date().after(ticket.get().getEvent().getStartDate())) {
                throw new BadRequestException("Event already started!");
            }
        }
    }

    private void checkTicketDuplicates(List<Long> ticketIDS) {
        int num = 0;
        for(Long t1 : ticketIDS) {
            num = 0;
            for(Long t2 : ticketIDS) {
                if(t1 == t2) {
                    num++;
                }
            }
            if(num > 1) {
                throw new BadRequestException("You cannot buy or reserve same ticket more than once!");
            }
        }
    }

    private void checkTicketsAndReservationsAvailability(List<Long> ticketIDS) {
        Optional<Ticket> ticket;
        for(Long ticketID : ticketIDS) {
            ticket = ticketRepository.findById(ticketID);
            if(ticket.isPresent()) {
                if(ticket.get().getStatus() != -1) {
                    System.out.println(ticket.get().getStatus());
                    throw new BadRequestException("Tickets are already taken");
                }
            } else {
                throw new BadRequestException("Ticket not found!");
            }
        }
    }
}
