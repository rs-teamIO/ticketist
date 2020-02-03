package com.siit.ticketist.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a registered user in the system.
 * A registered user can reserve and buy tickets, etc.
 */
@Entity
@DiscriminatorValue("REGISTERED_USER")
@Getter @Setter
public class RegisteredUser extends User {

   /**
    * Phone number of the user
    */
   @Column
   private String phone;

   /**
    * Holds the information whether the user is verified or not
    */
   @Column(nullable = false, columnDefinition = "boolean default false")
   private Boolean isVerified = false;

   /**
    * Verification code for the user's account
    */
   @Column
   private String verificationCode;

   /**
    * Collection of tickets the user has reserved or bought
    */
   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
   @JsonBackReference(value = "registeredUser-tickets")
   private Set<Ticket> tickets;

   /**
    * Collection of user's reservations
    */
   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
   @JsonBackReference(value = "registeredUser-reservations")
   private Set<Reservation> reservations;

   public RegisteredUser() {
      this.tickets = new HashSet<>();
      this.reservations = new HashSet<>();
   }

   public RegisteredUser(String username, String password, String email, String firstName, String lastName) {
      super(username, password, email, firstName, lastName);
   }
}