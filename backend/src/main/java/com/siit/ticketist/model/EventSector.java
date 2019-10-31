package com.siit.ticketist.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.*;

@Getter @Setter @NoArgsConstructor
public class EventSector {
   private Long id;
   private BigDecimal ticketPrice;
   private Boolean numeratedSeats;
   private Date date;
   private Set<Seat> seats;
   private Sector sector;
   private Event event;
}