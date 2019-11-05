package com.siit.ticketist.service;

import com.siit.ticketist.controller.exceptions.NotFoundException;
import com.siit.ticketist.dto.EventDTO;
import com.siit.ticketist.model.Event;
import com.siit.ticketist.model.EventSector;
import com.siit.ticketist.repository.EventRepository;
import com.siit.ticketist.repository.EventSectorRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventSectorRepository eventSectorRepository;
    public List<EventDTO> findAll() {
        List<Event> events = eventRepository.findAll();
        List<EventDTO> venuesDTO = new ArrayList<>();
        for(Event event : events) {
            venuesDTO.add(new EventDTO(event));
        }
        return venuesDTO;
    }

    public EventDTO findOne(long id) throws NotFoundException{
        return new EventDTO(eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event not found")));
    }

    public EventDTO save(EventDTO event){
        List<Date> dates = new ArrayList<>();

        return new EventDTO(eventRepository.save(event.convertToEntity()));
    }

}
