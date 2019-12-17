package com.siit.ticketist.service;

import com.siit.ticketist.exceptions.BadRequestException;
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

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

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
            }
        }



        try {
            this.emailService.sendTicketsPurchaseEmail(registeredUser, pdfTickets);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return resultTickets;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public Boolean acceptOrCancelReservations (List<Long> reservations, TicketStatus newStatus) {
        checkNumberOfTickets(reservations);
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

    public void checkStatusIsValid(Integer newStatus) {
        if(newStatus!=-1 && newStatus!=1)
            throw new BadRequestException("Status is not valid");
    }

    public List<Ticket> getUsersReservations() {
        RegisteredUser registeredUser = (RegisteredUser) userService.findCurrentUser();
        return ticketRepository.findAllReservationsByUser(registeredUser.getId());
    }

    public List<Ticket> getUsersTickets() {
        RegisteredUser registeredUser = (RegisteredUser) userService.findCurrentUser();
        return ticketRepository.findUsersTickets(registeredUser.getId());
    }

    public void checkNumberOfTickets(List<Long> tickets) {
        if(tickets.size() == 0) throw new BadRequestException("Ticket array is empty!");
    }

    public void checkMaxNumberOfReservationsPerUser(List<Long> reservations) {
        Optional<Ticket> ticket = ticketRepository.findById(reservations.get(0));
        if(ticket.isPresent()) {
            int numberOfReservations = ticketRepository.findUsersReservationsByEvent(userService.findCurrentUser().getId(), ticket.get().getEvent().getId()).size() + reservations.size();
            if(numberOfReservations > ticket.get().getEvent().getReservationLimit()) {
                throw new BadRequestException("Event limit of reservations is " + ticket.get().getEvent().getReservationLimit());
            }
        }
    }

    public void checkReservationDate(Long reservationID) {
        Optional<Ticket> ticket = ticketRepository.findById(reservationID);
        if(ticket.isPresent()) {
            if(new Date().after(ticket.get().getEvent().getReservationDeadline())) {
                throw new BadRequestException("Reservation deadline passed! Now you can only buy a ticket");
            }
        }
    }

    public void checkBuyTicketsDate(Long ticketID) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketID);
        if(ticket.isPresent()) {
            if(new Date().after(ticket.get().getEvent().getStartDate())) {
                throw new BadRequestException("Event already started!");
            }
        }
    }

    public void checkTicketDuplicates(List<Long> ticketIDS) {
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

    public void checkTicketsAndReservationsAvailability(List<Long> ticketIDS) {
        Optional<Ticket> ticket;
        for(Long ticketID : ticketIDS) {
            ticket = ticketRepository.findById(ticketID);
            if(ticket.isPresent()) {
                if(!ticket.get().getStatus().equals(TicketStatus.FREE)) {
                    System.out.println(ticket.get().getStatus());
                    throw new BadRequestException("Tickets are already taken");
                }
            } else {
                throw new BadRequestException("Ticket not found!");
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
