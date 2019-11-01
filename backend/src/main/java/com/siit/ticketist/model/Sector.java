package com.siit.ticketist.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "Sectors")
@Getter @Setter @NoArgsConstructor
public class Sector {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false)
   private String name;

   @Column(nullable = false)
   private Integer rowsCount;

   @Column(nullable = false)
   private Integer columnsCount;

   @Column(nullable = false)
   private Integer capacity;

   @Column(nullable = false)
   private Integer startRow;

   @Column(nullable = false)
   private Integer startColumn;
}