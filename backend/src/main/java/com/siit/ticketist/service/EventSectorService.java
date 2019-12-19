package com.siit.ticketist.service;

import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.model.Event;
import com.siit.ticketist.model.EventSector;
import com.siit.ticketist.model.TicketStatus;
import com.siit.ticketist.repository.EventSectorRepository;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * {@link EventSector} service layer.
 */
@Service
public class EventSectorService {

    private final EventService eventService;
    private final EventSectorRepository eventSectorRepository;

    public EventSectorService(EventService eventService, EventSectorRepository eventSectorRepository) {
        this.eventService = eventService;
        this.eventSectorRepository = eventSectorRepository;
    }

    public EventSector findByEventAndId(Long eventId, Long eventSectorId) {
        Event event = this.eventService.findOne(eventId);
        EventSector eventSector = event.getEventSectors().stream()
                .filter(es -> es.getId().equals(eventSectorId))
                .findFirst().orElseThrow(() -> new BadRequestException("Event doesn't contain sector with given ID."));
        eventSector = this.eventSectorRepository.findById(eventSector.getId())
                .orElseThrow(() -> new BadRequestException("Event sector with given ID not found."));
        return eventSector;
    }

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
        eventSector.setIsActive(isActive);
        eventSector = this.eventSectorRepository.save(eventSector);

        return eventSector;
    }

    public EventSector updateCapacity(Long eventId, Long eventSectorId, Integer capacity) {
        if(capacity < 1)
            throw new BadRequestException("Invalid capacity.");

        EventSector eventSector = this.findByEventAndId(eventId, eventSectorId);
        if(eventSector.getNumeratedSeats().booleanValue())
            throw new BadRequestException("Can't change capacity. Seats are numerated.");

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

        // TODO: Videti sta ce biti sa kartama u slucaju smanjivanja kapaciteta

        eventSector.setCapacity(capacity);
        eventSector = this.eventSectorRepository.save(eventSector);

        return eventSector;
    }
}
