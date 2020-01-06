package com.siit.ticketist.service;

import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.exceptions.NotFoundException;
import com.siit.ticketist.exceptions.OptimisticLockException;
import com.siit.ticketist.dto.PdfTicket;
import com.siit.ticketist.model.*;
import com.siit.ticketist.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.util.*;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class TicketService {

    private final TicketRepository ticketRepository;

    private final UserService userService;

    private final EmailService emailService;

    @Autowired
    public TicketService(TicketRepository ticketRepository, UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
        this.ticketRepository = ticketRepository;
    }

    public Ticket findOne(Long id) {
        return this.ticketRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ticket not found."));
    }

    public List<Ticket> findAllByEventId(Long id) {
        return ticketRepository.findByEventId(id);
    }

    public List<Ticket> findAllByEventSectorId(Long id) {
        return ticketRepository.findByEventSectorId(id);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OptimisticLockException.class)
    public List<Ticket> buyTickets(List<Long> ticketIDS, boolean isBuy) throws MessagingException {

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

        List<PdfTicket> pdfTickets = new ArrayList<>();

        for(Long ticketID : ticketIDS) {
            dbTicket = ticketRepository.findOneById(ticketID);

            if (dbTicket.isPresent()) {
                dbTicket.get().setUser(registeredUser);
                if (isBuy) {
                    dbTicket.get().setStatus(TicketStatus.PAID);
                } else {
                    dbTicket.get().setStatus(TicketStatus.RESERVED);
                }
                resultTickets.add(dbTicket.get());
                pdfTickets.add(new PdfTicket(dbTicket.get()));
            } else {
                throw new BadRequestException("Ticket does not exist!");
            }
        }

        this.emailService.sendTicketsPurchaseEmail(registeredUser, pdfTickets);

        return resultTickets;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public Boolean acceptOrCancelReservations (List<Long> reservations, TicketStatus newStatus) {
        checkNumberOfTickets(reservations);
        checkTicketDuplicates(reservations);
        checkStatusIsValid(newStatus);
        RegisteredUser registeredUser = (RegisteredUser) userService.findCurrentUser();
        List<Ticket> tickets = ticketRepository.findTicketsByIdGroup(reservations, registeredUser.getId());

        if(tickets.size() != reservations.size()) throw new BadRequestException("Some of tickets cannot be found/already sold");

        Optional<Ticket> ticket;

        for(Long ticketId : reservations) {
            ticket = ticketRepository.findOneById(ticketId);
            if(ticket.isPresent()) {
                ticket.get().setStatus(newStatus);
                if(newStatus == TicketStatus.FREE) {
                    ticket.get().setUser(null);
                }
            }
        }

        return true;
    }

    public void checkStatusIsValid(TicketStatus newStatus) {
        if(newStatus!=TicketStatus.FREE && newStatus!=TicketStatus.PAID)
            throw new BadRequestException("Status is not valid");
    }

    public List<Ticket> getUsersReservations() {
        RegisteredUser registeredUser = (RegisteredUser) userService.findCurrentUser();
        return ticketRepository.findAllReservationsByUser(registeredUser.getId());
    }

    public List<Ticket> getUsersBoughtTickets() {
        RegisteredUser registeredUser = (RegisteredUser) userService.findCurrentUser();
        return ticketRepository.findUsersBoughtTickets(registeredUser.getId());
    }

    public void checkNumberOfTickets(List<Long> tickets) {
        if(tickets.isEmpty()) throw new BadRequestException("Ticket array is empty!");
    }

    public void checkMaxNumberOfReservationsPerUser(List<Long> reservations) {
        Ticket ticket = findOne(reservations.get(0));
        int numberOfReservations = ticketRepository.findUsersReservationsByEvent(userService.findCurrentUser().getId(), ticket.getEvent().getId()).size() + reservations.size();
        if(numberOfReservations > ticket.getEvent().getReservationLimit()) {
            throw new BadRequestException("Event limit of reservations is " + ticket.getEvent().getReservationLimit());
        }
    }

    public void checkReservationDate(Long reservationID) {
        Ticket ticket = findOne(reservationID);
        if(new Date().after(ticket.getEvent().getReservationDeadline()))
            throw new BadRequestException("Reservation deadline passed! Now you can only buy a ticket");
    }

    public void checkBuyTicketsDate(Long ticketID) {
        Ticket ticket = findOne(ticketID);
        if(new Date().after(ticket.getEvent().getStartDate())) {
            throw new BadRequestException("Event already started!");
        }
    }

    public void checkTicketDuplicates(List<Long> ticketIDS) {
        int num = 0;
        for(Long t1 : ticketIDS) {
            num = 0;
            for(Long t2 : ticketIDS) {
                if(t1.equals(t2)) {
                    num++;
                }
            }
            if(num > 1) {
                throw new BadRequestException("Same tickets detected!");
            }
        }
    }

    public void checkTicketsAndReservationsAvailability(List<Long> ticketIDS) {
        Ticket ticket;
        for(Long ticketID : ticketIDS) {
            ticket = findOne(ticketID);

            if(!ticket.getStatus().equals(TicketStatus.FREE)) {
                throw new BadRequestException("Tickets are already taken");
            }
        }
    }

    /**
     * Changes the status of the {@link Ticket} with given ID to USED
     *
     * @param id Unique identifier of the ticket
     * @return {@link Ticket} with changed status
     */
    public Ticket scanTicket(Long id) {
        final Ticket ticket = ticketRepository.findOneById(id)
                .orElseThrow(() -> new BadRequestException("No ticket found with specified id."));

        if(!ticket.getStatus().equals(TicketStatus.PAID))
            throw new BadRequestException("Unable to scan ticket with requested ID.");

        ticket.setStatus(TicketStatus.USED);
        this.ticketRepository.save(ticket);

        return ticket;
    }
}
