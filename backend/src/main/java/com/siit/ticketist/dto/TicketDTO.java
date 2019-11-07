package com.siit.ticketist.dto;


import com.siit.ticketist.model.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor
public class TicketDTO {
    private Long id;

    @NotNull(message = "number row cannot be null")
    private Integer numberRow;

    @NotNull(message = "number column cannot be null")
    private Integer numberColumn;

    @NotNull(message = "price cannot be null")
    private BigDecimal price;

    private Boolean isPaid;

    @NotBlank(message = "event sector id cannot be blank")
    private Long eventSectorId;

    @NotBlank(message = "event id cannot be blank")
    private Long eventId;

    private Long userId;

    public TicketDTO(Ticket ticket){
        this.id = ticket.getId();
        this.isPaid = ticket.getIsPaid();
        this.price = ticket.getPrice();
        this.numberColumn = ticket.getNumberColumn();
        this.numberRow = ticket.getNumberColumn();
        this.eventId = ticket.getEvent().getId();
        this.userId = ticket.getUser().getId();
        this.eventSectorId = ticket.getEventSector().getId();
    }

    public Ticket convertToEntity(){
        Ticket ticket = new Ticket();
        ticket.setId(this.id);
        ticket.setIsPaid(this.isPaid);
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
