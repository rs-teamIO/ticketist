package com.siit.ticketist.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "TicketGroups")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TicketGroup {

    /**
     * Unique identifier of the ticket group
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * TicketGroup type
     */
    @Column(nullable = false)
    @Enumerated
    private TicketGroupType type;

    /**
     * Holds a reference to the related {@link Event} the tickets are for
     */
    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    /**
     * Holds a reference to the {@link RegisteredUser} who bought/reserved the tickets
     */
    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private RegisteredUser user;

    /**
     * Collection of tickets the user has reserved or bought
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "ticket")
    @JsonBackReference(value = "ticketGroup-tickets")
    private Set<Ticket> tickets;
}
