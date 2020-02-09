package com.siit.ticketist.unit.service;

import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.exceptions.NotFoundException;
import com.siit.ticketist.model.Venue;
import com.siit.ticketist.repository.EventRepository;
import com.siit.ticketist.repository.VenueRepository;
import com.siit.ticketist.service.ReportService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class ReportServiceTest {

    private static final Long INVALID_VENUE_ID = Long.valueOf(-1);
    private static final Long VENUE_TICKETS_RESERVATIONS = Long.valueOf(1);
    private static final Long VENUE_NO_EVENTS = Long.valueOf(4);

    private static final String CRITERIA_TICKETS = "TICKETS";
    private static final String CRITERIA_REVENUE = "REVENUE";
    private static final String INVALID_CRITERIA = "This criteria is not valid";

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private ReportService reportService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getAllVenuRevenuesShouldReturnMapOfVenuesAndTheirCorrespondingRevenues() {
        List<Object[]> venues = new ArrayList() {{
            add(new Object[]{"Spens", new BigDecimal(130)});
            add(new Object[]{"Novi Sad Fair", new BigDecimal(0)});
            add(new Object[]{"Startit Centar", new BigDecimal(0)});
            add(new Object[]{"Venue without events", new BigDecimal(0)});
        }};
        when(venueRepository.getAllVenueRevenues()).thenReturn(venues);

        Map<String, BigDecimal> venueMap = reportService.getAllVenueRevenues();
        assertThat(venueMap.size(), is(4));
        assertThat(venueMap, allOf(
                hasEntry(equalTo("Spens"), comparesEqualTo(BigDecimal.valueOf(130))),
                hasEntry(equalTo("Novi Sad Fair"), comparesEqualTo(BigDecimal.valueOf(0))),
                hasEntry(equalTo("Startit Centar"), comparesEqualTo(BigDecimal.valueOf(0))),
                hasEntry(equalTo("Venue without events"), comparesEqualTo(BigDecimal.valueOf(0)))));
    }

    @Test
    public void getVenueChartShouldThrowNotFoundExceptionWhenVenueWithGivenIdDoesNotExist() {
        exceptionRule.expect(NotFoundException.class);
        when(venueRepository.findById(INVALID_VENUE_ID)).thenReturn(Optional.empty());
        reportService.getVenueChart(CRITERIA_TICKETS, VENUE_TICKETS_RESERVATIONS);
    }

    @Test
    public void getVenueChartShouldThrowBadRequestExceptionWhenGivenCriteriaIsNotValid() {
        exceptionRule.expect(BadRequestException.class);
        Venue venue = new Venue();
        venue.setId(VENUE_TICKETS_RESERVATIONS);

        when(venueRepository.findById(VENUE_TICKETS_RESERVATIONS)).thenReturn(Optional.of(venue));
        reportService.getVenueChart(INVALID_CRITERIA, VENUE_TICKETS_RESERVATIONS);
    }

    @Test
    public void getVenueChartShouldReturnMapOfMonthsAndNumberOfTicketsSoldInThatMonthWhenCriteriaIsTickets() {
        Venue venue = new Venue();
        venue.setId(VENUE_TICKETS_RESERVATIONS);
        when(venueRepository.findById(VENUE_TICKETS_RESERVATIONS)).thenReturn(Optional.of(venue));

        List<Object[]> months = new ArrayList() {{
            add(new Object[]{11, BigInteger.valueOf(2)});
        }};
        when(eventRepository.getVenueTicketsReport(VENUE_TICKETS_RESERVATIONS)).thenReturn(months);

        Map<Integer, Float> monthsMap = reportService.getVenueChart(CRITERIA_TICKETS, VENUE_TICKETS_RESERVATIONS);
        assertThat(monthsMap.size(), is(1));
        assertThat(monthsMap, hasEntry(equalTo(11), equalTo(2F)));
    }

    @Test
    public void getVenueChartShouldReturnEmptyMapWhenWantedVenueHasNoEventsAndCriteriaIsTickets() {
        Venue venue = new Venue();
        venue.setId(VENUE_NO_EVENTS);
        when(venueRepository.findById(VENUE_NO_EVENTS)).thenReturn(Optional.of(venue));

        List<Object[]> months = new ArrayList();
        when(eventRepository.getVenueTicketsReport(VENUE_NO_EVENTS)).thenReturn(months);

        assertThat(reportService.getVenueChart(CRITERIA_TICKETS, VENUE_NO_EVENTS).size(), is(0));
    }

    @Test
    public void getVenueChartShouldReturnMapOfMonthsAndThatMonthsRevenueWhenCriteriaIsRevenue() {
        Venue venue = new Venue();
        venue.setId(VENUE_TICKETS_RESERVATIONS);
        when(venueRepository.findById(VENUE_TICKETS_RESERVATIONS)).thenReturn(Optional.of(venue));

        List<Object[]> months = new ArrayList() {{
            add(new Object[]{11, BigDecimal.valueOf(80)});
        }};
        when(eventRepository.getVenueRevenueReport(VENUE_TICKETS_RESERVATIONS)).thenReturn(months);

        Map<Integer, Float> monthsMap = reportService.getVenueChart(CRITERIA_REVENUE, VENUE_TICKETS_RESERVATIONS);
        assertThat(monthsMap.size(), is(1));
        assertThat(monthsMap, hasEntry(equalTo(11), equalTo(80F)));
    }

    @Test
    public void getVenueChartShouldReturnEmptyMapWhenWantedVenueHasNoEventsAndCriteriaIsRevenue() {
        Venue venue = new Venue();
        venue.setId(VENUE_NO_EVENTS);
        when(venueRepository.findById(VENUE_NO_EVENTS)).thenReturn(Optional.of(venue));

        List<Object[]> months = new ArrayList();
        when(eventRepository.getVenueRevenueReport(VENUE_NO_EVENTS)).thenReturn(months);

        assertThat(reportService.getVenueChart(CRITERIA_REVENUE, VENUE_NO_EVENTS).size(), is(0));
    }


}
