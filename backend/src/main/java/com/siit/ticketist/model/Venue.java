package com.siit.ticketist.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="Venues")
@Getter @Setter
public class Venue {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false)
   private String name;

   @Column(nullable = false)
   private Boolean isActive;

   @Column(nullable = false)
   private String street;

   @Column(nullable = false)
   private String city;

   @Column(nullable = false)
   private Double latitude;

   @Column(nullable = false)
   private Double longitude;

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   @JoinColumn(name = "venue_id")
   private Set<Sector> sectors;

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "venue")
   @JsonBackReference(value = "venue-events")
   private Set<Event> events;

   public Venue() {
      this.events = new HashSet<>();
      this.sectors = new HashSet<>();
   }
}