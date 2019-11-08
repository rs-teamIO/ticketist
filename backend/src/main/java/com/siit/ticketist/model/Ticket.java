package com.siit.ticketist.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Tickets")
@Getter @Setter @NoArgsConstructor
public class Ticket {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false)
   private Integer numberRow;

   @Column(nullable = false)
   private Integer numberColumn;

   @Column(nullable = false)
   private BigDecimal price;

   @Column(nullable = false)
   private Boolean isPaid;

   @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
   @JoinColumn(name = "event_sector_id")
   private EventSector eventSector;

   @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
   @JoinColumn(name = "event_id")
   private Event event;

   @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
   @JoinColumn(name = "user_id")
   private RegisteredUser user;
}