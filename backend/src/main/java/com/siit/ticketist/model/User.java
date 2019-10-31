package com.siit.ticketist.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter @Setter @NoArgsConstructor
public class User extends Person {
   private String phone;
   private Boolean isVerified;
   private String verificationCode;
   private Set<Reservation> reservations;
   private Set<Ticket> tickets;
}