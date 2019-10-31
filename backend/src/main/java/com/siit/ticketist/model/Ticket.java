package com.siit.ticketist.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.*;

@Getter @Setter @NoArgsConstructor
public class Ticket {
   private Long id;
   private BigDecimal price;
   private Seat seat;
   private Event event;
}