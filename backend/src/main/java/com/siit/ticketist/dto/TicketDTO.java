package com.siit.ticketist.dto;


import com.siit.ticketist.model.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Getter @Setter @NoArgsConstructor
public class TicketDTO {
    private Long id;

    @NotNull(message = "number row cannot be null")
    private Integer numberRow;

    @NotNull(message = "number column cannot be null")
    private Integer numberColumn;

    private BigDecimal price;

    private TicketStatus status;

    @NotBlank(message = "event sector id cannot be blank")
    private Long eventSectorId;

    @NotBlank(message = "event id cannot be blank")
    private Long eventId;

    private String eventName;
    private String venueName;
    private String sectorName;
    private Date date;

    private Long userId;

    public TicketDTO(Ticket ticket){
        this.id = ticket.getId();
        this.status = ticket.getStatus();
        this.price = ticket.getPrice();
        this.numberColumn = ticket.getNumberColumn();
        this.numberRow = ticket.getNumberRow();
        this.eventId = ticket.getEvent().getId();
        if (ticket.getUser() == null) {
            this.userId = null;
        } else {
            this.userId = ticket.getUser().getId();
        }
        this.eventSectorId = ticket.getEventSector().getId();
        this.eventName = ticket.getEvent().getName();
        this.venueName = ticket.getEvent().getVenue().getName();
        this.sectorName = ticket.getEventSector().getSector().getName();
        this.date = ticket.getEventSector().getDate();
    }

    public Ticket convertToEntity(){
        Ticket ticket = new Ticket();
        ticket.setId(this.id);
        ticket.setStatus(this.status);
        ticket.setPrice(this.price);
        ticket.setNumberRow(this.numberRow);
        ticket.setNumberColumn(this.numberColumn);
        EventSector eventSector = new EventSector();
        eventSector.setId(this.eventSectorId);
        ticket.setEventSector(eventSector);
        Event event = new Event();
        event.setId(this.eventId);
        ticket.setEvent(event);
        RegisteredUser user = new RegisteredUser();
        user.setId(this.userId);
        ticket.setUser(user);

        return ticket;
    }
}
