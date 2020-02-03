package com.siit.ticketist.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Reservations")
@Getter @Setter @AllArgsConstructor
public class Reservation {

    /**
     * Unique identifier of the reservation
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Holds a reference to the related {@link Event} the tickets are for
     */
    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    /**
     * Holds a reference to the {@link RegisteredUser} who reserved the tickets
     */
    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private RegisteredUser user;

    /**
     * Collection of tickets the user has reserved
     */
    @OneToMany(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY, mappedBy = "reservation")
    @JsonBackReference(value = "reservations-tickets")
    private Set<Ticket> tickets;

    public Reservation() {
        this.tickets = new HashSet<>();
    }
}
