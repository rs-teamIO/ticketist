package com.siit.ticketist.dto;

import com.siit.ticketist.model.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter @NoArgsConstructor
public class EventDTO {

    private Long id;

    @NotBlank(message = "name cannot be blank")
    private String name;

    @NotNull(message = "venue id cannot be null")
    private Long venueId;

    @NotNull(message = "category cannot be null")
    private Category category;

    @NotNull(message = "start date cannot be null")
    private Date startDate;

    @NotNull(message = "end date cannot be null")
    private Date endDate;

    @NotNull(message = "reservation deadline cannot be null")
    private Date reservationDeadline;

    @NotNull(message = "reservation limit cannot be null")
    private Integer reservationLimit;

    @NotBlank(message = "description cannot be blank")
    private String description;

    @NotNull(message = "event sectors cannot be null")
    private Set<EventSectorDTO> eventSectors;

    @NotNull(message = "media files cannot be null")
    private Set<MediaFile> mediaFiles;

    public EventDTO(Event event) {
        this.id = event.getId();
        this.name = event.getName();
        this.category = event.getCategory();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.reservationDeadline = event.getReservationDeadline();
        this.reservationLimit = event.getReservationLimit();
        this.description = event.getDescription();
        this.mediaFiles = event.getMediaFiles();
        this.venueId = event.getVenue().getId();
        this.eventSectors = new HashSet<>();
        event.getEventSectors().stream()
                .map(EventSectorDTO::new)
                .forEach(this.eventSectors::add);
    }

    public Event convertToEntity() {

        Event event = new Event();
        event.setId(this.id);
        event.setName(this.name);
        event.setCategory(this.category);
        event.setStartDate(this.startDate);
        event.setEndDate(this.endDate);
        event.setReservationDeadline(this.reservationDeadline);
        event.setReservationLimit(this.reservationLimit);
        event.setDescription(this.description);
        event.setMediaFiles(this.mediaFiles);

        Venue venue = new Venue();
        venue.setId(this.venueId);
        event.setVenue(venue);

        this.eventSectors.stream()
                .map(EventSectorDTO::convertToEntity)
                .forEach(event.getEventSectors()::add);

        return event;
    }

}
