package com.siit.ticketist.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a venue where events are hosted
 */
@Entity
@Table(name="Venues")
@Getter @Setter
public class Venue {

   /**
    * Unique identifier of the venue
    */
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   /**
    * Name of the venue
    */
   @Column(nullable = false)
   private String name;

   /**
    * Holds the information whether the venue is currently active or not.
    * In case the venue is not active, it cannot accept hosting new events.
    * Events that have been registered before setting the Venue to inactive are left untouched.
    */
   @Column(nullable = false, columnDefinition = "boolean default true")
   private Boolean isActive = true;

   /**
    * Street where the venue is located at
    */
   @Column(nullable = false)
   private String street;

   /**
    * City where the venue is located at
    */
   @Column(nullable = false)
   private String city;

   /**
    * Latitude of the venue
    */
   @Column(nullable = false)
   private Double latitude;

   /**
    * Longitude of the venue
    */
   @Column(nullable = false)
   private Double longitude;

   /**
    * Collection of sectors the venue contains
    */
   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   @JoinColumn(name = "venue_id")
   private Set<Sector> sectors;

   /**
    * Collection of events that are organised at this venue
    */
   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "venue")
   @JsonBackReference(value = "venue-events")
   private Set<Event> events;

   public Venue() {
      this.events = new HashSet<>();
      this.sectors = new HashSet<>();
   }
}