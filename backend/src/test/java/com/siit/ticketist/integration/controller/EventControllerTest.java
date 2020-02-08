package com.siit.ticketist.integration.controller;

import com.siit.ticketist.dto.EventDTO;
import com.siit.ticketist.dto.EventPageDTO;
import com.siit.ticketist.dto.EventSectorDTO;
import com.siit.ticketist.dto.SearchDTO;
import com.siit.ticketist.model.Category;
import com.siit.ticketist.security.TokenUtils;
import com.siit.ticketist.security.UserDetailsServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/data.sql")
public class EventControllerTest {

    private static final String GET_EVENTS_PAGED_PATH = "/api/events/paged?page={page}&size={size}";
    private static final String SEARCH_EVENTS_PATH = "/api/events/search?page={page}&size={size}";
    private static final String CANCEL_EVENT_PATH = "/api/events/cancel/{eventId}";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private AuthenticationManager authenticationManager;

    private HttpHeaders headers;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Before
    public void setUp() {
        final UserDetails userDetails = userDetailsService.loadUserByUsername("filip");
        final String token = tokenUtils.generateToken(userDetails);

        headers = new HttpHeaders();
        headers.set("Authorization", token);
    }


    @Test
    public void getEvents_ShouldPass_whenCalled(){

        ResponseEntity<List<EventDTO>> result = testRestTemplate.withBasicAuth("user2020", "123456")
                .exchange("/api/events", HttpMethod.GET, null, new ParameterizedTypeReference<List<EventDTO>>() {

                });

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(13, result.getBody().size());
    }

    @Test
    public void getEventsWithPagesEndpointShouldPassWhenCalledWithPageableObject() {
        ResponseEntity<EventPageDTO> result = testRestTemplate
                .getForEntity(GET_EVENTS_PAGED_PATH, EventPageDTO.class, 0, 8);
        assertEquals(result.getStatusCode(), HttpStatus.OK);
        assertNotNull(result.getBody());
        assertThat(result.getBody().getEvents(), hasSize(8));
        assertEquals(Long.valueOf(12), result.getBody().getTotalSize());
    }

    @Test
    public void getEvent_ShouldThrowNotFoundException_whenIdIsIncorrect(){

        HttpEntity request = new HttpEntity<>(headers);

        ResponseEntity<EventDTO> result = testRestTemplate.exchange("/api/events/100",HttpMethod.GET,request,EventDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody().getId());
    }


    @Test
    public void getEvent_ShouldPass_whenCalled(){

        HttpEntity request = new HttpEntity<>(headers);

        ResponseEntity<EventDTO> result = testRestTemplate.exchange("/api/events/1",HttpMethod.GET,request,EventDTO.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().getId().equals(1L));
    }

    @Test
    public void postEvent_ShouldThrowBadRequest_whenVenueIsUnavaliable(){

        EventDTO event = new EventDTO();
        event.setVenueId(1l);
        event.setCategory(Category.CULTURAL);
        event.setDescription("");
        event.setName("");
        event.setEventSectors(new HashSet<EventSectorDTO>());
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

        HttpEntity<EventDTO> request = new HttpEntity<>(event, headers);

        ResponseEntity<EventDTO> result = testRestTemplate.postForEntity("/api/events",request,EventDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody().getId());
    }

