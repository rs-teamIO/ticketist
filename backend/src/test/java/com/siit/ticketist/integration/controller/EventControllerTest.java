package com.siit.ticketist.integration.controller;

import com.siit.ticketist.dto.*;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;

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
import static org.hamcrest.Matchers.notNullValue;
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
        assertEquals(HttpStatus.OK, result.getStatusCode());
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

    private static final String MEDIA_FILES_URL = "/api/events/%s/media";

    private static final Long EVENT_ID = 10L;
    private static final Long NON_EXISING_EVENT_ID = 666L;
    private static final String VALID_IMAGE_NAME = "/images/03a4ee91-d9cc-4b39-b5bd-f28a2eb13d0d.jpeg";
    private static final String INVALID_IMAGE_NAME = "/images/9ce754e2-021f-4b8a-a7bf-142bba7e202b";

    @Test
    public void addMediaFiles_shouldSuccessfullyUploadMediaFile_whenContentTypeIsValid() {
        // Arrange
        LinkedMultiValueMap<String, Object> mediaFiles = new LinkedMultiValueMap<String, Object>();
        mediaFiles.add("mediaFiles", new ClassPathResource(VALID_IMAGE_NAME));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> request = new HttpEntity<>(mediaFiles, headers);

        // Act
        ResponseEntity<EventDTO> response = this.testRestTemplate
                .exchange(String.format(MEDIA_FILES_URL, EVENT_ID), HttpMethod.POST, request, EventDTO.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        assertThat(response.getBody().getMediaFiles(), hasSize(1));
    }

    @Test
    public void addMediaFiles_shouldThrowBadRequestException_whenContentTypeIsInvalid() {
        // Arrange
        LinkedMultiValueMap<String, Object> mediaFiles = new LinkedMultiValueMap<String, Object>();
        mediaFiles.add("mediaFiles", new ClassPathResource(INVALID_IMAGE_NAME));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> request = new HttpEntity<>(mediaFiles, headers);

        // Act
        ResponseEntity<ErrorResponse> response = this.testRestTemplate
                .exchange(String.format(MEDIA_FILES_URL, EVENT_ID), HttpMethod.POST, request, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        assertEquals("Invalid format of file 9ce754e2-021f-4b8a-a7bf-142bba7e202b.", response.getBody().getMessage());
    }

    @Test
    public void addMediaFiles_shouldThrowNotFoundException_whenEventDoesNotExist() {
        // Arrange
        LinkedMultiValueMap<String, Object> mediaFiles = new LinkedMultiValueMap<String, Object>();
        mediaFiles.add("mediaFiles", new ClassPathResource(INVALID_IMAGE_NAME));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> request = new HttpEntity<>(mediaFiles, headers);

        // Act
        ResponseEntity<ErrorResponse> response = this.testRestTemplate
                .exchange(String.format(MEDIA_FILES_URL, NON_EXISING_EVENT_ID), HttpMethod.POST, request, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        assertEquals("Event not found.", response.getBody().getMessage());
    }

    @Test
    public void getMediaFiles_shouldReturnMediaFiles_whenEventExists() {
        // Arrange
        HttpEntity<Object> request = new HttpEntity<>(headers);

        // Act
        ResponseEntity<Object> response = this.testRestTemplate
                .exchange(String.format(MEDIA_FILES_URL, EVENT_ID), HttpMethod.GET, request, Object.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
    }

    @Test
    public void getMediaFiles_shouldThrowNotFoundException_whenEventDoesNotExist() {
        // Arrange
        HttpEntity<Object> request = new HttpEntity<>(headers);

        // Act
        ResponseEntity<ErrorResponse> response = this.testRestTemplate
                .exchange(String.format(MEDIA_FILES_URL, NON_EXISING_EVENT_ID), HttpMethod.GET, request, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        assertEquals("Event not found.", response.getBody().getMessage());
    }

    private static final String EVENTS_URL = "/api/events/";

    private static final String NEW_EVENT_NAME = "New event name";
    private static final Category NEW_EVENT_CATEGORY = Category.SPORTS;
    private static final Date NEW_EVENT_RESERVATION_DEADLINE = new Date(System.currentTimeMillis() + 86400);
    private static final Integer NEW_EVENT_RESERVATION_LIMIT = Integer.valueOf(13);
    private static final String NEW_EVENT_DESCRIPTION = "New event description";

    @Test
    public void updateBasicInformation_shouldUpdateInformation_whenEventWithGivenIdExists() {
        // Arrange
        EventUpdateDTO dto = new EventUpdateDTO();
        dto.setName(NEW_EVENT_NAME);
        dto.setCategory(NEW_EVENT_CATEGORY);
        dto.setReservationDeadline(NEW_EVENT_RESERVATION_DEADLINE);
        dto.setReservationLimit(NEW_EVENT_RESERVATION_LIMIT);
        dto.setDescription(NEW_EVENT_DESCRIPTION);

        HttpEntity<EventUpdateDTO> request = new HttpEntity<EventUpdateDTO>(dto, headers);

        // Act
        ResponseEntity<EventDTO> response = this.testRestTemplate
                .exchange(EVENTS_URL.concat(EVENT_ID.toString()), HttpMethod.PUT, request, EventDTO.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        assertEquals(NEW_EVENT_NAME, response.getBody().getName());
        assertEquals(NEW_EVENT_CATEGORY, response.getBody().getCategory());
        assertEquals(NEW_EVENT_RESERVATION_DEADLINE, response.getBody().getReservationDeadline());
        assertEquals(NEW_EVENT_RESERVATION_LIMIT, response.getBody().getReservationLimit());
        assertEquals(NEW_EVENT_DESCRIPTION, response.getBody().getDescription());
    }

    @Test
    public void updateBasicInformation_shouldThrowNotFoundException_whenEventWithGivenIdDoesNotExists() {
        // Arrange
        EventUpdateDTO dto = new EventUpdateDTO();
        dto.setName(NEW_EVENT_NAME);
        dto.setCategory(NEW_EVENT_CATEGORY);
        dto.setReservationDeadline(NEW_EVENT_RESERVATION_DEADLINE);
        dto.setReservationLimit(NEW_EVENT_RESERVATION_LIMIT);
        dto.setDescription(NEW_EVENT_DESCRIPTION);

        HttpEntity<EventUpdateDTO> request = new HttpEntity<EventUpdateDTO>(dto, headers);

        // Act
        ResponseEntity<ErrorResponse> response = this.testRestTemplate
                .exchange(EVENTS_URL.concat(NON_EXISING_EVENT_ID.toString()), HttpMethod.PUT, request, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        assertEquals("Event not found.", response.getBody().getMessage());
    }

    @Test
    public void updateBasicInformation_shouldThrowBadRequestException_whenRequestContainsInvalidData() {
        // Arrange
        EventUpdateDTO dto = new EventUpdateDTO();

        HttpEntity<EventUpdateDTO> request = new HttpEntity<EventUpdateDTO>(dto, headers);

        // Act
        ResponseEntity<ErrorResponse> response = this.testRestTemplate
                .exchange(EVENTS_URL.concat(NON_EXISING_EVENT_ID.toString()), HttpMethod.PUT, request, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
    }

    private static final String CHANGE_TICKET_PRICE_URL = "/api/events/%s/event-sector/%s/ticket-price";

    private static final Long EVENT_SECTOR_ID = 5L;
    private static final Long EVENT_SECTOR_EVENT_ID = 11L;
    private static final Long NEW_TICKET_PRICE = 12345L;
    private static final Long INVALID_TICKET_PRICE = -12345L;
    private static final Long INVALID_EVENT_SECTOR_ID = 123423L;

    @Test
    public void changeEventSectorTicketPrice_shouldchangeTicketPrice_whenEventWithGivenIdExistsAndNewTicketPriceIsValid() {
        // Arrange
        BigDecimal dto = new BigDecimal(NEW_TICKET_PRICE);

        HttpEntity<BigDecimal> request = new HttpEntity<BigDecimal>(dto, headers);

        // Act
        ResponseEntity<EventDTO> response = this.testRestTemplate
                .exchange(String.format(CHANGE_TICKET_PRICE_URL, EVENT_SECTOR_EVENT_ID, EVENT_SECTOR_ID),
                        HttpMethod.PUT, request, EventDTO.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        EventSectorDTO eventSectorDto = response.getBody().getEventSectors().stream()
                .filter(eventSectorDTO -> eventSectorDTO.getId().equals(EVENT_SECTOR_ID))
                .findFirst().get();
        assertNotNull(eventSectorDto);
        assertEquals(NEW_TICKET_PRICE.longValue(), eventSectorDto.getTicketPrice().longValue());
    }

    @Test
    public void changeEventSectorTicketPrice_shouldThrowBadRequestException_whenNewTicketPriceIsInvalid() {
        // Arrange
        BigDecimal dto = new BigDecimal(INVALID_TICKET_PRICE);

        HttpEntity<BigDecimal> request = new HttpEntity<BigDecimal>(dto, headers);

        // Act
        ResponseEntity<ErrorResponse> response = this.testRestTemplate
                .exchange(String.format(CHANGE_TICKET_PRICE_URL, EVENT_SECTOR_EVENT_ID, EVENT_SECTOR_ID),
                        HttpMethod.PUT, request, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        assertEquals("Ticket price invalid.", response.getBody().getMessage());
    }

    @Test
    public void changeEventSectorTicketPrice_shouldThrowBadRequestException_whenEventDoesNotContainEventSectorWithGivenId() {
        // Arrange
        BigDecimal dto = new BigDecimal(NEW_TICKET_PRICE);

        HttpEntity<BigDecimal> request = new HttpEntity<BigDecimal>(dto, headers);

        // Act
        ResponseEntity<ErrorResponse> response = this.testRestTemplate
                .exchange(String.format(CHANGE_TICKET_PRICE_URL, EVENT_SECTOR_EVENT_ID, INVALID_EVENT_SECTOR_ID),
                        HttpMethod.PUT, request, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        assertEquals("Event doesn't contain sector with given ID.", response.getBody().getMessage());
    }

    private static final String CHANGE_STATUS_URL = "/api/events/%s/event-sector/%s/status";

    private static final Long EVENT_SECTOR_EVENT_ID_WITH_PURCHASED_TICKETS = 12L;
    private static final Long EVENT_SECTOR_ID_WITH_PURCHASED_TICKETS = 1L;

    @Test
    public void changeEventSectorStatus_shouldchangeStatus_whenEventWithGivenIdExists() {
        // Arrange
        Boolean dto = new Boolean(false);

        HttpEntity<Boolean> request = new HttpEntity<Boolean>(dto, headers);

        // Act
        ResponseEntity<EventDTO> response = this.testRestTemplate
                .exchange(String.format(CHANGE_STATUS_URL, EVENT_SECTOR_EVENT_ID, EVENT_SECTOR_ID),
                        HttpMethod.PUT, request, EventDTO.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        EventSectorDTO eventSectorDto = response.getBody().getEventSectors().stream()
                .filter(eventSectorDTO -> eventSectorDTO.getId().equals(EVENT_SECTOR_ID))
                .findFirst().get();
        assertNotNull(eventSectorDto);
    }

    @Test
    public void changeEventSectorStatus_shouldThrowBadRequestException_whenEventSectorContainsAtLeastOneReservedOrPurchasedTicket() {
        // Arrange
        Boolean dto = new Boolean(false);

        HttpEntity<Boolean> request = new HttpEntity<Boolean>(dto, headers);

        // Act
        ResponseEntity<ErrorResponse> response = this.testRestTemplate
                .exchange(String.format(CHANGE_STATUS_URL, EVENT_SECTOR_EVENT_ID_WITH_PURCHASED_TICKETS, EVENT_SECTOR_ID_WITH_PURCHASED_TICKETS),
                        HttpMethod.PUT, request, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        assertEquals("This event sector can't be disabled as there are purchased tickets.", response.getBody().getMessage());
    }

    @Test
    public void changeEventSectorStatus_shouldThrowBadRequestException_whenEventDoesNotContainEventSectorWithGivenId() {
        // Arrange
        Boolean dto = new Boolean(false);

        HttpEntity<Boolean> request = new HttpEntity<Boolean>(dto, headers);

        // Act
        ResponseEntity<ErrorResponse> response = this.testRestTemplate
                .exchange(String.format(CHANGE_STATUS_URL, EVENT_SECTOR_EVENT_ID, INVALID_EVENT_SECTOR_ID),
                        HttpMethod.PUT, request, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        assertEquals("Event doesn't contain sector with given ID.", response.getBody().getMessage());
    }

    private static final String CHANGE_CAPACITY_URL = "/api/events/%s/event-sector/%s/capacity";

    private static final Long EVENT_SECTOR_EVENT_ID_WITH_NON_NUMERATED_SEATS = 10L;
    private static final Long EVENT_SECTOR_ID_WITH_NON_NUMERATED_SEATS = 7L;

    @Test
    public void changeEventSectorCapacity_shouldUpdateCapacity_whenAllCriteriaIsSatisfied() {
        // Arrange
        Integer dto = new Integer(2);

        HttpEntity<Integer> request = new HttpEntity<Integer>(dto, headers);

        // Act
        ResponseEntity<EventDTO> response = this.testRestTemplate
                .exchange(String.format(CHANGE_CAPACITY_URL, EVENT_SECTOR_EVENT_ID_WITH_NON_NUMERATED_SEATS, EVENT_SECTOR_ID_WITH_NON_NUMERATED_SEATS),
                        HttpMethod.PUT, request, EventDTO.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
    }

    @Test
    public void changeEventSectorCapacity_shouldThrowBadRequestException_whenNewCapacityIsInvalid() {
        // Arrange
        Integer dto = new Integer(-1);

        HttpEntity<Integer> request = new HttpEntity<Integer>(dto, headers);

        // Act
        ResponseEntity<ErrorResponse> response = this.testRestTemplate
                .exchange(String.format(CHANGE_CAPACITY_URL, EVENT_SECTOR_EVENT_ID_WITH_NON_NUMERATED_SEATS, EVENT_SECTOR_ID_WITH_NON_NUMERATED_SEATS),
                        HttpMethod.PUT, request, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        assertEquals("Invalid capacity.", response.getBody().getMessage());
    }

    @Test
    public void changeEventSectorCapacity_shouldThrowBadRequestException_whenThereAreAlreadySoldTicketsForThatEventSector() {
        // Arrange
        Integer dto = new Integer(1);

        HttpEntity<Integer> request = new HttpEntity<Integer>(dto, headers);

        // Act
        ResponseEntity<ErrorResponse> response = this.testRestTemplate
                .exchange(String.format(CHANGE_CAPACITY_URL, 12L, 2L),
                        HttpMethod.PUT, request, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        assertEquals(String.format("Can't set the capacity to %d. There are 6 tickets already sold for this sector.", dto.intValue()), response.getBody().getMessage());
    }
}
