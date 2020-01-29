package com.siit.ticketist.integration.service;

import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.exceptions.NotFoundException;
import com.siit.ticketist.service.ReportService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/reports.sql")
public class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    private static final Long VENUE_TICKETS_RESERVATIONS = Long.valueOf(1);
    private static final Long VENUE_RESERVATIONS = Long.valueOf(2);
    private static final Long VENUE_NO_TICKETS_RESERVATIONS = Long.valueOf(3);
    private static final Long VENUE_NO_EVENTS = Long.valueOf(4);

    private static final String CRITERIA_TICKETS = "TICKETS";
    private static final String CRITERIA_REVENUE = "REVENUE";
    private static final String INVVALID_CRITERIA = "This criteria is not valid";

    @Test
    public void getAllVenueRevenuesShouldReturnMapOfVenuesAndTheirCorrespondingRevenues() {
        Map<String, BigDecimal> venueMap = (HashMap<String, BigDecimal>) reportService.getAllVenueRevenues();
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
        reportService.getVenueChart(CRITERIA_TICKETS, Long.valueOf(-1));
    }

    @Test
    public void getVenueChartShouldThrowBadRequestExceptionWhenGivenCriteriaIsNotValid() {
        exceptionRule.expect(BadRequestException.class);
        reportService.getVenueChart(INVVALID_CRITERIA, Long.valueOf(1));
    }

    @Test
    public void getVenueChartShouldReturnMapOfMonthsAndNumberOfTicketsSoldInThatMonthWhenCriteriaIsTickets() {
        Map<Integer, Float> monthsMap = reportService.getVenueChart(CRITERIA_TICKETS, Long.valueOf(1));
        assertThat("Only november (11) has more than 0 tickets sold",
                monthsMap, hasEntry(equalTo(11), equalTo(Float.valueOf(2))));
    }

    @Test
    public void getVenueChartShouldReturnEmptyMapWhenWantedVenueHasNoEventsAndCriteriaIsTickets() {
        assertThat(reportService.getVenueChart(CRITERIA_TICKETS, Long.valueOf(4)).size(), is(0));
    }


    @Test
    public void getVenueChartShouldReturnMapOfMonthsAndThatMonthsRevenueWhenCriteriaIsRevenue() {
        Map<Integer, Float> monthsMap = reportService.getVenueChart(CRITERIA_REVENUE, Long.valueOf(1));
        assertThat("Only november (11) has revenue bigger than 0",
                monthsMap, hasEntry(equalTo(11), equalTo(Float.valueOf(80))));
    }

    @Test
    public void getVenueChartShouldReturnEmptyMapWhenWantedVenueHasNoEventsAndCriteriaIsRevenue() {
        assertThat(reportService.getVenueChart(CRITERIA_REVENUE, Long.valueOf(4)).size(), is(0));
    }
}
