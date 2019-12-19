package com.siit.ticketist.controller;

import com.siit.ticketist.dto.EventDTO;
import com.siit.ticketist.dto.EventUpdateDTO;
import com.siit.ticketist.dto.SearchDTO;
import com.siit.ticketist.model.Event;
import com.siit.ticketist.model.MediaFile;
import com.siit.ticketist.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
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

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
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
    //@PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
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
    //@PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(value = "{eventId}/media", consumes = {"application/octet-stream", "multipart/form-data"})
    public ResponseEntity<EventDTO> addMediaFiles(@PathVariable("eventId") Long eventId, @RequestPart("mediaFiles") MultipartFile[] mediaFiles) {
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
    @GetMapping(value = "{eventId}/media/{fileName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody byte[] getMediaFile(@PathVariable("eventId") Long eventId, @PathVariable("fileName") String fileName) {
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
    public ResponseEntity deleteMediaFile(@PathVariable("eventId") Long eventId, @PathVariable("fileName") String fileName) {
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
    @PostMapping(value="{eventId}")
    public ResponseEntity<EventDTO> updateBasicInformation(@PathVariable("eventId") Long eventId, @Valid @RequestBody EventUpdateDTO dto) {
        Event updatedEvent = eventService.updateBasicInformation(eventId, dto.getName(), dto.getCategory(), dto.getReservationDeadline(), dto.getReservationLimit(), dto.getDescription());
        return new ResponseEntity<>(new EventDTO(updatedEvent), HttpStatus.OK);
    }

    /**
     * POST /api/events/search
     * Performs a search based on given criteria
     *
     * @param dto DTO containing search parameters
     * @return {@link ResponseEntity} containing the events that satisfy search criteria
     */
    @PostMapping(value = "/search")
    public ResponseEntity search(@RequestBody SearchDTO dto) {
        List<Event> events = eventService.search(dto.getEventName(), dto.getCategory(), dto.getVenueName(), dto.getStartDate(), dto.getEndDate());
        List<EventDTO> eventDTOs = events.stream()
                .map(EventDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(eventDTOs, HttpStatus.OK);
    }
}
