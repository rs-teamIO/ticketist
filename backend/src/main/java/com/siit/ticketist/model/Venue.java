package com.siit.ticketist.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter @Setter @NoArgsConstructor
public class Venue {
   private Long id;
   private String name;
   private Boolean isActive;
   private Set<Sector> sectors;
   private Location location;
   private Set<Event> events;
}