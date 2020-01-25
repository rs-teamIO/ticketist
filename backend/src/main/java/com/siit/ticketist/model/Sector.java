package com.siit.ticketist.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Represents a sector inside the {@link Venue}
 */
@Entity
@Table(name = "Sectors")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Sector {

   /**
    * Unique identifier of the sector
    */
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   /**
    * Sector's name
    */
   @Column(nullable = false)
   private String name;

   /**
    * Number of rows in the sector
    */
   @Column(nullable = false)
   private Integer rowsCount;

   /**
    * Number of columns in the sector
    */
   @Column(nullable = false)
   private Integer columnsCount;

   /**
    * The maximum capacity of the sector
    */
   @Column(nullable = false)
   private Integer maxCapacity;

   /**
    * The starting row of the sector on the UI layout
    */
   @Column(nullable = false)
   private Integer startRow;

   /**
    * The starting column of the sector on the UI layout
    */
   @Column(nullable = false)
   private Integer startColumn;
}