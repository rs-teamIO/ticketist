package com.siit.ticketist.controller;

import com.siit.ticketist.controller.exceptions.NotFoundException;
import com.siit.ticketist.dto.EventDTO;
import com.siit.ticketist.dto.SearchDTO;
import com.siit.ticketist.model.Event;
import com.siit.ticketist.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
            Event event = eventService.findOne(id);
            return new ResponseEntity<>(new EventDTO(event), HttpStatus.OK);
    }

//    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(consumes = {"application/octet-stream", "multipart/form-data"})
    public ResponseEntity<Object> createEvent(@RequestPart("event") @Valid EventDTO eventDTO, @RequestPart("mediaFiles") MultipartFile[] mediaFiles){

            Event event = eventDTO.convertToEntity();
            return new ResponseEntity<>(new EventDTO(eventService.save(event, mediaFiles)),HttpStatus.OK);

    }

    @PostMapping(value = "/search")
    public ResponseEntity search(@RequestBody SearchDTO dto) {
        List<Event> events = eventService.search(dto.getEventName(), dto.getCategory(), dto.getVenueName(), dto.getStartDate(), dto.getEndDate());
        List<EventDTO> eventDTOs = events.stream().map(obj -> new EventDTO((Event) obj)).collect(Collectors.toList());
        return new ResponseEntity(eventDTOs, HttpStatus.OK);
    }
}
