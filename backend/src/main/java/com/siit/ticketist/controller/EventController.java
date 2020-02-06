package com.siit.ticketist.controller;

import com.siit.ticketist.dto.EventDTO;
import com.siit.ticketist.dto.EventPageDTO;
import com.siit.ticketist.dto.EventUpdateDTO;
import com.siit.ticketist.dto.SearchDTO;
import com.siit.ticketist.model.Event;
import com.siit.ticketist.model.EventSector;
import com.siit.ticketist.model.MediaFile;
import com.siit.ticketist.service.EventSectorService;
import com.siit.ticketist.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Events REST controller.
 */
@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final EventSectorService eventSectorService;

    @Autowired
    public EventController(EventService eventService, EventSectorService eventSectorService) {
        this.eventService = eventService;
        this.eventSectorService = eventSectorService;
    }

    /**
     * GET /api/events
     * Returns all events
     *
     * @return {@link ResponseEntity} containing HttpStatus and a list of events
     */
    @GetMapping
    public ResponseEntity<List<EventDTO>> getEvents() {
        List<EventDTO> events = new ArrayList<>();
        eventService.findAll().stream()
                .map(EventDTO::new)
                .forEachOrdered(events::add);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    /**
     * GET /api/events
     * Returns event pages
     *
     * @return {@link ResponseEntity} containing HttpStatus and a page with a list of events
     */
    @GetMapping(value="/paged")
    public ResponseEntity<EventPageDTO> getEvents(Pageable pageable) {
        List<EventDTO> events = new ArrayList<>();
        eventService.findAll(pageable).stream()
                .map(EventDTO::new)
                .forEachOrdered(events::add);
        return new ResponseEntity<>(new EventPageDTO(events, eventService.getTotalNumberOfActiveEvents()), HttpStatus.OK);
    }

    /**
     * GET /api/events/{id}
     * Returns a {@link Event} with the requested ID
     *
     * @param id ID of the {@link Event}
     * @return {@link ResponseEntity} containing HttpStatus and content
     */
    @GetMapping(value="{id}")
    public ResponseEntity<EventDTO> getEvent(@PathVariable("id") Long id) {
        Event event = eventService.findOne(id);
        return new ResponseEntity<>(new EventDTO(event), HttpStatus.OK);
    }

    /**
     *  POST /api/events
     *  Creates a new {@link Event}.
     *
     * @param eventDTO DTO containing event info.
     * @return {@link ResponseEntity} containing the info about the created Event
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EventDTO> createEvent(@Valid @RequestBody EventDTO eventDTO) {
        Event eventToBeCreated = eventDTO.convertToEntity();
        Event event = eventService.save(eventToBeCreated);
        return new ResponseEntity<>(new EventDTO(event), HttpStatus.OK);
    }

    /**
     *  POST /api/events/1/media
     *  Adds media files to the {@link Event} instance with specified ID.
     *
     * @param eventId ID of the event to add media files to
     * @param mediaFiles List of media files
     * @return {@link ResponseEntity} containing HttpStatus and content
     */
    @PostMapping(value = "{eventId}/media", consumes = {"application/octet-stream", "multipart/form-data"})
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EventDTO> addMediaFiles(@PathVariable("eventId") Long eventId,
                                                  @RequestPart("mediaFiles") MultipartFile[] mediaFiles) {
        Event event = eventService.addMediaFiles(eventId, mediaFiles);
        return new ResponseEntity<>(new EventDTO(event), HttpStatus.OK);
    }

    /**
     * GET /api/events/1/media
     * Returns all {@link MediaFile} instances associated with the requested {@link Event}
     *
     * @param eventId ID of the event the media files are requested for
     * @return {@link ResponseEntity} containing HttpStatus and content
     */
    @GetMapping(value = "{eventId}/media")
    public ResponseEntity<Set<MediaFile>> getMediaFiles(@PathVariable("eventId") Long eventId) {
        Set<MediaFile> mediaFiles = eventService.getMediaFiles(eventId);
        return new ResponseEntity<>(mediaFiles, HttpStatus.OK);
    }

    /**
     * GET /api/events/1/media/test.jpg
     * Returns the byte representation of the requested file.
     *
     * @param eventId ID of the event the media files are requested for
     * @param fileName Name of the requested file
     * @return Byte array representation of the requested file
     */
    @GetMapping(value = "{eventId}/media/{fileName}", produces = { MediaType.IMAGE_JPEG_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE })
    public @ResponseBody byte[] getMediaFile(@PathVariable("eventId") Long eventId,
                                             @PathVariable("fileName") String fileName) {
        return eventService.getMediaFile(eventId, fileName);
    }

    /**
     * DELETE /api/events/1/media/test.jpg
     * Deletes the requested file.
     *
     * @param eventId ID of the event the media file is bound to
     * @param fileName Name of the file to be deleted
     * @return {@link ResponseEntity} containing HttpStatus and content
     */
    @DeleteMapping(value = "{eventId}/media/{fileName}")
    public ResponseEntity deleteMediaFile(@PathVariable("eventId") Long eventId,
                                          @PathVariable("fileName") String fileName) {
        eventService.deleteMediaFile(eventId, fileName);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * POST /api/events/1
     * Updates basic information of the {@link Event} with given ID
     *
     * @param eventId Unique identifier of the event
     * @param dto {@link EventUpdateDTO} containing updated information
     * @return Event with updated information
     */
    @PutMapping(value="{eventId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EventDTO> updateBasicInformation(@PathVariable("eventId") Long eventId,
                                                           @Valid @RequestBody EventUpdateDTO dto) {
        Event updatedEvent = this.eventService.updateBasicInformation(eventId, dto.getName(), dto.getCategory(),
                dto.getReservationDeadline(), dto.getReservationLimit(), dto.getDescription());
        return new ResponseEntity<>(new EventDTO(updatedEvent), HttpStatus.OK);
    }

    /**
     * PUT /api/events/1/event-sector/1/ticket-price
     * Changes the ticket price of {@link EventSector} with given ID.
     * The new ticket price affects only the tickets that are purchased after the change.
     *
     * @param eventId ID of the {@link Event} the {@link EventSector} is referenced to
     * @param eventSectorId Unique identifier of the {@link EventSector}
     * @param newTicketPrice new ticket price
     * @return Event with updated information
     */
    @PutMapping(value = "{eventId}/event-sector/{eventSectorId}/ticket-price")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EventDTO> changeEventSectorTicketPrice(@PathVariable("eventId") Long eventId,
                                                                 @PathVariable("eventSectorId") Long eventSectorId,
                                                                 @RequestBody @Positive BigDecimal newTicketPrice) {
        EventSector updatedEventSector = this.eventSectorService.updateTicketPrice(eventId, eventSectorId, newTicketPrice);
        return new ResponseEntity<>(new EventDTO(updatedEventSector.getEvent()), HttpStatus.OK);
    }

    /**
     * PUT /api/events/1/event-sector/1/status
     * Changes the status of {@link EventSector} with given ID
     *
     * @param eventId ID of the {@link Event} the {@link EventSector} is referenced to
     * @param eventSectorId Unique identifier of the {@link EventSector}
     * @param newStatus new status
     * @return Event with updated information
     */
    @PutMapping(value = "{eventId}/event-sector/{eventSectorId}/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EventDTO> changeEventSectorStatus(@PathVariable("eventId") Long eventId,
                                                            @PathVariable("eventSectorId") Long eventSectorId,
                                                            @RequestBody Boolean newStatus) {
        EventSector updatedEventSector = this.eventSectorService.updateStatus(eventId, eventSectorId, newStatus);
        return new ResponseEntity<>(new EventDTO(updatedEventSector.getEvent()), HttpStatus.OK);
    }

    /**
     * PUT /api/events/1/event-sector/1/capacity
     * Changes the capacity of {@link EventSector} with given ID
     *
     * @param eventId ID of the {@link Event} the {@link EventSector} is referenced to
     * @param eventSectorId Unique identifier of the {@link EventSector}
     * @param newCapacity new capacity
     * @return Event with updated information
     */
    @PutMapping(value = "{eventId}/event-sector/{eventSectorId}/capacity")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EventDTO> changeEventSectorCapacity(@PathVariable("eventId") Long eventId,
                                                              @PathVariable("eventSectorId") Long eventSectorId,
                                                              @RequestBody @Positive Integer newCapacity) {
        EventSector updatedEventSector = this.eventSectorService.updateCapacity(eventId, eventSectorId, newCapacity);
        return new ResponseEntity<>(new EventDTO(updatedEventSector.getEvent()), HttpStatus.OK);
    }

    /**
     * POST /api/events/search
     * Performs a search based on given criteria
     *
     * @param dto DTO containing search parameters
     * @return {@link ResponseEntity} containing the events that satisfy search criteria
     */
    @PostMapping(value = "/search")
        public ResponseEntity search(@RequestBody SearchDTO dto, Pageable pageable) {
        Page<Event> eventsPage = eventService.search(dto.getEventName(), dto.getCategory(), dto.getVenueName(), dto.getStartDate(), dto.getEndDate(), pageable);

        List<EventDTO> eventDTOs = eventsPage.getContent().stream()
                .map(EventDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(new EventPageDTO(eventDTOs, eventsPage.getTotalElements()), HttpStatus.OK);
    }

    /**
     * GET /api/events/1
     *
     * @param eventId ID of the event to be cancelled
     * @return {@link ResponseEntity} containing HttpStatus and content
     */
    @GetMapping(value = "cancel/{eventId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity cancelEvent(@PathVariable("eventId") Long eventId) throws MessagingException {
        Event event = eventService.cancelEvent(eventId);
        return new ResponseEntity(new EventDTO(event), HttpStatus.OK);
    }
}
