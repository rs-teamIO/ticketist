package com.siit.ticketist.dto;

import com.siit.ticketist.model.Event;
import com.siit.ticketist.model.EventSector;
import com.siit.ticketist.model.Sector;
import com.siit.ticketist.model.Ticket;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Getter @Setter
public class EventSectorDTO {

    private Long id;

    @NotNull(message = "ticket price cannot be null")
    private BigDecimal ticketPrice;

    @NotNull(message = "are seats numerated cannot be null")
    private Boolean numeratedSeats;

    @NotNull(message = "date cannot be null")
    private Date date;

    @NotNull(message = "sector id cannot be null")
    private Long sectorId;

    @NotNull(message = "event id cannot be null")
    private Long eventId;

    public EventSectorDTO(EventSector eventSector) {
        this.id = eventSector.getId();
        this.ticketPrice = eventSector.getTicketPrice();
        this. numeratedSeats = eventSector.getNumeratedSeats();
        this.date = eventSector.getDate();
        this.sectorId = eventSector.getSector().getId();
        this.eventId = eventSector.getEvent().getId();
    }

    public EventSector convertToEntity(){
        EventSector eventSector = new EventSector();
        eventSector.setId(this.id);
        eventSector.setDate(this.date);
        eventSector.setNumeratedSeats(this.numeratedSeats);
        eventSector.setTicketPrice(this.ticketPrice);
        Event event = new Event();
        event.setId(this.eventId);
        eventSector.setEvent(event);
        Sector sector = new Sector();
        sector.setId(this.sectorId);
        eventSector.setSector(sector);

        return eventSector;
    }

}

