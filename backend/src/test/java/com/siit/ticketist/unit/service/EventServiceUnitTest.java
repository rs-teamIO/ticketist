package com.siit.ticketist.unit.service;
import static org.junit.Assert.*;

import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.exceptions.NotFoundException;
import com.siit.ticketist.model.*;
import com.siit.ticketist.repository.EventRepository;
import com.siit.ticketist.repository.SectorRepository;
import com.siit.ticketist.service.EventService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
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
public class EventServiceUnitTest {

    @Autowired
    private EventService eventService;

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private SectorRepository sectorRepository;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

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
        Mockito.when(eventRepository.findByVenueId(ven.getId())).thenReturn(evList);

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
        Mockito.when(eventRepository.findByVenueId(ven.getId())).thenReturn(evList);

        Boolean check = eventService.checkVenueAvailability(event);
        assertFalse("venue is not avaliable for this event",check);
    }


    @Test
    public void checkSectorMaxCapacity_ShouldReturnFalse_whenCapacityIsGreaterThanSectorLimitUnit(){
        Sector sector = new Sector();
        sector.setMaxCapacity(5);
        Mockito.when(sectorRepository.findById(1l)).thenReturn(Optional.of(sector));
        Boolean check = eventService.checkSectorMaxCapacity(1l,10);
        assertFalse("capacity is greater than max capacity",check);
    }

    @Test
    public void checkSectorMaxCapacity_ShouldReturnTrue_whenCapacityIsLowerThanSectorLimitUnit(){
        Sector sector = new Sector();
        sector.setMaxCapacity(5);
        Mockito.when(sectorRepository.findById(1l)).thenReturn(Optional.of(sector));
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

        Mockito.when(sectorRepository.findById(1l)).thenReturn(Optional.of(sector));
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
        Mockito.when(sectorRepository.findById(10l)).thenReturn(Optional.empty());
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

        Mockito.when(sectorRepository.findOne(Example.of(sec))).thenReturn(Optional.empty());

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

        Mockito.when(sectorRepository.findById(sec.getId())).thenReturn(Optional.of(sec));
        eventService.save(event);

    }

}