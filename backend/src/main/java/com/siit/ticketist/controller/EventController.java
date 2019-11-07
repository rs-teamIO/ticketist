package com.siit.ticketist.controller;

import com.siit.ticketist.controller.exceptions.NotFoundException;
import com.siit.ticketist.dto.EventDTO;
import com.siit.ticketist.model.Event;
import com.siit.ticketist.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping()
    public ResponseEntity<List<EventDTO>> getEvents() {
        return new ResponseEntity<>(eventService.findAll(), HttpStatus.OK);
    }


    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getEvent(@PathVariable long id) {
        try {
            return new ResponseEntity<>(eventService.findOne(id), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping()
    public ResponseEntity<Object> createEvent(@Valid @RequestBody EventDTO event) {
        try {
            return new ResponseEntity<>(eventService.save(event), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/search")
    public ResponseEntity search(@PathParam("eventName") String eventName,
                                 @PathParam("category") String category,
                                 @PathParam("venueName") String venueName,
                                 @PathParam("startDate") Long startDate,
                                 @PathParam("endDate") Long endDate) {
        List<Event> events = eventService.search(eventName, category, venueName, startDate, endDate);
        List<EventDTO> eventDTOs = events.stream().map(obj -> new EventDTO((Event)obj)).collect(Collectors.toList());
        return new ResponseEntity(eventDTOs, HttpStatus.OK);
    }
}
