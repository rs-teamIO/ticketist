package com.siit.ticketist.dto;

import com.siit.ticketist.model.Ticket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PdfTicket {

    String ticketId;
    String eventName;
    String venueName;
    String eventSector;
    String row;
    String column;
    String date;
    String price;
    String base64Image;

    public PdfTicket(Ticket ticket) {
        this.ticketId = ticket.getId().toString();
        this.eventName = ticket.getEvent().getName();
        this.venueName = ticket.getEvent().getVenue().getName();
        this.eventSector = ticket.getEventSector().getSector().getName();
        this.row = ticket.getNumberRow().toString();
        this.column = ticket.getNumberColumn().toString();
        this.date = ticket.getEvent().getStartDate().toString();
        this.price = ticket.getPrice().toString();
    }
}