package com.siit.ticketist.controller;

import com.siit.ticketist.controller.exceptions.NotFoundException;
import com.siit.ticketist.dto.EventDTO;
import com.siit.ticketist.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping()
    public ResponseEntity<List<EventDTO>> getEvents(){
        return new ResponseEntity<>(eventService.findAll(), HttpStatus.OK);
    }


    @GetMapping(value="/{id}")
    public ResponseEntity<Object> getEvent(@PathVariable long id){
        try {
            return new ResponseEntity<>(eventService.findOne(id), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(consumes = {"application/octet-stream", "multipart/form-data"})
    public ResponseEntity<Object> createEvent(@RequestPart("event") @Valid EventDTO event, @RequestPart("mediaFiles") MultipartFile[] mediaFiles){
        try{
            return new ResponseEntity<>(eventService.save(event, mediaFiles),HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
}
