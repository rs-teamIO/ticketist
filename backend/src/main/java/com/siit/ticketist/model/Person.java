package com.siit.ticketist.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter @Setter @NoArgsConstructor
public abstract class Person {
   private Long id;
   private String firstName;
   private String lastName;
   private String email;
   private String password;
}