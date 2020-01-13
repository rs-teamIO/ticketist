package com.siit.ticketist.service;
import static org.junit.Assert.*;

import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.exceptions.NotFoundException;
import com.siit.ticketist.model.*;
import com.siit.ticketist.repository.EventRepository;
import com.siit.ticketist.repository.SectorRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/data.sql")
public class EventServiceTest {

    @Autowired
    private EventService eventService;

    @Test(expected = NotFoundException.class)
    public void findOne_ShouldThrowException_whenIdIsWrong(){
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


    @Test(expected = NotFoundException.class)
    public void generateTickets_ShouldThrowNotFoundException_whenSectorIsNotFound(){
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

    @Test(expected = BadRequestException.class)
    public void Save_ShouldThrowException_whenVenueIsOccupied(){
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


    @Test(expected = BadRequestException.class)
    public void Save_ShouldThrowException_whenEventDatesAreNotCorrect(){
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


    @Test(expected = BadRequestException.class)
    public void Save_ShouldThrowException_whenCapacityOfInumerableSectorisNull(){
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

    @Test(expected = NotFoundException.class)
    public void Save_ShouldThrowNotFoundException_whenSectorIsNotFound(){
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

    @Test(expected = BadRequestException.class)
    public void Save_ShouldThrowException_whenCapacityExceedsSectorCapacity(){
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

        eventService.save(event);

    }
}
