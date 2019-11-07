package com.siit.ticketist.service;

import com.siit.ticketist.controller.exceptions.BadRequestException;
import com.siit.ticketist.controller.exceptions.NotFoundException;
import com.siit.ticketist.model.Event;
import com.siit.ticketist.model.EventSector;
import com.siit.ticketist.model.Sector;
import com.siit.ticketist.repository.EventRepository;
import com.siit.ticketist.repository.SectorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SectorRepository sectorRepository;

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public Event findOne(Long id) throws NotFoundException {
        return eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event not found"));
    }

    public Event save(Event event) throws BadRequestException {

        if(!checkEventDates(event)) {
            throw new BadRequestException("Dates are invalid");
        }

        boolean capacityCheck = true;
        Optional<Sector> sector;

        for(EventSector eventSector : event.getEventSectors()) {
            if(eventSector.getNumeratedSeats()) {
                sector = sectorRepository.findById(eventSector.getSector().getId());
                sector.ifPresent(value -> eventSector.setCapacity(value.getMaxCapacity()));
            } else {
                if(eventSector.getCapacity() == null) {
                    throw new BadRequestException("Capacity of event sector with innumerable seats cannot be null");
                }
                capacityCheck = checkSectorMaxCapacity(eventSector.getSector().getId(), eventSector.getCapacity());
                if(!capacityCheck) break;
            }
        }

        if(!capacityCheck) throw new BadRequestException("Capacity is greater than max capacity");

        List<Date> datesInRange = datesBetween(event.getStartDate(), event.getEndDate());
        Set<EventSector> eventSectorList = new HashSet<>();

        for(Date date : datesInRange) {
            for(EventSector eventSector : event.getEventSectors()) {
                EventSector newEventSector = new EventSector(eventSector.getId(), eventSector.getTicketPrice(), eventSector.getNumeratedSeats(),
                        date, eventSector.getCapacity(), eventSector.getTickets(), eventSector.getSector(), eventSector.getEvent());
                eventSectorList.add(newEventSector);
            }
        }

        event.setEventSectors(eventSectorList);

        for(EventSector eventSector : event.getEventSectors()) {
            eventSector.setEvent(event);
        }

        return eventRepository.save(event);
    }

    private boolean checkEventDates(Event event) {
        boolean check = true;
        if(event.getReservationDeadline().after(event.getStartDate()) || event.getStartDate().after(event.getEndDate())) {
            check = false;
        }
        return check;
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
