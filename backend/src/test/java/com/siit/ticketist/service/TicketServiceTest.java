package com.siit.ticketist.service;

import com.itextpdf.text.log.SysoCounter;
import com.siit.ticketist.dto.AuthenticationRequest;
import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.model.*;
import com.siit.ticketist.security.TokenUtils;
import com.siit.ticketist.security.UserDetailsServiceImpl;
import lombok.With;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/data.sql")
public class TicketServiceTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Before
    public void before(){
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken("user2020", "123456"));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void ga() throws ParseException {
        /*
        (1, true, 'Spens', 'Sutjeska 2', 'Novi Sad', 45.2470, 19.8453),
    (2, true, 'Novi Sad Fair', 'Hajduk Veljkova 11', 'Novi Sad', 45.267136, 19.833549),
    (3, false, 'Startit Centar', 'Miroslava Antica 2', 'Novi Sad', 45.267136, 19.833549);

         */
        Venue venue1 = new Venue();
        venue1.setIsActive(true);
        venue1.setLongitude(100.00);
        venue1.setLatitude(100.00);
        venue1.setCity("Novi Sad");
        venue1.setName("SPENS");
        venue1.setStreet("Sutjeska 2");
        venue1.setId(1l);

        Venue venue2 = new Venue();
        venue2.setId(2l);
        venue2.setIsActive(true);
        venue2.setName("Novi Sad Fair");
        venue2.setStreet("Hajduk Veljkova 11");
        venue2.setCity("Novi Sad");
        venue2.setLatitude(45.00);
        venue2.setLongitude(45.00);

        Venue venue3 = new Venue();
        venue3.setId(3l);
        venue3.setIsActive(false);
        venue3.setName("Startit Centar");
        venue3.setStreet("Miroslava Antica 2");
        venue3.setCity("Novi Sad");
        venue3.setLongitude(35.00);
        venue3.setLatitude(35.00);


        Sector sector1 = new Sector();
        sector1.setId(1l);
        sector1.setColumnsCount(2);
        sector1.setRowsCount(2);
        sector1.setMaxCapacity(4);
        sector1.setName("Sever");
        sector1.setStartRow(1);
        sector1.setStartColumn(1);

        Sector sector2 = new Sector();
        sector2.setId(2l);
        sector2.setColumnsCount(3);
        sector2.setRowsCount(2);
        sector2.setMaxCapacity(6);
        sector2.setName("Zapad");
        sector2.setStartRow(10);
        sector2.setStartColumn(10);


        Sector sector3 = new Sector();
        sector3.setId(3l);
        sector3.setColumnsCount(3);
        sector3.setRowsCount(3);
        sector3.setMaxCapacity(9);
        sector3.setName("Istok");
        sector3.setStartRow(20);
        sector3.setStartColumn(20);

        Sector sector4 = new Sector();
        sector4.setId(4l);
        sector4.setColumnsCount(4);
        sector4.setRowsCount(4);
        sector4.setMaxCapacity(16);
        sector4.setName("Jug");
        sector4.setStartRow(30);
        sector4.setStartColumn(30);


        Sector sector5 = new Sector();
        sector5.setId(5l);
        sector5.setColumnsCount(2);
        sector5.setRowsCount(2);
        sector5.setMaxCapacity(4);
        sector5.setName("Sever");
        sector5.setStartRow(10);
        sector5.setStartColumn(10);

        Sector sector6 = new Sector();
        sector6.setId(6l);
        sector6.setColumnsCount(2);
        sector6.setRowsCount(2);
        sector6.setMaxCapacity(4);
        sector6.setName("Zapad");
        sector6.setStartRow(20);
        sector6.setStartColumn(20);

        /*
         (1, 'EXIT Festival', 'Cool music!', 'ENTERTAINMENT', '2020-06-14', '2020-06-15', '2020-06-5', 3, 1),
    (2, 'NBA', 'NBA Playoff 1/4!', 'SPORTS', '2019-06-14', '2019-06-14', '2019-06-5', 3, 3);
         */

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);


        Event event1 = new Event();
        event1.setId(1l);
        event1.setName("EXIT Festival");
        event1.setDescription("Cool music!");
        event1.setCategory(Category.ENTERTAINMENT);
        event1.setStartDate(format.parse("2020-06-14"));
        event1.setEndDate(format.parse("2020-06-15"));
        event1.setReservationDeadline(format.parse("2020-06-5"));
        event1.setReservationLimit(3);


        Event event2 = new Event();
        event2.setId(2l);
        event2.setName("NBA");
        event2.setDescription("NBA Playoff 1/4!");
        event2.setCategory(Category.SPORTS);
        event2.setStartDate(format.parse("2019-06-14"));
        event2.setEndDate(format.parse("2019-06-14"));
        event2.setReservationDeadline(format.parse("2019-06-5"));
        event2.setReservationLimit(3);

        /*
        (1, 4, '2020-06-14', 1, 35.0, 1, 1),
    (2, 4, '2020-06-15', 0, 25.0, 1, 1),
    (3, 4, '2020-06-14', 1, 45.0, 2, 6);
         */
        EventSector eventSector1 = new EventSector();
        eventSector1.setId(1l);
        eventSector1.setCapacity(4);
        eventSector1.setDate(format.parse("2020-06-14"));
        eventSector1.setNumeratedSeats(true);
        eventSector1.setTicketPrice(BigDecimal.valueOf(35));

        EventSector eventSector2 = new EventSector();
        eventSector2.setId(2l);
        eventSector2.setCapacity(4);
        eventSector2.setDate(format.parse("2020-06-15"));
        eventSector2.setNumeratedSeats(false);
        eventSector2.setTicketPrice(BigDecimal.valueOf(25));

        EventSector eventSector3 = new EventSector();
        eventSector3.setId(3l);
        eventSector3.setCapacity(4);
        eventSector3.setDate(format.parse("2020-06-14"));
        eventSector3.setNumeratedSeats(true);
        eventSector3.setTicketPrice(BigDecimal.valueOf(45));

        /*

    (1, 1, 1),
    (2, 1, 1);
         */

        Reservation reservation1 = new Reservation();
        reservation1.setId(1l);


        Reservation reservation2 = new Reservation();
        reservation1.setId(2l);

        Ticket ticket1 = new Ticket();
        ticket1.setId(1l);
        ticket1.setNumberColumn(1);
        ticket1.setNumberRow(1);
        ticket1.setPrice(BigDecimal.valueOf(35));
        ticket1.setStatus(TicketStatus.RESERVED);
        ticket1.setVersion(0l);

        Ticket ticket2 = new Ticket();
        ticket2.setId(2l);
        ticket2.setNumberColumn(1);
        ticket2.setNumberRow(2);
        ticket2.setPrice(BigDecimal.valueOf(35));
        ticket2.setStatus(TicketStatus.FREE);
        ticket2.setVersion(0l);

        Ticket ticket3 = new Ticket();
        ticket3.setId(3l);
        ticket3.setNumberColumn(2);
        ticket3.setNumberRow(1);
        ticket3.setPrice(BigDecimal.valueOf(35));
        ticket3.setStatus(TicketStatus.FREE);
        ticket3.setVersion(0l);


        Ticket ticket4 = new Ticket();
        ticket4.setId(4l);
        ticket4.setNumberColumn(2);
        ticket4.setNumberRow(2);
        ticket4.setPrice(BigDecimal.valueOf(35));
        ticket4.setStatus(TicketStatus.FREE);
        ticket4.setVersion(0l);


        Ticket ticket5 = new Ticket();
        ticket5.setId(5l);
        ticket5.setNumberColumn(-1);
        ticket5.setNumberRow(-1);
        ticket5.setPrice(BigDecimal.valueOf(25));
        ticket5.setStatus(TicketStatus.RESERVED);
        ticket5.setVersion(0l);


        Ticket ticket6 = new Ticket();
        ticket6.setId(6l);
        ticket6.setNumberColumn(-1);
        ticket6.setNumberRow(-1);
        ticket6.setPrice(BigDecimal.valueOf(25));
        ticket6.setStatus(TicketStatus.RESERVED);
        ticket6.setVersion(0l);


        Ticket ticket7 = new Ticket();
        ticket7.setId(7l);
        ticket7.setNumberColumn(-1);
        ticket7.setNumberRow(-1);
        ticket7.setPrice(BigDecimal.valueOf(25));
        ticket7.setStatus(TicketStatus.PAID);
        ticket7.setVersion(0l);


        Ticket ticket8 = new Ticket();
        ticket8.setId(8l);
        ticket8.setNumberColumn(-1);
        ticket8.setNumberRow(-1);
        ticket8.setPrice(BigDecimal.valueOf(25));
        ticket8.setStatus(TicketStatus.FREE);
        ticket8.setVersion(0l);

        Ticket ticket9 = new Ticket();
        ticket9.setId(9l);
        ticket9.setNumberColumn(1);
        ticket9.setNumberRow(1);
        ticket9.setPrice(BigDecimal.valueOf(45));
        ticket9.setStatus(TicketStatus.RESERVED);
        ticket9.setVersion(0l);


        Ticket ticket10 = new Ticket();
        ticket10.setId(10l);
        ticket10.setNumberColumn(1);
        ticket10.setNumberRow(2);
        ticket10.setPrice(BigDecimal.valueOf(45));
        ticket10.setStatus(TicketStatus.RESERVED);
        ticket10.setVersion(0l);

        Ticket ticket11 = new Ticket();
        ticket11.setId(11l);
        ticket11.setNumberColumn(2);
        ticket11.setNumberRow(1);
        ticket11.setPrice(BigDecimal.valueOf(45));
        ticket11.setStatus(TicketStatus.RESERVED);
        ticket11.setVersion(0l);

        Ticket ticket12 = new Ticket();
        ticket12.setId(12l);
        ticket12.setNumberColumn(2);
        ticket12.setNumberRow(2);
        ticket12.setPrice(BigDecimal.valueOf(45));
        ticket12.setStatus(TicketStatus.FREE);
        ticket12.setVersion(0l);

        RegisteredUser registeredUser1 = new RegisteredUser();
        registeredUser1.setId(1l);
        registeredUser1.setEmail("kacjica+1@gmail.com");
        registeredUser1.setFirstName("Katarina");
        registeredUser1.setLastName("Tukelic");
        registeredUser1.setPassword("123456");
        registeredUser1.setIsVerified(true);
        registeredUser1.setUsername("kaca");



        /*
        ('REGISTERED_USER', 1, 'kacjica+1@gmail.com', 'Katarina', 'Tukelic', '123456', 'kaca', 1, null),
    ('ADMIN', 2, 'f.ivkovic16+1@gmail.com', 'Filip', 'Ivkovic', '123456', 'filip', 1, null);
         */
    }


    @Test
    public void checkStatusIsValid_ShouldPass_whenStatusIsValid(){
        ticketService.checkStatusIsValid(TicketStatus.FREE);
        ticketService.checkStatusIsValid(TicketStatus.PAID);
    }

    @Test
    public void checkStatusIsValid_ShouldThrowException_whenStatusIsInvalid(){
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Status is not valid");
        ticketService.checkStatusIsValid(TicketStatus.USED);
    }

    @Test
    public void acceptOrCancelReservations_ShouldThrowBadRequestException_whenReservationIsInvalid(){
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Reservation does not exist");
        Reservation reservation = new Reservation();
        reservation.setId(10l);
        ticketService.acceptOrCancelReservations(reservation.getId(),TicketStatus.PAID);
    }

    @Test
    public void acceptOrCancelReservations_ShouldThrowBadRequestException_whenItDoesntBelongToThatUser(){
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Reservation does not belong to that user!");
        Reservation reservation = new Reservation();
        reservation.setId(3l);

        ticketService.acceptOrCancelReservations(reservation.getId(), TicketStatus.PAID);
    }

    @Test
    public void acceptOrCancelReservations_ShouldReturnTrue_whenTicketsAndStatusAreValid(){

    }
}
