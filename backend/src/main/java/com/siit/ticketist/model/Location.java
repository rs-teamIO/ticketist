package com.siit.ticketist.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "Locations")
@Getter @Setter @NoArgsConstructor
public class Location {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false)
   private String street;

   @Column(nullable = false)
   private String city;

   @Column(nullable = false)
   private Double latitude;

   @Column(nullable = false)
   private Double longitude;
}