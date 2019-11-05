package com.siit.ticketist.service;

import com.siit.ticketist.model.Event;
import com.siit.ticketist.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public List<Object[]> findAllEventsReport(){
        return eventRepository.findAllEventsReport();
    }

}
