package com.siit.ticketist.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor
public class RegisteredUser extends User {

   @Column
   private String phone;

   @Column(nullable = false)
   private Boolean isVerified;

   @Column
   private String verificationCode;

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy="user")
   @JsonBackReference(value = "registeredUser-tickets")
   private Set<Ticket> tickets;

   public RegisteredUser(String username, String password, String email, String firstName, String lastName) {
      super(username, password, email, firstName, lastName);
   }
}