    @Test
    public void postEvent_ShouldThrowBadRequest_whenDatesAreInvalid(){

        EventDTO event = new EventDTO();
        event.setVenueId(1l);
        event.setCategory(Category.CULTURAL);
        event.setDescription("");
        event.setName("");
        event.setEventSectors(new HashSet<EventSectorDTO>());
        //'2020-03-14', '2020-03-15'
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        Date date2 = null;
        Date date3 = null;
        try {
            date = dateFormat.parse("20/03/2020");
            date2 = dateFormat.parse("18/03/2020");
            date3 = dateFormat.parse("24/03/2020");;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = date.getTime();
        long time2 = date2.getTime();
        long time3 = date3.getTime();
        event.setStartDate(new Timestamp(time));
        event.setEndDate(new Timestamp(time2));
        event.setReservationDeadline(new Timestamp(time3));

        HttpEntity<EventDTO> request = new HttpEntity<>(event, headers);

        ResponseEntity<EventDTO> result = testRestTemplate.postForEntity("/api/events",request,EventDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody().getId());
    }

    @Test
    public void postEvent_ShouldThrowBadRequest_whenThereAreNoEventSectors(){

        EventDTO event = new EventDTO();
        event.setVenueId(1l);
        event.setCategory(Category.CULTURAL);
        event.setDescription("");
        event.setName("");
        event.setEventSectors(new HashSet<EventSectorDTO>());
        //'2020-03-14', '2020-03-15'
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        Date date2 = null;
        Date date3 = null;
        try {
            date = dateFormat.parse("24/04/2020");
            date2 = dateFormat.parse("21/04/2020");
            date3 = dateFormat.parse("19/04/2020");;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = date.getTime();
        long time2 = date2.getTime();
        long time3 = date3.getTime();
        event.setStartDate(new Timestamp(time));
        event.setEndDate(new Timestamp(time2));
        event.setReservationDeadline(new Timestamp(time3));

        HttpEntity<EventDTO> request = new HttpEntity<>(event, headers);

        ResponseEntity<EventDTO> result = testRestTemplate.postForEntity("/api/events",request,EventDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody().getId());
    }

    @Test
    public void postEvent_ShouldThrowBadRequest_whenEventSectorSizeIsNull(){

        EventDTO event = new EventDTO();
        event.setVenueId(1l);
        event.setCategory(Category.CULTURAL);
        event.setDescription("");
        event.setName("");
        //'2020-03-14', '2020-03-15'
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        Date date2 = null;
        Date date3 = null;
        try {
            date = dateFormat.parse("24/04/2020");
            date2 = dateFormat.parse("21/04/2020");
            date3 = dateFormat.parse("19/04/2020");;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = date.getTime();
        long time2 = date2.getTime();
        long time3 = date3.getTime();
        event.setStartDate(new Timestamp(time));
        event.setEndDate(new Timestamp(time2));
        event.setReservationDeadline(new Timestamp(time3));

        Set<EventSectorDTO> eventSectors = new HashSet<>();
        EventSectorDTO eSector = new EventSectorDTO();
        eSector.setNumeratedSeats(false);
        eventSectors.add(eSector);
        event.setEventSectors(eventSectors);

        HttpEntity<EventDTO> request = new HttpEntity<>(event, headers);

        ResponseEntity<EventDTO> result = testRestTemplate.postForEntity("/api/events",request,EventDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody().getId());
    }

    @Test
    public void postEvent_ShouldThrowBadRequest_whenEventSectorSizeIsGreaterThanSectors(){

        EventDTO event = new EventDTO();
        event.setVenueId(1l);
        event.setCategory(Category.CULTURAL);
        event.setDescription("");
        event.setName("");
        //'2020-03-14', '2020-03-15'
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        Date date2 = null;
        Date date3 = null;
        try {
            date = dateFormat.parse("24/04/2020");
            date2 = dateFormat.parse("21/04/2020");
            date3 = dateFormat.parse("19/04/2020");;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = date.getTime();
        long time2 = date2.getTime();
        long time3 = date3.getTime();
        event.setStartDate(new Timestamp(time));
        event.setEndDate(new Timestamp(time2));
        event.setReservationDeadline(new Timestamp(time3));

        Set<EventSectorDTO> eventSectors = new HashSet<>();
        EventSectorDTO eSector = new EventSectorDTO();
        eSector.setNumeratedSeats(false);
        eSector.setCapacity(100);
        eSector.setSectorId(1L);
        eventSectors.add(eSector);
        event.setEventSectors(eventSectors);

        HttpEntity<EventDTO> request = new HttpEntity<>(event, headers);

        ResponseEntity<EventDTO> result = testRestTemplate.postForEntity("/api/events",request,EventDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody().getId());
    }

    @Test
    public void postEvent_ShouldThrowBadRequest_whenEventSectorIsNotFound(){

        EventDTO event = new EventDTO();
        event.setVenueId(1l);
        event.setCategory(Category.CULTURAL);
        event.setDescription("");
        event.setName("");
        //'2020-03-14', '2020-03-15'
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        Date date2 = null;
        Date date3 = null;
        try {
            date = dateFormat.parse("24/04/2020");
            date2 = dateFormat.parse("21/04/2020");
            date3 = dateFormat.parse("19/04/2020");;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = date.getTime();
        long time2 = date2.getTime();
        long time3 = date3.getTime();
        event.setStartDate(new Timestamp(time));
        event.setEndDate(new Timestamp(time2));
        event.setReservationDeadline(new Timestamp(time3));

        Set<EventSectorDTO> eventSectors = new HashSet<>();
        EventSectorDTO eSector = new EventSectorDTO();
        eSector.setNumeratedSeats(true);
        eSector.setSectorId(100L);
        eventSectors.add(eSector);
        event.setEventSectors(eventSectors);

        HttpEntity<EventDTO> request = new HttpEntity<>(event, headers);

        ResponseEntity<EventDTO> result = testRestTemplate.postForEntity("/api/events",request,EventDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody().getId());
    }


    @Test
    public void postEvent_ShouldPass_whenEverythingIsValid(){

        EventDTO event = new EventDTO();
        event.setVenueId(1l);
        event.setCategory(Category.CULTURAL);
        event.setDescription("this is my first event");
        event.setName("EXIT");
        event.setReservationLimit(3);
        event.setMediaFiles(new HashSet<>());
        //'2020-03-14', '2020-03-15'
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        Date date2 = null;
        Date date3 = null;
        try {
            date = dateFormat.parse("21/04/2021");
            date2 = dateFormat.parse("24/04/2021");
            date3 = dateFormat.parse("19/04/2021");;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = date.getTime();
        long time2 = date2.getTime();
        long time3 = date3.getTime();
        event.setStartDate(new Timestamp(time));
        event.setEndDate(new Timestamp(time2));
        event.setReservationDeadline(new Timestamp(time3));

        Set<EventSectorDTO> eventSectors = new HashSet<>();

        EventSectorDTO eSector = new EventSectorDTO();
        eSector.setNumeratedSeats(true);
        eSector.setSectorId(1L);
        eSector.setTicketPrice(BigDecimal.valueOf(100));

        eventSectors.add(eSector);
        event.setEventSectors(eventSectors);

        HttpEntity<EventDTO> request = new HttpEntity<>(event, headers);

        ResponseEntity<EventDTO> result = testRestTemplate.postForEntity("/api/events",request,EventDTO.class);
        System.out.println("\n" + result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody().getId());
    }

    @Test
    public void searchEndpoint_ShouldPassWhenAllSearchFieldsAreEmpty() {
        SearchDTO emptyDTO = new SearchDTO();
        ResponseEntity<EventPageDTO> result = testRestTemplate
                .postForEntity(SEARCH_EVENTS_PATH, emptyDTO, EventPageDTO.class, 0, 8);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertThat(result.getBody().getEvents(), hasSize(8));
        assertEquals(Long.valueOf(12), result.getBody().getTotalSize());
    }

    @Test
    public void searchEndpoint_ShouldPassWhenSearchFieldsArePartiallyOrFullyFilled() {
        //Partial
        SearchDTO searchDTO = new SearchDTO("", "ENTERTAINMENT", "Spens", null, null);
        ResponseEntity<EventPageDTO> result = testRestTemplate
                .postForEntity(SEARCH_EVENTS_PATH, searchDTO, EventPageDTO.class, 0, 8);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertThat(result.getBody().getEvents(), hasSize(5));
        assertEquals(Long.valueOf(5), result.getBody().getTotalSize());

        //Full
        searchDTO = new SearchDTO("event", "ENTERTAINMENT", "Spens", 1594677600000L, 1594764000000L);
        result = testRestTemplate.postForEntity(SEARCH_EVENTS_PATH, searchDTO, EventPageDTO.class, 0, 8);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertThat(result.getBody().getEvents(), hasSize(1));
        assertEquals(Long.valueOf(1), result.getBody().getTotalSize());
    }

    @Test
    public void cancelEventEndpointShouldReturnNotFoundWhenVenueWithGivenIdIsNotFound() {
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<EventDTO> result = testRestTemplate
                .exchange(CANCEL_EVENT_PATH, HttpMethod.GET, request, EventDTO.class, -1L);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void cancelEventEndpointShouldReturnBadRequestWhenTheEventHasPassed() {
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<EventDTO> result = testRestTemplate
                .exchange(CANCEL_EVENT_PATH, HttpMethod.GET, request, EventDTO.class, 1L);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void cancelEventEndpointShouldReturnForbiddenWhenTheEventIsAlreadyCancelled() {
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<EventDTO> result = testRestTemplate
                .exchange(CANCEL_EVENT_PATH, HttpMethod.GET, request, EventDTO.class, 9L);
        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    }

    @Test
    public void cancelEventEndpointShouldPassWhenEverythingIsValid() {
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<EventDTO> result = testRestTemplate
                .exchange(CANCEL_EVENT_PATH, HttpMethod.GET, request, EventDTO.class, 8L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(Long.valueOf(8), result.getBody().getId());
    }


}
