package com.siit.ticketist.service;

import com.siit.ticketist.controller.exceptions.BadRequestException;
import com.siit.ticketist.controller.exceptions.NotFoundException;
import com.siit.ticketist.model.*;
import com.siit.ticketist.service.interfaces.StorageService;
import com.siit.ticketist.repository.EventRepository;
import com.siit.ticketist.repository.SectorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

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

    private Set<Ticket> generateTickets(EventSector eventSector) {
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

    private boolean checkVenueAvailability(Event event) {
        boolean check = true;
        List<Event> events = eventRepository.findEventsByVenueId(event.getVenue().getId());

        for(Event e : events){
            check = event.getEndDate().before(e.getStartDate()) || event.getStartDate().after(e.getEndDate());
            if(!check) break;
        }

        return check;
    }

    private boolean checkEventDates(Event event) {
        boolean check = true;
        if(event.getReservationDeadline().after(event.getStartDate()) || event.getStartDate().after(event.getEndDate()) || new Date().after(event.getReservationDeadline())) {
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
