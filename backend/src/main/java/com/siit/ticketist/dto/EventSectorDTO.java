package com.siit.ticketist.dto;

import com.siit.ticketist.model.Event;
import com.siit.ticketist.model.EventSector;
import com.siit.ticketist.model.Sector;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Getter @Setter @NoArgsConstructor
public class EventSectorDTO {

    private Long id;

    @NotNull(message = "ticket price cannot be null")
    private BigDecimal ticketPrice;

    @NotNull(message = "are seats numerated cannot be null")
    private Boolean numeratedSeats;

    private Date date;

    private Integer capacity;

    @NotNull(message = "sector id cannot be null")
    private Long sectorId;

    private Long eventId;

    public EventSectorDTO(EventSector eventSector) {
        this.id = eventSector.getId();
        this.ticketPrice = eventSector.getTicketPrice();
        this.numeratedSeats = eventSector.getNumeratedSeats();
        this.date = eventSector.getDate();
        this.sectorId = eventSector.getSector().getId();
        this.eventId = eventSector.getEvent().getId();
        this.capacity = eventSector.getCapacity();
    }

    public EventSector convertToEntity(){
        EventSector eventSector = new EventSector();
        eventSector.setId(this.id);
        eventSector.setDate(this.date);
        eventSector.setNumeratedSeats(this.numeratedSeats);
        eventSector.setTicketPrice(this.ticketPrice);
        eventSector.setCapacity(this.capacity);
        Event event = new Event();
        event.setId(this.eventId);
        eventSector.setEvent(event);
        Sector sector = new Sector();
        sector.setId(this.sectorId);
        eventSector.setSector(sector);

        return eventSector;
    }

}

