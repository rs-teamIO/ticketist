package com.siit.ticketist.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "EventSectors")
@Getter @Setter
public class EventSector {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false)
   private BigDecimal ticketPrice;

   @Column(nullable = false)
   private Boolean numeratedSeats;

   @Column(nullable = false)
   private Date date;

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "eventSector")
   @JsonBackReference(value = "eventSector-seats")
   private Set<Seat> seats;

   @OneToOne(cascade = { CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE}, fetch = FetchType.LAZY)
   @JoinColumn(name = "sector_id")
   private Sector sector;

   @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   @JoinColumn(name = "event_id")
   private Event event;

   public EventSector() {
      seats = new HashSet<>();
   }
}