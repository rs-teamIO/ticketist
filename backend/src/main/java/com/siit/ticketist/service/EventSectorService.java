package com.siit.ticketist.service;

import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.model.Event;
import com.siit.ticketist.model.EventSector;
import com.siit.ticketist.model.Ticket;
import com.siit.ticketist.model.TicketStatus;
import com.siit.ticketist.repository.EventSectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.abs;

/**
 * {@link EventSector} service layer.
 */
@Service
@RequiredArgsConstructor
public class EventSectorService {

    private final EventService eventService;
    private final EventSectorRepository eventSectorRepository;

    /**
     * Performs check if the {@link EventSector} with given ID
     * references an {@link Event} with given ID and returns it.
     *
     * @param eventId Unique identifier of the {@link Event}
     * @param eventSectorId Unique identifier of the {@link EventSector}
     * @return {@link EventSector} instace
     * @throws BadRequestException exception thrown in case of an error
     */
    public EventSector findByEventAndId(Long eventId, Long eventSectorId) {
        Event event = this.eventService.findOne(eventId);
        EventSector eventSector = event.getEventSectors().stream()
                .filter(es -> es.getId().equals(eventSectorId))
                .findFirst().orElseThrow(() -> new BadRequestException("Event doesn't contain sector with given ID."));
        eventSector = this.eventSectorRepository.findById(eventSector.getId())
                .orElseThrow(() -> new BadRequestException("Event sector with given ID not found."));
        return eventSector;
    }

    /**
     * Performs check if the {@link EventSector} with given ID
     * references an {@link Event} with given ID.
     *
     * If the event sector is present, the ticket price is updated.
     * The price of free tickets is also updated.
     *
     * @param eventId Unique identifier of the {@link Event}
     * @param eventSectorId Unique identifier of the {@link EventSector}
     * @param ticketPrice updated ticket price
     * @return {@link EventSector} instace
     * @throws BadRequestException exception thrown in case of an error
     */
    public EventSector updateTicketPrice(Long eventId, Long eventSectorId, BigDecimal ticketPrice) {
        if(ticketPrice.compareTo(BigDecimal.ZERO) < 0)
            throw new BadRequestException("Ticket price invalid.");

        EventSector eventSector = this.findByEventAndId(eventId, eventSectorId);

        eventSector.setTicketPrice(ticketPrice);
        eventSector.getTickets().stream()
                .filter(ticket -> ticket.getStatus().equals(TicketStatus.FREE))
                .forEach(ticket -> ticket.setPrice(ticketPrice));

        eventSector = this.eventSectorRepository.save(eventSector);

        return eventSector;
    }

    /**
     * Performs check if the {@link EventSector} with given ID
     * references an {@link Event} with given ID.
     *
     * If the event sector is present, the status is updated.
     * (fivkovic) Videti sta ce biti sa kartama (verovatno treba promeniti status)
     *
     * @param eventId Unique identifier of the {@link Event}
     * @param eventSectorId Unique identifier of the {@link EventSector}
     * @param isActive new status
     * @return {@link EventSector} instace
     * @throws BadRequestException exception thrown in case of an error
     */
    public EventSector updateStatus(Long eventId, Long eventSectorId, Boolean isActive) {
        EventSector eventSector = this.findByEventAndId(eventId, eventSectorId);
        if(eventSector.getIsActive().equals(isActive))
            return eventSector;
        if(!isActive.booleanValue()) {
            boolean allTicketsAreFree = eventSector.getTickets().stream()
                    .allMatch(ticket -> ticket.getStatus().equals(TicketStatus.FREE));
            if(!allTicketsAreFree)
                throw new BadRequestException("This event sector can't be disabled as there are purchased tickets.");
        }

        if(isActive.booleanValue())
            eventSector.getTickets().stream()
                    .forEach(ticket -> ticket.setStatus(TicketStatus.FREE));
        else
            eventSector.getTickets().stream()
                    .forEach(ticket -> ticket.setStatus(TicketStatus.USED));

        eventSector.setIsActive(isActive);
        eventSector = this.eventSectorRepository.save(eventSector);

        return eventSector;
    }

    /**
     * Performs check if the {@link EventSector} with given ID
     * references an {@link Event} with given ID.
     *
     * If the event sector is present, the capacity is updated.
     *
     * NOTE: Capacity can only be updated to a number that is below
     * the maxCapacity of referenced {@link com.siit.ticketist.model.Sector}
     * and above the number of sold tickets.
     *
     * @param eventId Unique identifier of the {@link Event}
     * @param eventSectorId Unique identifier of the {@link EventSector}
     * @param capacity new capacity
     * @return {@link EventSector} instace
     * @throws BadRequestException exception thrown in case of an error
     */
    public EventSector updateCapacity(Long eventId, Long eventSectorId, Integer capacity) {
        if(capacity < 1)
            throw new BadRequestException("Invalid capacity.");

        final EventSector eventSector = this.findByEventAndId(eventId, eventSectorId);
        if(eventSector.getNumeratedSeats().booleanValue())
            throw new BadRequestException("Can't change capacity. Seats are numerated.");

        Integer capacityDifference = capacity - eventSector.getCapacity();

        if(capacityDifference.intValue() == 0)
            return eventSector;

        boolean exceedsMaxCapacity = capacity > eventSector.getSector().getMaxCapacity();
        if(exceedsMaxCapacity)
            throw new BadRequestException("New capacity exceeds the max. capacity of the sector.");

        Long numberOfTakenTickets = eventSector.getTickets().stream()
                .filter(ticket -> ticket.getStatus().equals(TicketStatus.RESERVED) ||
                        ticket.getStatus().equals(TicketStatus.PAID))
                .count();
        if(numberOfTakenTickets > capacity)
            throw new BadRequestException(String.format("Can't set the capacity to %d. There are %d tickets already sold for this sector.",
                    capacity, numberOfTakenTickets));

        if(capacityDifference > 0) {
            Set<Ticket> newTickets = IntStream.range(0, capacityDifference).parallel()
                    .mapToObj(i -> new Ticket(null, -1, -1, eventSector.getTicketPrice(),
                            TicketStatus.FREE, 0L, eventSector, eventSector.getEvent(), null, null))
                    .collect(Collectors.toSet());
            eventSector.getTickets().addAll(newTickets);
        } else {
            List<Ticket> freeTickets = eventSector.getTickets().stream()
                    .filter(ticket -> ticket.getStatus().equals(TicketStatus.FREE))
                    .collect(Collectors.toList());
            IntStream.rangeClosed(0, abs(capacityDifference) - 1)
                    .forEach(i -> eventSector.getTickets().remove(freeTickets.get(i)));
        }

        eventSector.setCapacity(capacity);

        return this.eventSectorRepository.save(eventSector);
    }
}
