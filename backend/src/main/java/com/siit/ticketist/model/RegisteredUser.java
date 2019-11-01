package com.siit.ticketist.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter @Setter
public class RegisteredUser extends User {

   @Column
   private String phone;

   @Column(nullable = false)
   private Boolean isVerified;

   @Column(nullable = false)
   private String verificationCode;

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy="user")
   @JsonBackReference(value = "registeredUser-reservations")
   private Set<Reservation> reservations;

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   @JoinColumn(name = "user_id")
   private Set<Ticket> tickets;

   public RegisteredUser() {
      verificationCode = UUID.randomUUID().toString();
      isVerified = false;
      reservations = new HashSet<Reservation>();
      tickets = new HashSet<Ticket>();
   }
}