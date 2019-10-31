package com.siit.ticketist.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter @Setter @NoArgsConstructor
public class Sector {
   private Long id;
   private String name;
   private Integer rows;
   private Integer columns;
   private Integer capacity;
   private Integer startRow;
   private Integer startColumn;
}