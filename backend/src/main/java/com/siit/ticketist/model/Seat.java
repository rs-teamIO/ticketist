package com.siit.ticketist.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "Seats")
@Getter @Setter @NoArgsConstructor
public class Seat {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false)
   private Integer numberRow;

   @Column(nullable = false)
   private Integer numberColumn;

   @Column(nullable = false)
   private Boolean isAvailable;

   @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   @JoinColumn(name = "eventSector_id")
   private EventSector eventSector;
}