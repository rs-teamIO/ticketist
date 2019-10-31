package com.siit.ticketist.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter @Setter @NoArgsConstructor
public class Reservation {
   private Long id;
   private Boolean isCancelled;
   private Set<Seat> seats;
   private User user;
}