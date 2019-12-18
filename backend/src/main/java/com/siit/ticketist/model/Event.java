package com.siit.ticketist.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents an event hosted on specific dates at a given {@link Venue}
 */
@Entity
@Table(name = "Events")
@Getter @Setter
public class Event {

   /**
    * Unique identifier of the event
    */
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   /**
    * Name of the event
    */
   @Column(nullable = false)
   private String name;

   /**
    * Holds the information whether the event has been cancelled or not.
    * In case the event is cancelled, users cannot buy any tickets for that event.
    */
   @Column(nullable = false, columnDefinition = "boolean default false")
   private Boolean isCancelled = false;

   /**
    * Event {@link Category}
    */
   @Enumerated(EnumType.STRING)
   private Category category;

   /**
    * The start date of the event.
    * (the start date of the FIRST event part)
    */
   @Column(nullable = false)
   private Date startDate;

   /**
    * The end date of the event
    * (the start date of the LAST event part)
    */
   @Column(nullable = false)
   private Date endDate;

   /**
    * Represents a deadline after which tickets cannot be
    * reserved anymore. After the deadline passes, tickets can
    * only be bought directly.
    */
   @Column(nullable = false)
   private Date reservationDeadline;

   /**
    * Represents the number of tickets a single user
    * can purchase for the event.
    */
   @Column(nullable = false)
   private Integer reservationLimit;

   /**
    * Detailed description of the event
    */
   @Column(nullable = false)
   private String description;

   /**
    * Collection of media files (photos, videos, etc.)
    * related to the event
    */
   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   @JoinColumn(name = "event_id")
   private Set<MediaFile> mediaFiles;

   /**
    * Collection of {@link EventSector} objects related to the event.
    * For each part of an event, there's a corresponding {@link EventSector}
    */
   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "event")
   @JsonBackReference(value = "event-eventSectors")
   private Set<EventSector> eventSectors;

   /**
    * Collection of {@link Ticket} objects available for the event.
    */
   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "event")
   @JsonBackReference(value = "event-tickets")
   private Set<Ticket> tickets;

   /**
    * The {@link Venue} the event is organised at
    */
   @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
   @JoinColumn(name = "venue_id")
   private Venue venue;

   public Event() {
      this.mediaFiles = new HashSet<>();
      this.eventSectors = new HashSet<>();
      this.tickets = new HashSet<>();
   }
}