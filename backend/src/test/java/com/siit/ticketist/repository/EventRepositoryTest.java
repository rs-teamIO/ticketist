package com.siit.ticketist.repository;


import com.siit.ticketist.model.Event;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void findEventsByVenueId_ShouldReturnEmptyList_whenVenueIdIsWrong(){
        List<Event> eventList = eventRepository.findEventsByVenueId(12l);
        assertEquals("event list is empty (0 elements)",0,eventList.size());

    }

    public void findEventsByVenueId_ShouldReturnEventList_whenVenueIdIsCorrect(){
        List<Event> eventList = eventRepository.findEventsByVenueId(2l);
        assertEquals("event list is not empty (2 elements)",2,eventList.size());

    }

}
