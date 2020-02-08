package com.siit.ticketist.integration.service;

import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.exceptions.ForbiddenException;
import com.siit.ticketist.exceptions.NotFoundException;
import com.siit.ticketist.model.*;
import com.siit.ticketist.service.EventService;
import com.siit.ticketist.service.TicketService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/data.sql")
public class EventServiceTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private TicketService ticketService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void findAllWithPagesShouldReturnPagedActiveEvents() {
        Page<Event> eventPage = eventService.findAll(PageRequest.of(0, 10));
        assertEquals(10, eventPage.getNumberOfElements());
        assertEquals(12, eventPage.getTotalElements());
        List<Long> eventIds = eventPage.getContent().stream().map(event -> event.getId()).collect(Collectors.toList());
        assertThat(eventIds, containsInAnyOrder(1L,  2L, 3L, 4L, 5L, 6L, 7L, 8L, 10L, 11L));
    }

    @Test
    public void findOne_ShouldThrowException_whenIdIsWrong(){
        exceptionRule.expect(NotFoundException.class);
        eventService.findOne(100l);
    }

    @Test
    public void findOne_ShouldReturnEvent_whenIdIsFound(){
        eventService.findOne(1l);
    }

    @Test
    public void checkVenueAvailability_ShouldReturnTrue_whenVenueIsFree(){
        Event event = new Event();
        Venue ven = new Venue();
        ven.setId(1l);
        event.setVenue(ven);
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
        Boolean check = eventService.checkVenueAvailability(event);
        assertTrue("venue is avaliable for this event",check);
    }


    @Test
    public void checkVenueAvailability_ShouldReturnFalse_whenVenueIsOccupied(){
        Event event = new Event();
        Venue ven = new Venue();
        ven.setId(1l);
        event.setVenue(ven);
        //'2020-03-14', '2020-03-15'
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        Date date2 = null;
        try {
            date = dateFormat.parse("15/03/2020");
            date2 = dateFormat.parse("17/03/2020");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = date.getTime();
        long time2 = date2.getTime();
        event.setStartDate(new Timestamp(time));
        event.setEndDate(new Timestamp(time2));
        Boolean check = eventService.checkVenueAvailability(event);
        assertFalse("venue is not avaliable for this event",check);
    }


    @Test
    public void checkEventDates_ShouldReturnTrue_whenDatesAreValid(){
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
            date = dateFormat.parse("15/03/2020");
            date2 = dateFormat.parse("17/03/2020");
            date3 = dateFormat.parse("14/03/2020");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = date.getTime();
        long time2 = date2.getTime();
        long time3 = date3.getTime();
        event.setStartDate(new Timestamp(time));
        event.setEndDate(new Timestamp(time2));
        event.setReservationDeadline(new Timestamp(time3));
        Boolean check = eventService.checkEventDates(event);
        assertTrue("event dates are correct",check);
    }

    @Test
    public void checkEventDates_ShouldReturnFalse_whenDatesAreInvalid(){
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
            date = dateFormat.parse("15/03/2020");
            date2 = dateFormat.parse("13/03/2020");
            date3 = dateFormat.parse("14/03/2020");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = date.getTime();
        long time2 = date2.getTime();
        long time3 = date3.getTime();
        event.setStartDate(new Timestamp(time));
        event.setEndDate(new Timestamp(time2));
        event.setReservationDeadline(new Timestamp(time3));
        Boolean check = eventService.checkEventDates(event);
        assertFalse("event dates are not in order",check);
    }

    @Test
    public void checkSectorMaxCapacity_ShouldReturnFalse_whenCapacityIsGreaterThanSectorLimit(){
        Boolean check = eventService.checkSectorMaxCapacity(1l,10);
        assertFalse("capacity is greater than max capacity",check);
    }


    @Test
    public void checkSectorMaxCapacity_ShouldReturnTrue_whenCapacityIsLowerThanSectorLimit(){
        Boolean check = eventService.checkSectorMaxCapacity(1l,2);
        assertTrue("capacity is lesser than max capacity",check);
    }

    @Test
    public void datesBetween_ShouldReturnListOfDates_whenTwoDatesAreSent(){
        Date d = new Date(2020,1,12);
        Date d2 = new Date(2020,1,14);
        List<Date> dates = eventService.datesBetween(d,d2);
        assertEquals("Dates list should have 3 dates",3,dates.size());
    }

    @Test
    public void generateTickets_ShouldReturnTickets_whenValidSectorIdIsSent(){
        EventSector eSector = new EventSector();
        Sector sector = new Sector();
        sector.setId(1l);
        eSector.setSector(sector);
        eSector.setNumeratedSeats(true);
        Set<Ticket> tickets = eventService.generateTickets(eSector);
        assertEquals("ticket list is not empty",4,tickets.size());

    }


    @Test
    public void generateTickets_ShouldThrowNotFoundException_whenSectorIsNotFound(){
        exceptionRule.expect(NotFoundException.class);
        exceptionRule.expectMessage("Sector not found.");
        EventSector eSector = new EventSector();
        Sector sector = new Sector();
        sector.setId(10l);
        eSector.setSector(sector);
        eSector.setNumeratedSeats(true);
        Set<Ticket> tickets = eventService.generateTickets(eSector);
    }


    @Test
    public void generateTickets_ShouldReturnList_whenItsInumerableWithCorrectCapacity(){
        EventSector eSector = new EventSector();
        eSector.setNumeratedSeats(false);
        eSector.setCapacity(5);
        Set<Ticket> tickets = eventService.generateTickets(eSector);
        assertEquals("ticket list is not empty",5,tickets.size());
    }


    @Test
    public void generateTickets_ShouldReturnEmptyList_whenItsInumerableWithEmptyCapacity(){
        EventSector eSector = new EventSector();
        eSector.setNumeratedSeats(false);
        eSector.setCapacity(0);
        Set<Ticket> tickets = eventService.generateTickets(eSector);
        assertEquals("ticket list is not empty",0,tickets.size());

    }

    @Test
    public void Save_ShouldThrowException_whenVenueIsOccupied(){
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("There is already an event at that time");
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
            date = dateFormat.parse("15/03/2020");
            date2 = dateFormat.parse("17/03/2020");
            date3 = dateFormat.parse("14/03/2020");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = date.getTime();
        long time2 = date2.getTime();
        long time3 = date3.getTime();
        event.setStartDate(new Timestamp(time));
        event.setEndDate(new Timestamp(time2));
        event.setReservationDeadline(new Timestamp(time3));

        eventService.save(event);
    }


    @Test
    public void Save_ShouldThrowException_whenEventDatesAreNotCorrect(){
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Dates are invalid");
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
            date = dateFormat.parse("15/03/2020");
            date2 = dateFormat.parse("13/03/2020");
            date3 = dateFormat.parse("14/03/2020");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = date.getTime();
        long time2 = date2.getTime();
        long time3 = date3.getTime();
        event.setStartDate(new Timestamp(time));
        event.setEndDate(new Timestamp(time2));
        event.setReservationDeadline(new Timestamp(time3));

        eventService.save(event);
    }


    @Test
    public void Save_ShouldThrowException_whenCapacityOfInumerableSectorisNull(){
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Capacity of event sector with innumerable seats cannot be null");
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
        eSector.setNumeratedSeats(false);
        eventSectors.add(eSector);
        event.setEventSectors(eventSectors);

        eventService.save(event);
    }

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


        eventService.save(event);
    }

    @Test
    public void Save_ShouldThrowException_whenCapacityExceedsSectorCapacity(){
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Capacity is greater than max capacity");
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
        eSector.setNumeratedSeats(false);
        eSector.setCapacity(100);
        eventSectors.add(eSector);
        event.setEventSectors(eventSectors);
        Sector sec = new Sector();
        sec.setId(1L);
        sec.setMaxCapacity(10);
        eSector.setSector(sec);

        eventService.save(event);
    }

    @Test
    public void Save_ShouldPass_whenEventIsValidInumerable(){
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
        eSector.setNumeratedSeats(false);
        eSector.setTicketPrice(BigDecimal.valueOf(100));
        eSector.setCapacity(2);

        Sector sec = new Sector();
        sec.setId(1L);
        sec.setMaxCapacity(100);

        eSector.setSector(sec);

        eventSectors.add(eSector);
        event.setEventSectors(eventSectors);

        Event ev = eventService.save(event);
        assertEquals(ev.getName(),event.getName());
    }

    @Test
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

        Event ev = eventService.save(event);
        assertEquals(ev.getName(),event.getName());
    }

    @Test
    public void getTotalNumberOfActiveEventsShouldReturnHowManyEventsAreNotCancelled() {
        assertEquals(Long.valueOf(12), eventService.getTotalNumberOfActiveEvents());
    }

    @Test
    public void filterEventsByDeadlineShouldReturnActiveEventsWithReservationDeadlineThreeDaysFromNow() {
        List<Event> events = eventService.filterEventsByDeadline();
        assertThat("Only active events with reservation deadline 3 days from now are returned", events, hasSize(2));
        assertThat(events, containsInAnyOrder(
                hasProperty("id", is(Long.valueOf(7))),
                hasProperty("id", is(Long.valueOf(8)))
        ));
    }

    @Test
    public void cancelEventShouldThrowBadRequestExceptionWhenTheEventHasPassed() throws MessagingException {
        exceptionRule.expect(BadRequestException.class);
        eventService.cancelEvent(Long.valueOf(2));
    }

    @Test
    public void cancelEventShouldThrowForbiddenExceptionWhenTheEventIsAlreadyCancelled() throws MessagingException {
        exceptionRule.expect(ForbiddenException.class);
        eventService.cancelEvent(Long.valueOf(9));
    }

    @Test
    public void cancelEventShouldSetEventToCancelledUpdateTicketsAndNotifyUsersAboutEventCancellation() throws MessagingException {
        eventService.cancelEvent(Long.valueOf(8));
        Event cancelledEvent = eventService.findOne(Long.valueOf(8));
        assertEquals(true, cancelledEvent.getIsCancelled());
        List<TicketStatus> eventTicketStatuses = ticketService.findAllByEventId(cancelledEvent.getId()).stream()
                .map(ticket -> ticket.getStatus()).collect(Collectors.toList());
        assertThat(eventTicketStatuses, everyItem(equalTo(TicketStatus.EVENT_CANCELLED)));
    }

    @Test
    public void searchShouldCallRepositoryMethodAndPassCorrectParametersToIt() {
        // Used to test a private method convertMillisToDate(Long)
        Long millisecondsFrom = 1594677600000L;     // 2020-07-14 00:00:00
        Long millisecondsTo = null;
        PageRequest pageReq = PageRequest.of(0, 1);

        List<Event> events = eventService.search("", "", "",
                millisecondsFrom, millisecondsTo, pageReq).getContent();
        assertThat(events, hasSize(1));
        assertThat(events, contains(hasProperty("id", is(13L))));
    }

}
