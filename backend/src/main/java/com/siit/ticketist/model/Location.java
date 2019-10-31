package com.siit.ticketist.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter @Setter @NoArgsConstructor
public class Location {
   private Long id;
   private String street;
   private String city;
   private Double latitude;
   private Double longitude;
}