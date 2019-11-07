package com.siit.ticketist.service;

import com.siit.ticketist.controller.exceptions.BadRequestException;
import com.siit.ticketist.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.siit.ticketist.controller.exceptions.NotFoundException;
import com.siit.ticketist.dto.EventDTO;
import com.siit.ticketist.dto.EventSectorDTO;
import com.siit.ticketist.model.Event;
import com.siit.ticketist.model.EventSector;
import com.siit.ticketist.repository.EventRepository;
import com.siit.ticketist.repository.EventSectorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

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

    /*
    --------------------
        Reports
    --------------------
    */
    public List<Object[]> findAllEventsReport() {
        return eventRepository.findAllEventsReport();
    }

    public List<Object[]> findAllEventReportsByVenue(Long venueId) {
        return eventRepository.findAllEventReportsByVenue(venueId);
    }

    private Map<Integer, Float> getVenueTicketReports(Long venueId) {
        List<Object[]> lista = eventRepository.getVenueTicketsReport(venueId);
        Map<Integer, Float> venueTicketSales =
                eventRepository.getVenueTicketsReport(venueId)
                        .stream()
                        .collect(Collectors.toMap(
                                obj -> (Integer)obj[0],
                                obj -> ((BigInteger)obj[1]).floatValue()));
        return venueTicketSales;
    }

    private Map<Integer, Float> getVenueRevenueReport(Long venueId){
        List<Object[]> lista = eventRepository.getVenueRevenueReport(venueId);

        Map<Integer, Float> venueRevenues =
                eventRepository.getVenueRevenueReport(venueId)
                        .stream()
                        .collect(Collectors.toMap(
                                obj -> (Integer)obj[0],
                                obj -> ((BigDecimal)obj[1]).floatValue()));
        return venueRevenues;
    }

    public Map<Integer, Float> getVenueChart(String criteria, Long venueId) {
        //TODO napisi metodu koja proverava da li venue sa tim id postoji
        if(criteria.toUpperCase().equals("TICKETS"))
            return getVenueTicketReports(venueId);
        else if(criteria.toUpperCase().equals("REVENUE"))
            return getVenueRevenueReport(venueId);
        else
            throw new BadRequestException("Invalid criteria");
    }

    /*
        Search
     */
    public List<Event> search(String eventName, String category, String venueName, Long millisecondsFrom, Long millisecondsTo){
        return eventRepository.search(eventName, category, venueName,
                convertMillisToDate(millisecondsFrom), convertMillisToDate(millisecondsTo));
    }

    private Date convertMillisToDate(Long millisecondsFrom){
        //ToDo konvertuje u CET (local timezone), mozda bude problema
        Date date = millisecondsFrom == null ? null : new Date(millisecondsFrom);
        return date;
    }

}
