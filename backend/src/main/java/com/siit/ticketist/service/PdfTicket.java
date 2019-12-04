package com.siit.ticketist.service;

import com.siit.ticketist.model.Ticket;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class PdfTicket {

    public PdfTicket(Ticket ticket) {
        this.eventName = ticket.getEvent().getName();
        this.venueName = ticket.getEvent().getVenue().getName();
        this.eventSector = ticket.getEventSector().getSector().getName();
        this.row = ticket.getNumberRow().toString();
        this.column = ticket.getNumberColumn().toString();
        this.date = ticket.getEvent().getStartDate().toString();
        this.price = ticket.getPrice().toString();
    }

    String eventName;
    String venueName;
    String eventSector;
    String row;
    String column;
    String date;
    String price;
}