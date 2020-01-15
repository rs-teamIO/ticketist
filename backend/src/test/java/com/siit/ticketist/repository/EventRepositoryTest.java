package com.siit.ticketist.repository;


import com.siit.ticketist.model.Event;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@Sql("/reports.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void findAllEventsReport_ReturnsListOfEventsItsVenueSoldTicketsAndTotalRevenue() {
        List<Object[]> events = eventRepository.findAllEventsReport();
        assertThat(events, hasSize(4));
        events.stream().forEach(obj -> {
            if (((String) obj[0]).equals("Event 1")) {
                assertEquals("Two sold tickets for 'Event 1'", BigInteger.valueOf(2), (BigInteger) obj[2]);
                assertThat("Total revenue for 'Event 1' is 80", new BigDecimal(80), comparesEqualTo((BigDecimal) obj[3]));
            } else if (((String) obj[0]).equals("Event 2")) {
                assertEquals("One sold ticket for 'Event 2'", BigInteger.valueOf(1), (BigInteger) obj[2]);
                assertThat("Total revenue for 'Event 2' is 50", new BigDecimal(50), comparesEqualTo((BigDecimal) obj[3]));
            } else {
                assertEquals("No sold tickets", BigInteger.valueOf(0), (BigInteger) obj[2]);
                assertThat("No revenue", new BigDecimal(0), comparesEqualTo((BigDecimal) obj[3]));
            }
        });
    }

    //---------------------------------------------------------------------------------------------
    @Test
    public void findAllEventReportsByVenue_ReturnsListOfEventsSoldTicketsAndTotalRevenueForWantedVenue() {
        List<Object[]> events = eventRepository.findAllEventReportsByVenue(Long.valueOf(1));
        assertThat(events, hasSize(2));
        events.stream().forEach(obj -> {
            if (((String) obj[0]).equals("Event 1")) {
                assertEquals(BigInteger.valueOf(2), (BigInteger) obj[2]);
                assertThat(new BigDecimal(80), comparesEqualTo((BigDecimal) obj[3]));
            } else {
                assertEquals("Event 2", (String) obj[0]);
                assertEquals(BigInteger.valueOf(1), (BigInteger) obj[2]);
                assertThat(new BigDecimal(50), comparesEqualTo((BigDecimal) obj[3]));
            }
        });
    }

    @Test
    public void findAllEventReportsByVenue_ReturnsEmptyListWhenVenueDoesNotHaveAnyEvents() {
        List<Object[]> events = eventRepository.findAllEventReportsByVenue(Long.valueOf(4));
        assertTrue(events.isEmpty());
    }

    @Test
    public void findAllEventReportsByVenue_ReturnsEmptyListWhenVenueWithGivenIdDoesNotExist() {
        List<Object[]> events = eventRepository.findAllEventReportsByVenue(Long.valueOf(-1));
        assertTrue(events.isEmpty());
    }

    //---------------------------------------------------------------------------------------------
    @Test
    public void getVenueTicketsReport_ReturnsListOfMonthsWithSoldTicketsWhenThereAreSoldTicketsForWantedVenueInTheLastYear() {
        /*
            If event has sold tickets, but happened over a year ago, it will not be considered for the report
            If a month has 0 sold tickets, it will not be in the list
         */
        List<Object[]> venues = eventRepository.getVenueTicketsReport(Long.valueOf(1));
        assertThat(venues, hasSize(1));
        assertEquals("Month number (1-12) with more than 0 sold tickets", 11, (int) venues.get(0)[0]);
        assertEquals("Number of sold tickets for that month", BigInteger.valueOf(2), (BigInteger) venues.get(0)[1]);
    }

    @Test
    public void getVenueTicketsReport_ReturnsEmptyListWhenVenueDoesNotHaveAnyEvents() {
        List<Object[]> venues = eventRepository.getVenueTicketsReport(Long.valueOf(4));
        assertTrue(venues.isEmpty());
    }

    @Test
    public void getVenueTicketsReport_ReturnsEmptyListWhenVenueWithGivenIdDoesNotExist() {
        List<Object[]> venues = eventRepository.getVenueTicketsReport(Long.valueOf(-1));
        assertTrue(venues.isEmpty());
    }

    //---------------------------------------------------------------------------------------------
    @Test
    public void getVenueRevenueReport_ReturnsListOfMonthsWithItsRevenueWhenThereAreSoldTicketsForWantedVenueInTheLastYear() {
        /*
            Any earnings made over a year ago will not be considered for the report
            If a month has no revenue, it will not be in the list
         */
        List<Object[]> venues = eventRepository.getVenueRevenueReport(Long.valueOf(1));
        assertThat(venues, hasSize(1));
        assertEquals("Month number (1-12) with revenue biger than 0", 11, (int) venues.get(0)[0]);
        assertThat("Accumulated earnings for that month", new BigDecimal(80), comparesEqualTo((BigDecimal) venues.get(0)[1]));
    }

    @Test
    public void getVenueRevenueReport_ReturnsEmptyListWhenVenueDoesNotHaveAnyEvents() {
        List<Object[]> venues = eventRepository.getVenueTicketsReport(Long.valueOf(4));
        assertTrue(venues.isEmpty());
    }

    @Test
    public void getVenueRevenueReport_ReturnsEmptyListWhenVenueWithGivenIdDoesNotExist() {
        List<Object[]> venues = eventRepository.getVenueTicketsReport(Long.valueOf(-1));
        assertTrue(venues.isEmpty());
    }

    //---------------------------------------------------------------------------------------------
    @Test
    public void searchWithEmptyParamsShouldReturnAllEvents() {
        List<Event> events = eventRepository.search("", "", "", null, null, PageRequest.of(0, 8));
        assertThat("All events are returned", events, hasSize(4));
        assertAllEventsInList(events);
    }

    @Test
    public void searchWithNameParamShouldReturnEventsWhichNameContainsWantedParam() {
        //Full name
        List<Event> events = eventRepository.search("event 1", "", "", null, null, PageRequest.of(0, 8));
        assertThat(events, hasSize(1));
        assertThat(events, containsInAnyOrder(hasProperty("name", is("Event 1"))));

        //Partial name
        events = eventRepository.search("event", "", "", null, null, PageRequest.of(0, 8));
        assertThat(events, hasSize(4));
        assertAllEventsInList(events);
    }

    @Test
    public void searchWithCategoryParamShouldReturnEventsWhichCategoryContainsWantedParam() {
        //Full name
        List<Event> events = eventRepository.search("", "entertainment", "", null, null, PageRequest.of(0, 8));
        assertThat(events, hasSize(1));
        assertThat(events, containsInAnyOrder(
                hasProperty("name", is("Event 3"))
        ));

        //Partial name
        events = eventRepository.search("", "enter", "", null, null, PageRequest.of(0, 8));
        assertThat(events, hasSize(1));
        assertThat(events, containsInAnyOrder(
                hasProperty("name", is("Event 3"))
        ));
    }

    @Test
    public void searchWithVenueNameParamShouldReturnEventsWhichVenueNameContainsWantedParam() {
        //Full name
        List<Event> events = eventRepository.search("", "", "spens", null, null, PageRequest.of(0, 8));
        assertThat(events, hasSize(2));
        assertThat(events, containsInAnyOrder(
                hasProperty("name", is("Event 1")),
                hasProperty("name", is("Event 2"))
        ));

        //Partial name
        events = eventRepository.search("", "", "sp", null, null, PageRequest.of(0, 8));
        assertThat(events, hasSize(2));
        assertThat(events, containsInAnyOrder(
                hasProperty("name", is("Event 1")),
                hasProperty("name", is("Event 2"))
        ));
    }

    @Test
    public void searchWithStartDateParamShouldReturnEventsWhichStartDateIsBiggerOrEqualToWantedDateParam() throws ParseException {
        //Full name
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(sdf.format(new Date(Long.valueOf("1573516800000")))); // 2019-11-12 00:00:00
        List<Event> events = eventRepository.search("", "", "", date, null, PageRequest.of(0, 8));
        assertThat(events, hasSize(2));
        assertThat(events, containsInAnyOrder(
                hasProperty("name", is("Event 3")),
                hasProperty("name", is("Event 4"))
        ));
    }

    @Test
    public void searchWithEndDateParamShouldReturnEventsWhichStartDateIsLessOrEqualToWantedDateParam() throws ParseException {
        //Full name
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(sdf.format(new Date(Long.valueOf("1583362800000")))); // 2019-03-05 00:00:00
        List<Event> events = eventRepository.search("", "", "", null, date, PageRequest.of(0, 8));
        assertThat(events, hasSize(4));
        assertAllEventsInList(events);
    }

    //Combined Search
    @Test
    public void searchWithAllParamsShouldReturnEventsWhichPropertiesSatisfyAllWantedSearchParams() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = sdf.parse(sdf.format(new Date(Long.valueOf("1573340400000")))); // 2019-11-10 00:00:00
        Date endDate = sdf.parse(sdf.format(new Date(Long.valueOf("1583362800000")))); // 2019-03-05 00:00:00
        List<Event> events = eventRepository.search("event", "sports", "spens", startDate, endDate, PageRequest.of(0, 8));
        assertThat("Only 'Event 1' satisfies every search param", events, hasSize(1));
        assertThat(events, containsInAnyOrder(
                hasProperty("name", is("Event 1"))
        ));
    }

    @Test
    public void searchWithParamsThatNoneOfTheEventsSatifyShouldReturnEmptyList(){
        List<Event> events = eventRepository.search("event", "cultural", "spens", null, null, PageRequest.of(0, 8));
        assertTrue(events.isEmpty());
    }

    private void assertAllEventsInList(List<Event> events) {
        assertThat(events, containsInAnyOrder(
                hasProperty("name", is("Event 1")),
                hasProperty("name", is("Event 2")),
                hasProperty("name", is("Event 3")),
                hasProperty("name", is("Event 4"))
        ));
    }

}
