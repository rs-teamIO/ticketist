package com.siit.ticketist.controller;

import com.siit.ticketist.dto.EventDTO;
import com.siit.ticketist.dto.SearchDTO;
import com.siit.ticketist.model.Event;
import com.siit.ticketist.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
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
    public ResponseEntity<List<EventDTO>> getEvents(){
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
    public ResponseEntity<Object> getEvent(@PathVariable("id") Long id) {
        Event event = eventService.findOne(id);
        return new ResponseEntity<>(new EventDTO(event), HttpStatus.OK);
    }

    /**
     *  POST /api/events
     *  Creates a new {@link Event}.
     *
     * @param eventDTO DTO containing event info.
     * @param mediaFiles List of media files
     * @return {@link ResponseEntity} containing the info about the created Event
     */
    //@PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(consumes = {"application/octet-stream", "multipart/form-data"})
    public ResponseEntity<Object> createEvent(@RequestPart("event") @Valid EventDTO eventDTO, @RequestPart("mediaFiles") MultipartFile[] mediaFiles){
        try{
            Event eventToBeCreated = eventDTO.convertToEntity();
            Event event = eventService.save(eventToBeCreated, mediaFiles);
            return new ResponseEntity<>(new EventDTO(event), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
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
