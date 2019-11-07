package com.siit.ticketist.service;

import com.siit.ticketist.controller.exceptions.BadRequestException;
import com.siit.ticketist.model.*;
import com.siit.ticketist.repository.EventRepository;
import com.siit.ticketist.repository.EventSectorRepository;
import com.siit.ticketist.repository.SectorRepository;
import com.siit.ticketist.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
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

    @Autowired
    private EventRepository eventRepository;

    public List<Ticket> findAllByEventId(Long id) {
        return ticketRepository.findTicketsByEventId(id);
    }

    public List<Ticket> findAllByEventSectorId(Long id) {
        return ticketRepository.findTicketsByEventSectorId(id);
    }

    @Transactional
    public List<Ticket> buyTickets (List<Ticket> ticketsToBuy) {

        ArrayList<ArrayList<Ticket>> ticketLists = splitTicketsByNumeration(ticketsToBuy);

        ArrayList<Ticket> numerated = ticketLists.get(0);
        ArrayList<Ticket> nonNumerated = ticketLists.get(1);
        Optional<EventSector> eventSector;

        checkTicketsAndReservations(numerated);
        checkTicketsAndReservationsNonumeration(nonNumerated);

        RegisteredUser registeredUser = (RegisteredUser) userService.findCurrentUser();

        List<Ticket> boughtTickets = new ArrayList<>();

        for(Ticket ticket : ticketsToBuy) {
            ticket.setUser(registeredUser);
            ticket.setIsPaid(true);
            eventSector = eventSectorRepository.findById(ticket.getEventSector().getId());
            eventSector.ifPresent(sector -> ticket.setPrice(sector.getTicketPrice()));
            ticketRepository.save(ticket);
            boughtTickets.add(ticket);
        }

        return boughtTickets;
    }


    @Transactional
    public List<Ticket> makeReservations (List<Ticket> reservations) throws BadRequestException {

        checkMaxNumberOfReservationsPerUser(reservations);
        ArrayList<ArrayList<Ticket>> ticketLists = splitTicketsByNumeration(reservations);

        ArrayList<Ticket> numerated = ticketLists.get(0);
        ArrayList<Ticket> nonNumerated = ticketLists.get(1);
        Optional<EventSector> eventSector;
        List<Ticket> reservedTickets = new ArrayList<>();

        checkTicketsAndReservations(numerated);
        checkTicketsAndReservationsNonumeration(nonNumerated);

        RegisteredUser registeredUser = (RegisteredUser) userService.findCurrentUser();


        for(Ticket ticket : reservations) {
            ticket.setUser(registeredUser);
            ticket.setIsPaid(false);
            eventSector = eventSectorRepository.findById(ticket.getEventSector().getId());
            eventSector.ifPresent(sector -> ticket.setPrice(sector.getTicketPrice()));
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

    private void checkMaxNumberOfReservationsPerUser(List<Ticket> reservations) {
        boolean check = true;
        Optional<Event> event = eventRepository.findById(reservations.get(0).getEvent().getId());
        if(event.isPresent()){
            int numberOfReservations = ticketRepository.findUsersReservations(userService.findCurrentUser().getId()).size() + reservations.size();
            System.out.println(numberOfReservations + "   " + event.get().getReservationLimit());
            if(numberOfReservations > event.get().getReservationLimit()) {
                check = false;
            }
        }
        if(!check) {
            throw new BadRequestException("Event limit of reservations is " + event.get().getReservationLimit());
        }
    }

    private ArrayList<ArrayList<Ticket>> splitTicketsByNumeration(List<Ticket> tickets) {
        ArrayList<Ticket> numeratedTickets = new ArrayList<>();
        ArrayList<Ticket> nonNumeratedTickets = new ArrayList<>();
        Optional<EventSector> eventSector;

        for(Ticket ticket : tickets){
            eventSector = eventSectorRepository.findById(ticket.getEventSector().getId());
            if(eventSector.isPresent()){
                if(eventSector.get().getNumeratedSeats()) {
                    numeratedTickets.add(ticket);
                } else {
                    nonNumeratedTickets.add(ticket);
                }
            }
        }

        ArrayList<ArrayList<Ticket>> result = new ArrayList<ArrayList<Ticket>>();
        result.add(numeratedTickets);
        result.add(nonNumeratedTickets);
        return result;
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

    private boolean checkSeatsAvailability(List<Ticket> reservations) {
        boolean check = false;

        for (Ticket ticket : reservations) {
            check = checkTicketExistence(ticket.getEventSector().getId(), ticket.getNumberColumn(), ticket.getNumberRow());
            if(check) break;
        }

        return !check;
    }

    private boolean checkTicketExistence(Long eventSectorId, Integer col, Integer row) {
        Optional<Ticket> ticket = ticketRepository.checkTicketExistence(eventSectorId, col, row);
        return ticket.isPresent();
    }

    private void checkTicketsAndReservationsNonumeration(List<Ticket> nonNumerated) {
        boolean check = checkTicketNumeration(nonNumerated);

        if(!check){
            throw new BadRequestException("Not enough available seats left");
        }
    }

    private boolean checkTicketNumeration(List<Ticket> nonNumerated) {
        Optional<EventSector> eventSector;
        boolean check = true;

        int noSeats = 0;

        for(Ticket ticket : nonNumerated){
            eventSector = eventSectorRepository.findById(ticket.getEventSector().getId());
            if(eventSector.isPresent() && !eventSector.get().getNumeratedSeats()){
                noSeats = 0;
                for(Ticket t : nonNumerated){
                    if(ticket.getEventSector().getId().equals(t.getEventSector().getId())){
                        noSeats++;
                    }
                }
                if(eventSector.get().getCapacity() - ticketRepository.findTicketsByEventSectorId(eventSector.get().getId()).size() - noSeats < 0) check = false;
            }
            ticket.setNumberColumn(-1);
            ticket.setNumberRow(-1);
            if(!check) break;
        }
        return check;
    }


}
