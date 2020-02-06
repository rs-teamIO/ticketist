package com.siit.ticketist.dto;

import com.siit.ticketist.model.Reservation;
import com.siit.ticketist.model.Ticket;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter @NoArgsConstructor
public class ReservationDTO {
    private Long id;

    @NotBlank(message = "event name cannot be blank")
    private String eventName;

    @NotBlank(message = "venue name cannot be blank")
    private String venueName;

    @NotNull(message = "price cannot be null")
    private Double price;

    @NotNull(message = "size cannot be null")
    private Integer ticketCount;

//    @NotNull(message = "event id cannot be null")
    private Long eventId;

    public ReservationDTO(Reservation reservation) {
        this.id = reservation.getId();
        this.eventName = reservation.getEvent().getName();
        this.venueName = reservation.getEvent().getVenue().getName();
        this.price = 0.0;
        for(Ticket ticket : reservation.getTickets()) {
            this.price += ticket.getPrice().doubleValue();
        }
        this.ticketCount = reservation.getTickets().size();
        this.eventId = reservation.getEvent().getId();
    }

}
