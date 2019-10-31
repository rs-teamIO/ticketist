package com.siit.ticketist.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter @Setter @NoArgsConstructor
public class Seat {
   private Long id;
   private Integer row;
   private Integer column;
   private Boolean isAvailable;
   private EventSector eventSector;
}