package com.siit.ticketist.service;

import com.siit.ticketist.controller.exceptions.BadRequestException;
import com.siit.ticketist.controller.exceptions.NotFoundException;
import com.siit.ticketist.model.*;
import com.siit.ticketist.service.interfaces.StorageService;
import com.siit.ticketist.repository.EventRepository;
import com.siit.ticketist.repository.SectorRepository;
import com.siit.ticketist.service.interfaces.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Value("${allowed-content-types}")
    private String[] contentTypes;

    @Autowired
    private StorageService storageService;

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

    public Event save(Event event, MultipartFile[] mediaFiles){

        if(!checkEventDates(event)) {
            throw new BadRequestException("Dates are invalid");
        }

        if(!checkVenueAvailability(event)){
            throw new BadRequestException("There is already an event at that time");
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
            eventSector.setTickets(generateTickets(eventSector));
            eventSector.getEvent().getTickets().addAll(eventSector.getTickets());
        }

        List<MediaFile> eventMediaFiles = new ArrayList<>();
        Arrays.asList(mediaFiles).stream().forEach((mf) -> {
            if (!Arrays.asList(contentTypes).contains(mf.getContentType()))
                throw new BadRequestException(String.format("File %s is not a valid media file.", mf.getOriginalFilename()));
        });

        Arrays.asList(mediaFiles).stream().forEach((mf) -> {
            String fileName = UUID.randomUUID().toString();
            storageService.write(fileName, mf);
            eventMediaFiles.add(new MediaFile(fileName, mf.getContentType()));
        });

        event.getMediaFiles().addAll(eventMediaFiles);

        return eventRepository.save(event);
    }

    public Set<Ticket> generateTickets(EventSector eventSector) {
        Set<Ticket> tickets = new HashSet<>();
        if(eventSector.getNumeratedSeats()){
            Optional<Sector> sector = sectorRepository.findById(eventSector.getSector().getId());
            if(sector.isPresent()) {
                for (int row = 1; row <= sector.get().getRowsCount(); row++) {
                    for (int col = 1; col <= sector.get().getColumnsCount(); col++) {
                        tickets.add(new Ticket(null, row, col, eventSector.getTicketPrice(), -1, 0L, eventSector, eventSector.getEvent(), null));
                    }
                }
            }
        }else{
            for(int cap=0;cap<eventSector.getCapacity();cap++){
                tickets.add(new Ticket(null, -1, -1, eventSector.getTicketPrice(), -1, 0L, eventSector, eventSector.getEvent(), null));
            }
        }

        return tickets;
    }

    public boolean checkVenueAvailability(Event event) {
        boolean check = true;
        List<Event> events = eventRepository.findEventsByVenueId(event.getVenue().getId());

        for(Event e : events){
            check = event.getEndDate().before(e.getStartDate()) || event.getStartDate().after(e.getEndDate());
            if(!check) break;
        }

        return check;
    }

    public boolean checkEventDates(Event event) {
        boolean check = true;
        if(event.getReservationDeadline().after(event.getStartDate()) || event.getStartDate().after(event.getEndDate()) || new Date().after(event.getReservationDeadline())) {
            check = false;
        }
        return check;
    }

    public boolean checkSectorMaxCapacity(Long sectorID, int capacity) {
        Optional<Sector> sector = sectorRepository.findById(sectorID);
        boolean check = true;

        if(sector.isPresent()) {
            if(capacity > sector.get().getMaxCapacity()) {
                check = false;
            }
        }

        return check;
    }

    public List<Date> datesBetween(Date startDate, Date endDate) {
        List<Date> datesInRange = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);
        if(startDate.getHours()*60 + startDate.getMinutes() >=
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

    public Map<Integer, Float> getVenueTicketReports(Long venueId) {
        List<Object[]> lista = eventRepository.getVenueTicketsReport(venueId);
        Map<Integer, Float> venueTicketSales =
                eventRepository.getVenueTicketsReport(venueId)
                        .stream()
                        .collect(Collectors.toMap(
                                obj -> (Integer)obj[0],
                                obj -> ((BigInteger)obj[1]).floatValue()));
        return venueTicketSales;
    }

    public Map<Integer, Float> getVenueRevenueReport(Long venueId){
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

    public Date convertMillisToDate(Long millisecondsFrom){
        //ToDo konvertuje u CET (local timezone), mozda bude problema
        Date date = millisecondsFrom == null ? null : new Date(millisecondsFrom);
        return date;
    }

    /*
        Helper methods for
     */
    public List<Event> filterEventsByDeadline(){
        List<Event> allEvents = eventRepository.findAll();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String threeDaysFromNow = sdf.format(addDays(new Date(), 3));

        List<Event> filteredEvents = new ArrayList<>();
        for(Event e : allEvents){
            if(sdf.format(e.getReservationDeadline()).equals(threeDaysFromNow))
                filteredEvents.add(e);
        }
        return filteredEvents;
    }

    public Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

}
