package com.siit.ticketist.controller;

import com.siit.ticketist.controller.exceptions.NotFoundException;
import com.siit.ticketist.dto.EventDTO;
import com.siit.ticketist.model.Event;
import com.siit.ticketist.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping()
    public ResponseEntity<List<EventDTO>> getEvents(){
        List<Event> events = eventService.findAll();
        List<EventDTO> eventsDTO = new ArrayList<>();
        for(Event event : events) {
            eventsDTO.add(new EventDTO(event));
        }
        return new ResponseEntity<>(eventsDTO, HttpStatus.OK);
    }


    @GetMapping(value="/{id}")
    public ResponseEntity<Object> getEvent(@PathVariable long id) {
        try {
            Event event = eventService.findOne(id);
            return new ResponseEntity<>(new EventDTO(event), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

//    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping()
    public ResponseEntity<Object> createEvent(@Valid @RequestBody EventDTO eventDTO){
        try{
            Event event = eventDTO.convertToEntity();
            return new ResponseEntity<>(new EventDTO(eventService.save(event)),HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
}
