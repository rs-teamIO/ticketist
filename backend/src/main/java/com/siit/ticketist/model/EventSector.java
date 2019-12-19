package com.siit.ticketist.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a part of the event (single day).
 */
@Entity
@Table(name = "EventSectors")
@Getter @Setter @AllArgsConstructor
public class EventSector {

   /**
    * Unique identifier of the event sector
    */
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   /**
    * True if the event sector is active, otherwise false.
    */
   @Column(nullable = false)
   private Boolean isActive = true;

   /**
    * Ticket price for each {@link Ticket} in this sector for the related {@link Event}
    */
   @Column(nullable = false)
   private BigDecimal ticketPrice;

   /**
    * Represents if the seats are enumerated for this event or not
    */
   @Column(nullable = false)
   private Boolean numeratedSeats;

   /**
    * The start date of the related {@link Event} for this event sector
    */
   @Column(nullable = false)
   private Date date;

   /**
    * Represents the capacity of the event sector. This attribute
    * is only populated in cases where the numeratedSeats property
    * is set to false. The value of this attribute cannot be bigger than
    * the value of the maxCapacity attribute in the related {@link Event} object.
    */
   @Column(nullable = false)
   private Integer capacity;

   /**
    * Collection of tickets available for this sector
    */
   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "eventSector")
   @JsonBackReference(value = "eventSector-tickets")
   private Set<Ticket> tickets;

   /**
    * Holds a reference to the originating {@link Sector} in the {@link Venue}
    */
   @OneToOne(cascade = { CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE}, fetch = FetchType.LAZY)
   @JoinColumn(name = "sector_id")
   private Sector sector;

   /**
    * Holds a reference to the related {@link Event}
    */
   @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   @JoinColumn(name = "event_id")
   private Event event;

   public EventSector() {
      this.tickets = new HashSet<>();
   }
}