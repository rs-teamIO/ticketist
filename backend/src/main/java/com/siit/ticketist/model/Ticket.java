package com.siit.ticketist.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Represents a ticket that users can reserve and buy.
 */
@Entity
@Table(name = "Tickets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Ticket {

   /**
    * Unique identifier of the ticket
    */
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   /**
    * Row of the seat associated to the ticket
    */
   @Column(nullable = false)
   private Integer numberRow;

   /**
    * Column of the seat associated to the ticket
    */
   @Column(nullable = false)
   private Integer numberColumn;

   /**
    * Ticket price
    */
   @Column(nullable = false)
   private BigDecimal price;

   /**
    * Ticket status
    */
   @Column(nullable = false)
   @Enumerated
   private TicketStatus status;

   @Version
   private Long version;

   /**
    * Holds a reference to the related {@link EventSector}
    */
   @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
   @JoinColumn(name = "event_sector_id")
   private EventSector eventSector;

   /**
    * Holds a reference to the related {@link Event} the ticket is for
    */
   @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
   @JoinColumn(name = "event_id")
   private Event event;

   /**
    * Holds a reference to the {@link RegisteredUser} who bought/reserved the ticket
    */
   @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
   @JoinColumn(name = "user_id")
   private RegisteredUser user;

   /**
    * Holds a reference to the {@link TicketGroup}
    */
   @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
   @JoinColumn(name = "ticket_group_id")
   private TicketGroup ticketGroup;
}