package com.siit.ticketist.integration.controller;

import com.siit.ticketist.dto.InitialReportDTO;
import com.siit.ticketist.dto.VenueReportDTO;
import com.siit.ticketist.security.TokenUtils;
import com.siit.ticketist.security.UserDetailsServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/reports.sql")
public class ReportsControllerTest {

    private static final String INITIAL_REPORT_PATH = "/api/reports";
    private static final String INVALID_VENUE_ID_PATH = "/api/reports/-1/TICKETS";
    private static final String INVALID_CRITERIA_PATH = "/api/reports/1/SOMETHINGNOTVALID";
    private static final String TICKETS_CRITERIA_PATH = "/api/reports/1/TICKETS";
    private static final String REVENUE_CRITERIA_PATH = "/api/reports/1/REVENUE";

    private HttpHeaders headers;
    private HttpEntity request;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private AuthenticationManager authenticationManager;

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
        request = new HttpEntity<>(headers);
    }

    @Test
    public void generateInitialReportEndpointShouldReturnInitialReportData(){
        ResponseEntity<InitialReportDTO> result = testRestTemplate.
                exchange(INITIAL_REPORT_PATH, HttpMethod.GET, request, InitialReportDTO.class);


//        List<EventReportDTO> lista = result.getBody().getEvents();
//        List<EventReportDTO> lista2 = new ArrayList(){{
//            add(new EventReportDTO("Event 1", "Spens", BigInteger.valueOf(2), new BigDecimal(80)));
//            add(new EventReportDTO("Event 2", "Spens", BigInteger.valueOf(1), new BigDecimal(50)));
//            add(new EventReportDTO("Event 3", "Novi Sad Fair", BigInteger.valueOf(0), new BigDecimal(0)));
//            add(new EventReportDTO("Event 4", "Startit Centar", BigInteger.valueOf(0), new BigDecimal(0)));
//        }};



        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertThat(result.getBody(), notNullValue());
        assertThat(result.getBody().getAllVenuesChart().size(), is(4));
        assertThat(result.getBody().getEvents(), hasSize(4));

//        assertThat(result.getBody().getEvents(), hasItems(
//                new EventReportDTO("Event 1", "Spens", BigInteger.valueOf(2), new BigDecimal(80)),
//                new EventReportDTO("Event 2", "Spens", BigInteger.valueOf(1), new BigDecimal(50)),
//                new EventReportDTO("Event 3", "Novi Sad Fair", BigInteger.valueOf(0), new BigDecimal(0)),
//                new EventReportDTO("Event 4", "Startit Centar", BigInteger.valueOf(0), new BigDecimal(0))
//        ));
    }

    @Test
    public void generateVenueReportEndpointShouldReturnNotFoundWhenVenueIdIsNotValid() {
        ResponseEntity<VenueReportDTO> result = testRestTemplate
                .exchange(INVALID_VENUE_ID_PATH, HttpMethod.GET, request, VenueReportDTO.class);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void generateVenueReportEndpointShouldReturnBadRequestWhenVenueIdIsNotValid() {
        ResponseEntity<VenueReportDTO> result = testRestTemplate
                .exchange(INVALID_CRITERIA_PATH, HttpMethod.GET, request, VenueReportDTO.class);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void generateVenueReportEndpointShouldPassWhenCriteriaIsTickets() {
        ResponseEntity<VenueReportDTO> result = testRestTemplate
                .exchange(TICKETS_CRITERIA_PATH, HttpMethod.GET, request, VenueReportDTO.class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void generateVenueReportEndpointShouldPassWhenCriteriaIsRevenue() {
        ResponseEntity<VenueReportDTO> result = testRestTemplate
                .exchange(REVENUE_CRITERIA_PATH, HttpMethod.GET, request, VenueReportDTO.class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
