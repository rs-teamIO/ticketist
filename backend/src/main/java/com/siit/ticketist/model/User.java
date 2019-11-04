package com.siit.ticketist.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "Users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter @Setter @NoArgsConstructor
public class User {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   protected Long id;

   @Column(nullable = false)
   protected String username;

   @Column(nullable = false)
   protected String password;

   @Column(nullable = false, unique = true)
   @Size(min = 6, max = 50)
   protected String email;

   @Column(nullable = false)
   protected String firstName;

   @Column(nullable = false)
   protected String lastName;

   @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
   @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
   @Column(name = "authority", nullable = false)
   @Enumerated(EnumType.STRING)
   protected Collection<Role> authorities = new ArrayList<>();

   public User(String username, String password, String email, String firstName, String lastName) {
      this.username = username;
      this.password = password;
      this.email = email;
      this.firstName = firstName;
      this.lastName = lastName;
      this.authorities = new ArrayList<>();
   }

   public boolean hasAuthority(Role role) {
      return authorities.contains(role);
   }
}