package com.siit.ticketist.unit.service;

import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.exceptions.ForbiddenException;
import com.siit.ticketist.exceptions.NotFoundException;
import com.siit.ticketist.model.*;
import com.siit.ticketist.repository.EventRepository;
import com.siit.ticketist.repository.SectorRepository;
import com.siit.ticketist.repository.TicketRepository;
import com.siit.ticketist.service.EmailService;
import com.siit.ticketist.service.EventService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.MessagingException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private SectorRepository sectorRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private EmailService emailService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(eventService, "emailService", this.emailService);
    }

    @Test
    public void checkVenueAvailability_ShouldReturnTrue_whenVenueIsFreeUnit(){
        Event event = new Event();
        Venue ven = new Venue();
        ven.setId(1l);
        event.setVenue(ven);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        Date date2 = null;
        Date dateTest = null;
        try {
            date = dateFormat.parse("15/04/2020");
            date2 = dateFormat.parse("17/04/2020");
            dateTest = dateFormat.parse("14/09/2020");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = date.getTime();
        long time2 = date2.getTime();
        event.setStartDate(new Timestamp(time));
        event.setEndDate(new Timestamp(time2));

        Event ev = new Event();
        ev.setStartDate(dateTest);
        ev.setEndDate(dateTest);
        List<Event> evList = new ArrayList<Event>();
        evList.add(ev);
        when(eventRepository.findByVenueId(ven.getId())).thenReturn(evList);

        Boolean check = eventService.checkVenueAvailability(event);
        assertTrue("venue is avaliable for this event",check);
    }


    @Test
    public void checkVenueAvailability_ShouldReturnFalse_whenVenueIsOccupiedUnit(){
        Event event = new Event();
        Venue ven = new Venue();
        ven.setId(1l);
        event.setVenue(ven);
        //'2020-03-14', '2020-03-15'
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        Date date2 = null;
        try {
            date = dateFormat.parse("15/04/2020");
            date2 = dateFormat.parse("17/04/2020");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = date.getTime();
        long time2 = date2.getTime();
        event.setStartDate(new Timestamp(time));
        event.setEndDate(new Timestamp(time2));

        Event ev = new Event();
        ev.setStartDate(date);
        ev.setEndDate(date2);
        List<Event> evList = new ArrayList<Event>();
        evList.add(ev);
        when(eventRepository.findByVenueId(ven.getId())).thenReturn(evList);

        Boolean check = eventService.checkVenueAvailability(event);
        assertFalse("venue is not avaliable for this event",check);
    }


    @Test
    public void checkSectorMaxCapacity_ShouldReturnFalse_whenCapacityIsGreaterThanSectorLimitUnit(){
        Sector sector = new Sector();
        sector.setMaxCapacity(5);
        when(sectorRepository.findById(1l)).thenReturn(Optional.of(sector));
        Boolean check = eventService.checkSectorMaxCapacity(1l,10);
        assertFalse("capacity is greater than max capacity",check);
    }

    @Test
    public void checkSectorMaxCapacity_ShouldReturnTrue_whenCapacityIsLowerThanSectorLimitUnit(){
        Sector sector = new Sector();
        sector.setMaxCapacity(5);
        when(sectorRepository.findById(1l)).thenReturn(Optional.of(sector));
        Boolean check = eventService.checkSectorMaxCapacity(1l,2);
        assertTrue("capacity is lesser than max capacity",check);
    }

    @Test
    public void generateTickets_ShouldReturnTickets_whenValidSectorIdIsSentUnit(){
        EventSector eSector = new EventSector();
        Sector sector = new Sector();
        sector.setId(1l);
        sector.setColumnsCount(2);
        sector.setRowsCount(2);

        eSector.setSector(sector);
        eSector.setNumeratedSeats(true);
        eSector.setTicketPrice(BigDecimal.valueOf(100));

        Event event = new Event();
        event.setId(10l);

        eSector.setEvent(event);

        when(sectorRepository.findById(1l)).thenReturn(Optional.of(sector));
        Set<Ticket> tickets = eventService.generateTickets(eSector);
        assertEquals("ticket list is not empty",4,tickets.size());

    }

    @Test
    public void generateTickets_ShouldThrowNotFoundException_whenSectorIsNotFoundUnit(){
        exceptionRule.expect(NotFoundException.class);
        exceptionRule.expectMessage("Sector not found.");
        EventSector eSector = new EventSector();
        Sector sector = new Sector();
        sector.setId(10l);
        eSector.setSector(sector);
        eSector.setNumeratedSeats(true);
        when(sectorRepository.findById(10l)).thenReturn(Optional.empty());
        Set<Ticket> tickets = eventService.generateTickets(eSector);
        assertEquals("ticket list is empty",0,tickets.size());
    }


    //------------------------------------------------------

    @Test
    public void Save_ShouldThrowNotFoundException_whenSectorIsNotFound(){
        exceptionRule.expect(NotFoundException.class);
        exceptionRule.expectMessage("Sector not found.");
        Event event = new Event();
        Venue ven = new Venue();
        ven.setId(1l);
        event.setVenue(ven);
        //'2020-03-14', '2020-03-15'
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        Date date2 = null;
        Date date3 = null;
        try {
            date = dateFormat.parse("15/04/2020");
            date2 = dateFormat.parse("17/04/2020");
            date3 = dateFormat.parse("14/04/2020");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = date.getTime();
        long time2 = date2.getTime();
        long time3 = date3.getTime();

        event.setStartDate(new Timestamp(time));
        event.setEndDate(new Timestamp(time2));
        event.setReservationDeadline(new Timestamp(time3));

        Set<EventSector> eventSectors = new HashSet<>();
        EventSector eSector = new EventSector();
        eSector.setNumeratedSeats(true);

        Sector sec = new Sector();
        sec.setId(100l);

        eSector.setSector(sec);
        eventSectors.add(eSector);
        event.setEventSectors(eventSectors);

        when(sectorRepository.findOne(Example.of(sec))).thenReturn(Optional.empty());

        eventService.save(event);
    }

    public void Save_ShouldPass_whenEventIsValidNumerable(){
        Event event = new Event();
        Venue ven = new Venue();
        ven.setId(1l);

        event.setVenue(ven);
        //'2020-03-14', '2020-03-15'
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        Date date2 = null;
        Date date3 = null;
        try {
            date = dateFormat.parse("15/04/2020");
            date2 = dateFormat.parse("17/04/2020");
            date3 = dateFormat.parse("14/04/2020");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = date.getTime();
        long time2 = date2.getTime();
        long time3 = date3.getTime();

        event.setStartDate(new Timestamp(time));
        event.setEndDate(new Timestamp(time2));
        event.setReservationDeadline(new Timestamp(time3));
        event.setCategory(Category.CULTURAL);
        event.setDescription("");
        event.setName("");
        event.setReservationLimit(3);

        Set<EventSector> eventSectors = new HashSet<>();

        EventSector eSector = new EventSector();
        eSector.setNumeratedSeats(true);
        eSector.setTicketPrice(BigDecimal.valueOf(100));
        eSector.setCapacity(2);

        Sector sec = new Sector();
        sec.setId(1L);
        sec.setMaxCapacity(10);

        eSector.setSector(sec);

        eventSectors.add(eSector);
        event.setEventSectors(eventSectors);

        when(sectorRepository.findById(sec.getId())).thenReturn(Optional.of(sec));
        eventService.save(event);

    }

    @Test
    public void filterEventsByDeadlineShouldReturnActiveEventsWithReservationDeadlineThreeDaysFromNow() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String threeDaysFromNow = sdf.format(addDays(new Date(), 3));
        String fiveDaysFromNow = sdf.format(addDays(new Date(), 5));

        Event cancelledEvent = new Event();
        cancelledEvent.setName("Cancelled Event");
        cancelledEvent.setIsCancelled(true);
        cancelledEvent.setReservationDeadline(sdf.parse(threeDaysFromNow));

        Event activeEventNotToBeNotified = new Event();
        activeEventNotToBeNotified.setName("Active Event Not To Be Notified");
        activeEventNotToBeNotified.setIsCancelled(false);
        activeEventNotToBeNotified.setReservationDeadline(sdf.parse(fiveDaysFromNow));

        Event activeEvent = new Event();
        activeEvent.setName("Active Event");
        activeEvent.setIsCancelled(false);
        activeEvent.setReservationDeadline(sdf.parse(threeDaysFromNow));

        when(eventRepository.findAll()).thenReturn(new ArrayList(){{
            add(cancelledEvent);
            add(activeEventNotToBeNotified);
            add(activeEvent);
        }});

        List<Event> filteredEvents = eventService.filterEventsByDeadline();
        assertThat(filteredEvents, hasSize(1));
        assertThat(filteredEvents, contains(hasProperty("name", is("Active Event"))));
    }

    @Test
    public void getTotalNumberOfActiveEventsShouldReturnHowManyEventsAreNotCancelled() {
        when(eventRepository.countByIsCancelled(false)).thenReturn(8L);
        assertEquals(Long.valueOf(8), eventService.getTotalNumberOfActiveEvents());
    }

    @Test
    public void cancelEventShouldThrowBadRequestExceptionWhenTheEventHasPassed() throws MessagingException {
        exceptionRule.expect(BadRequestException.class);

        Event event = new Event();
        event.setStartDate(new Date(System.currentTimeMillis() - 86400000));
        when(eventRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(event));

        eventService.cancelEvent(Long.valueOf(1));
    }

    @Test
    public void cancelEventShouldThrowForbiddenExceptionWhenTheEventIsAlreadyCancelled() throws MessagingException {
        exceptionRule.expect(ForbiddenException.class);

        Event event = new Event();
        event.setStartDate(new Date(System.currentTimeMillis() + 86400000));
        event.setIsCancelled(true);
        when(eventRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(event));

        eventService.cancelEvent(Long.valueOf(1));
    }

    @Test
    public void cancelEventShouldSetEventToCancelledUpdateTicketsAndNotifyUsersAboutEventCancellation() throws MessagingException {
        Event event = new Event();
        event.setId(Long.valueOf(1));
        event.setStartDate(new Date(System.currentTimeMillis() + 86400000));
        event.setIsCancelled(false);
        when(eventRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(event));

        eventService.cancelEvent(Long.valueOf(1));
        assertTrue(event.getIsCancelled());
        verify(emailService).sendEventCancelledEmails(event);
        verify(ticketRepository).deactivateTickets(event.getId());
        verify(eventRepository).save(event);
    }

    @Test
    public void searchShouldCallRepositoryMethodAndPassCorrectParametersToIt() {
        // Used to test a private method convertMillisToDate(Long)
        Long millisecondsFrom = 1583362800000L;
        Long millisecondsTo = null;
        PageRequest pageReq = PageRequest.of(0, 8);

        eventService.search("", "", "",
                millisecondsFrom, millisecondsTo, pageReq);
        verify(eventRepository).search("", "", "",
                new Date(millisecondsFrom), null, pageReq);
    }

}