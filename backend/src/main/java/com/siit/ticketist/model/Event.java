package com.siit.ticketist.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter @Setter @NoArgsConstructor
public class Event {
   private Long id;
   private String name;
   private Category category;
   private Date startDate;
   private Date endDate;
   private Date reservationDeadline;
   private Integer reservationLimit;
   private String description;
   private Set<MediaFile> mediaFiles;
   private Set<EventSector> eventSectors;
   private Set<Ticket> tickets;
   private Set<Reservation> reservations;
   private Venue venue;
}