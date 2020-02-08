package com.siit.ticketist.service;

import com.siit.ticketist.dto.PdfTicket;
import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.exceptions.NotFoundException;
import com.siit.ticketist.exceptions.OptimisticLockException;
import com.siit.ticketist.model.RegisteredUser;
import com.siit.ticketist.model.Reservation;
import com.siit.ticketist.model.Ticket;
import com.siit.ticketist.model.TicketStatus;
import com.siit.ticketist.repository.ReservationRepository;
import com.siit.ticketist.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private final ReservationRepository reservationRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository, UserService userService, EmailService emailService,
                         ReservationRepository ticketGroupRepository) {
        this.userService = userService;
        this.emailService = emailService;
        this.ticketRepository = ticketRepository;
        this.reservationRepository = ticketGroupRepository;
    }

    public Ticket findOne(Long id) {
        return this.ticketRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ticket not found."));
    }

    public Ticket findOneLock(Long id) {
        return this.ticketRepository.findOneById(id)
                .orElseThrow(() -> new NotFoundException("Ticket not found"));
    }

    public List<Ticket> findAllByEventId(Long id) {
        return ticketRepository.findByEventId(id);
    }

    public List<Ticket> findAllByEventSectorId(Long id) {
        return ticketRepository.findByEventSectorId(id);
    }

    public Set<Ticket> findAllByReservationId(Long id) {
        return reservationRepository.findOneById(id).orElseThrow(() -> new NotFoundException("Reservation not found")).getTickets();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OptimisticLockException.class)
    public List<Ticket> buyTickets(List<Long> ticketIDS) throws MessagingException {

        // Check ticket list min length
        checkNumberOfTickets(ticketIDS);

        // Check ticket duplicates
        checkTicketDuplicates(ticketIDS);

        // Check are tickets free
        checkTicketsAndReservationsAvailability(ticketIDS);

        // Check if all tickets are related to same event
        checkTicketGroupEvent(ticketIDS);

        // Check event start date
        checkBuyTicketsDate(ticketIDS.get(0));

        // Initialization
        RegisteredUser registeredUser = (RegisteredUser) userService.findCurrentUser();
        List<Ticket> resultTickets = new ArrayList<>();
        List<PdfTicket> pdfTickets = new ArrayList<>();
        Ticket dbTicket;

        for(Long ticketID : ticketIDS) {
            dbTicket = findOneLock(ticketID);
            dbTicket.setUser(registeredUser);
            dbTicket.setStatus(TicketStatus.PAID);
            resultTickets.add(dbTicket);
            pdfTickets.add(new PdfTicket(dbTicket));
        }

        this.emailService.sendTicketsPurchaseEmail(registeredUser, pdfTickets);

        return resultTickets;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OptimisticLockException.class)
    public List<Ticket> reserveTickets(List<Long> ticketIDS) throws MessagingException {

        // Check ticket list min length
        checkNumberOfTickets(ticketIDS);

        // Check ticket duplicates
        checkTicketDuplicates(ticketIDS);

        // Check are tickets free
        checkTicketsAndReservationsAvailability(ticketIDS);

        // Check if all tickets are related to same event
        checkTicketGroupEvent(ticketIDS);

        // Check reservation deadline date
        checkReservationDate(ticketIDS.get(0));

        // Check maximum number of reservations per user
        checkMaxNumberOfReservationsPerUser(ticketIDS);

        //Initialization
        RegisteredUser registeredUser = (RegisteredUser) userService.findCurrentUser();
        List<Ticket> resultTickets = new ArrayList<>();
        List<PdfTicket> pdfTickets = new ArrayList<>();
        Reservation reservation = new Reservation();
        Ticket dbTicket;

        reservation.setEvent(findOne(ticketIDS.get(0)).getEvent());
        reservation.setUser(registeredUser);
        reservationRepository.save(reservation);

        for(Long ticketID : ticketIDS) {
            dbTicket = findOneLock(ticketID);
            dbTicket.setUser(registeredUser);
            dbTicket.setStatus(TicketStatus.RESERVED);
            dbTicket.setReservation(reservation);
            reservation.getTickets().add(dbTicket);
            resultTickets.add(dbTicket);
            pdfTickets.add(new PdfTicket(dbTicket));
        }

        this.emailService.sendTicketsPurchaseEmail(registeredUser, pdfTickets);

        return resultTickets;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public Boolean acceptOrCancelReservations (Long reservationId, TicketStatus newStatus) {

        // Check wanted status
        checkStatusIsValid(newStatus);

        //Initialization
        RegisteredUser registeredUser = (RegisteredUser) userService.findCurrentUser();
        Optional<Reservation> reservation = reservationRepository.findById(reservationId);

        if (reservation.isPresent()) {
            if (reservation.get().getUser().getId().equals(registeredUser.getId())) {
                for (Ticket ticket : reservation.get().getTickets()) {
                    ticket.setStatus(newStatus);
                    ticket.setReservation(null);
                    if (newStatus == TicketStatus.FREE) {
                        ticket.setUser(null);
                    }
                }
            } else {
                throw new BadRequestException("Reservation does not belong to that user!");
            }
        } else {
            throw new BadRequestException("Reservation does not exist");
        }

        reservationRepository.delete(reservation.get());

        return true;
    }

    public Page<Reservation> getUsersReservations(Pageable page) {
        RegisteredUser registeredUser = (RegisteredUser) userService.findCurrentUser();
        return reservationRepository.findAllByUserId(registeredUser.getId(), page);
    }

    public long getTotalNumberOfUsersReservations() {
        RegisteredUser registeredUser = (RegisteredUser) userService.findCurrentUser();
        return reservationRepository.countByUserId(registeredUser.getId());
    }

    public List<Ticket> getUsersBoughtTickets() {
        RegisteredUser registeredUser = (RegisteredUser) userService.findCurrentUser();
        return ticketRepository.findAllByUserIdAndStatus(registeredUser.getId(), TicketStatus.PAID);
    }

    public void checkStatusIsValid(TicketStatus newStatus) {
        if(newStatus!=TicketStatus.FREE && newStatus!=TicketStatus.PAID)
            throw new BadRequestException("Status is not valid");
    }

    public void checkNumberOfTickets(List<Long> tickets) {
        if(tickets.isEmpty()) throw new BadRequestException("Ticket array is empty!");
    }

    public void checkMaxNumberOfReservationsPerUser(List<Long> reservations) {
        Ticket ticket = findOne(reservations.get(0));
        int numberOfReservations = ticketRepository.findAllByUserIdAndStatusAndEventId(userService.findCurrentUser().getId(), TicketStatus.RESERVED, ticket.getEvent().getId()).size() + reservations.size();
        if (numberOfReservations > ticket.getEvent().getReservationLimit()) {
            throw new BadRequestException("Event limit of reservations is " + ticket.getEvent().getReservationLimit());
        }
    }

    public void checkTicketGroupEvent(List<Long> reservations) {
        Ticket ticket;
        Long eventID = -1L;
        for(int i = 0 ; i < reservations.size(); i++) {
            ticket = findOne(reservations.get(i));
            if (i==0) {
                eventID = ticket.getEvent().getId();
            } else {
                if (!ticket.getEvent().getId().equals(eventID)) {
                    throw new BadRequestException("Every ticket should be for same event!");
                }
            }
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
                .orElseThrow(() -> new BadRequestException("No ticket found with specified ID."));

        if(!ticket.getStatus().equals(TicketStatus.PAID))
            throw new BadRequestException("Unable to scan ticket with requested ID.");

        ticket.setStatus(TicketStatus.USED);
        this.ticketRepository.save(ticket);

        return ticket;
    }
}
