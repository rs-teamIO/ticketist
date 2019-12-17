package com.siit.ticketist.service;

import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.exceptions.NotFoundException;
import com.siit.ticketist.model.*;
import com.siit.ticketist.repository.EventRepository;
import com.siit.ticketist.repository.SectorRepository;
import com.siit.ticketist.service.interfaces.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Event service layer.
 */
@Service
public class EventService {

    @Value("${allowed-content-types}")
    private String[] contentTypes;

    private final StorageService storageService;
    private final EventRepository eventRepository;
    private final SectorRepository sectorRepository;

    public EventService(StorageService storageService, EventRepository eventRepository, SectorRepository sectorRepository) {
        this.storageService = storageService;
        this.eventRepository = eventRepository;
        this.sectorRepository = sectorRepository;
    }

    /**
     * Returns a list of all available events
     *
     * @return {@link List} of all {@link Event} objects
     */
    public List<Event> findAll() {
        return this.eventRepository.findAll();
    }

    /**
     * Finds a {@link Event} with given ID.
     * If no such event is found, the method throws a {@link NotFoundException}
     *
     * @param id ID of the event
     * @return {@link Event} instance
     * @throws NotFoundException Exception thrown in case no event with given ID is found.
     */
    public Event findOne(Long id) throws NotFoundException {
        return this.eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found."));
    }

    public Event save(Event event) {

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

        return eventRepository.save(event);
    }

    public Set<Ticket> generateTickets(EventSector eventSector) {
        Set<Ticket> tickets = new HashSet<>();
        if(eventSector.getNumeratedSeats()){
            Optional<Sector> sector = sectorRepository.findById(eventSector.getSector().getId());
            if(sector.isPresent()) {
                for (int row = 1; row <= sector.get().getRowsCount(); row++) {
                    for (int col = 1; col <= sector.get().getColumnsCount(); col++) {
                        tickets.add(new Ticket(null, row, col, eventSector.getTicketPrice(), TicketStatus.FREE, 0L, eventSector, eventSector.getEvent(), null));
                    }
                }
            }
        }else{
            for(int cap=0;cap<eventSector.getCapacity();cap++){
                tickets.add(new Ticket(null, -1, -1, eventSector.getTicketPrice(), TicketStatus.FREE, 0L, eventSector, eventSector.getEvent(), null));
            }
        }

        return tickets;
    }

    public boolean checkVenueAvailability(Event event) {
        boolean check = true;
        List<Event> events = eventRepository.findByVenueId(event.getVenue().getId());

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

    /**
     * Adds media files (images, videos) to {@link Event} with specified ID.
     * If no such event is found, the method throws a {@link NotFoundException}
     *
     * @param eventId ID of the event
     * @param mediaFiles List of media files
     * @return {@link Event} instance
     * @throws NotFoundException Exception thrown in case no event with given ID is found.
     * @throws BadRequestException Exception thrown in case one of the submitted files is not valid.
     */
    public Event addMediaFiles(Long eventId, MultipartFile[] mediaFiles) {
        Event event = this.findOne(eventId);

        Stream.of(mediaFiles).forEach((mf) -> {
            if (!Arrays.asList(contentTypes).contains(mf.getContentType()))
                throw new BadRequestException(String.format("File %s is not a valid media file.", mf.getOriginalFilename()));
        });

        ArrayList<MediaFile> eventMediaFiles = new ArrayList<>();
        Stream.of(mediaFiles).forEach((mf) -> {
            String fileName = UUID.randomUUID().toString();
            this.storageService.write(fileName, mf);
            eventMediaFiles.add(new MediaFile(fileName, mf.getContentType()));
        });

        event.getMediaFiles().addAll(eventMediaFiles);

        return this.eventRepository.save(event);
    }

    /**
     * Returns a collection of all {@link MediaFile} instances associated with the requested {@link Event}
     *
     * @param eventId ID of the event the media files are requested for
     * @return Set<MediaFile> Collection of media files
     */
    public Set<MediaFile> getMediaFiles(Long eventId) {
        return this.findOne(eventId).getMediaFiles();
    }

    /**
     * Returns the byte representation of the requested file.
     *
     * @param eventId ID of the event the media files are requested for
     * @param fileName Name of the requested file
     * @return Byte array representation of the requested file
     * @throws BadRequestException Exception thrown in case the file cannot be found
     * TODO: IOException
     */
    public byte[] getMediaFile(Long eventId, String fileName) {
        Event event = this.findOne(eventId);
        List<MediaFile> mediaFiles = event.getMediaFiles().stream()
                .filter(mediaFile -> mediaFile.getFileName().equals(fileName))
                .collect(Collectors.toList());
        if(mediaFiles.isEmpty())
            throw new BadRequestException("Event does not have a file with given name.");

        return this.storageService.read(mediaFiles.get(0).getFileName());
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
        Helper method for email notifications
     */
    public List<Event> filterEventsByDeadline(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String threeDaysFromNow = sdf.format(addDays(new Date(), 3));

        List<Event> filteredEvents = new ArrayList<>();
        eventRepository.findAll().stream().forEach(event -> {
            if(sdf.format(event.getReservationDeadline()).equals(threeDaysFromNow))
                filteredEvents.add(event);
        });

        return filteredEvents;
    }

    public Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

}
