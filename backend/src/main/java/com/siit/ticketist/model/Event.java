package com.siit.ticketist.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Events")
@Getter @Setter
public class Event {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false)
   private String name;

   @Enumerated(EnumType.STRING)
   private Category category;

   @Column(nullable = false)
   private Date startDate;

   @Column(nullable = false)
   private Date endDate;

   @Column(nullable = false)
   private Date reservationDeadline;

   @Column(nullable = false)
   private Integer reservationLimit;

   @Column(nullable = false)
   private String description;

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   @JoinColumn(name = "event_id")
   private Set<MediaFile> mediaFiles;

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "event")
   @JsonBackReference(value = "event-eventSectors")
   private Set<EventSector> eventSectors;

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "event")
   @JsonBackReference(value = "event-tickets")
   private Set<Ticket> tickets;

   @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   @JoinColumn(name = "venue_id")
   private Venue venue;

   public Event() {
      mediaFiles = new HashSet<>();
      eventSectors = new HashSet<>();
      tickets = new HashSet<>();
   }
}