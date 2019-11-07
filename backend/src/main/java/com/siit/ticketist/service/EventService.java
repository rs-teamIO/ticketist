package com.siit.ticketist.service;

import com.siit.ticketist.controller.exceptions.BadRequestException;
import com.siit.ticketist.controller.exceptions.NotFoundException;
import com.siit.ticketist.dto.EventDTO;
import com.siit.ticketist.dto.EventSectorDTO;
import com.siit.ticketist.model.Event;
import com.siit.ticketist.model.EventSector;
import com.siit.ticketist.model.Sector;
import com.siit.ticketist.repository.EventRepository;
import com.siit.ticketist.repository.EventSectorRepository;
import com.siit.ticketist.repository.SectorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SectorRepository sectorRepository;

    public List<EventDTO> findAll() {
        List<Event> events = eventRepository.findAll();
        List<EventDTO> eventsDTO = new ArrayList<>();
        for(Event event : events) {
            eventsDTO.add(new EventDTO(event));
        }
        return eventsDTO;
    }

    public EventDTO findOne(Long id) throws NotFoundException {
        return new EventDTO(eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event not found")));
    }

    public EventDTO save(EventDTO event) throws BadRequestException {
        if(event.getReservationDeadline().after(event.getStartDate()) || event.getStartDate().after(event.getEndDate())) {
            throw new BadRequestException("Dates are invalid");
        }

        boolean capacityCheck = true;
        Optional<Sector> sector;
        for(EventSectorDTO eventSectorDTO : event.getEventSectors()) {
            if (eventSectorDTO.getNumeratedSeats()) {
                sector = sectorRepository.findById(eventSectorDTO.getSectorId());
                sector.ifPresent(value -> eventSectorDTO.setCapacity(value.getMaxCapacity()));
            } else {
                if(eventSectorDTO.getCapacity() == null) {
                    throw new BadRequestException("Capacity of event sector with innumerable seats cannot be null");
                }
            }
            capacityCheck = checkSectorMaxCapacity(eventSectorDTO.getSectorId(), eventSectorDTO.getCapacity());
            if(!capacityCheck) break;
        }

        if(!capacityCheck) throw new BadRequestException("Capacity is greater than max capacity");

        List<Date> datesInRange = datesBetween(event.getStartDate(), event.getEndDate());
        Set<EventSectorDTO> eventSectorListDTO = new HashSet<>();

        for(Date date : datesInRange) {
            for(EventSectorDTO eventsectorDTO : event.getEventSectors()) {
                eventsectorDTO.setDate(date);
                eventSectorListDTO.add(new EventSectorDTO(eventsectorDTO.convertToEntity()));
            }
            System.out.println("Date: " + new Date(date.getTime()));
        }

        event.setEventSectors(eventSectorListDTO);

        Event eventEntity = event.convertToEntity();
        for(EventSector eventSector : eventEntity.getEventSectors()) {
            eventSector.setEvent(eventEntity);
        }

        return new EventDTO(eventRepository.save(eventEntity));
    }

    private boolean checkSectorMaxCapacity(Long sectorID, int capacity) {
        Optional<Sector> sector = sectorRepository.findById(sectorID);
        boolean check = true;

        if(sector.isPresent()) {
            if(capacity > sector.get().getMaxCapacity()) {
                check = false;
            }
        }

        return check;
    }

    private List<Date> datesBetween(Date startDate, Date endDate) {
        List<Date> datesInRange = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);
        if(startDate.getHours()*60 + startDate.getMinutes() >
                endDate.getHours()*60 + endDate.getMinutes()) {
            endCalendar.add(Calendar.DATE, 1);
        }

        while (calendar.before(endCalendar)) {
            Date result = calendar.getTime();
            datesInRange.add(result);
            calendar.add(Calendar.DATE, 1);
        }

        return datesInRange;
    }


}
