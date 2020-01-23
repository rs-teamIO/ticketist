package com.siit.ticketist.service;

import com.siit.ticketist.dto.VenueBasicDTO;
import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.exceptions.NotFoundException;
import com.siit.ticketist.model.Sector;
import com.siit.ticketist.model.Venue;
import com.siit.ticketist.repository.VenueRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/data.sql")
public class VenueServiceUnitTest {

    @Autowired
    private VenueService venueService;

    @MockBean
    private VenueRepository venueRepositoryMock;

    private Venue venue;

    private Set<Sector> sectors;

    @Before
    public void setupVenueRepositoryMock() {
        Long sectorID = 1L;
        Sector sector = new Sector(sectorID, "Sever", 2, 2, 4, 1, 1);
        sectors = new HashSet<>();
        sectors.add(sector);

        Long venueID = 1L;
        venue = new Venue(venueID, "SPENS", true, "Puskinova 12", "Novi Sad", 15.0, 15.0, sectors, new HashSet<>());
        List<Venue> venues = new ArrayList<>();
        venues.add(venue);

        when(venueRepositoryMock.findById(venueID))
                .thenReturn(Optional.of(venue));

        when(venueRepositoryMock.findAll())
                .thenReturn(venues);

    }

    // Try to find venue by id, but method throws NotFoundException because
    // wanted venue does not exist
    @Test(expected = NotFoundException.class)
    public void findOne_throwsNotFoundException_whenWantedVenueDoesNotExist2() {
        Long wantedID = 2L;
        venueService.findOne(wantedID);
    }

    // Successfully find venue by id
    @Test
    public void findOne_successfullyGetVenue() {
        Long wantedID = 1L;
        Venue venue = venueService.findOne(wantedID);

        assertEquals("Venue's id equals to wanted id", wantedID, venue.getId());
        assertEquals("Venue's name is correct", "SPENS", venue.getName());
        assertEquals("Venue's active status is correct", true, venue.getIsActive());
        assertEquals("Venue's street is correct", "Puskinova 12", venue.getStreet());
        assertEquals("Venue's city is correct", "Novi Sad", venue.getCity());
        assertEquals("Venue's latitude is correct", new Double("15.0"), venue.getLatitude());
        assertEquals("Venue's longitude is correct", new Double("15.0"), venue.getLongitude());
        assertSame("Venue's sector size is correct", 1, venue.getSectors().size());
    }


    // Try to save venue, but method throws BadRequestException because
    // venue with entered name/city/street triple already exists
    @Test(expected = BadRequestException.class)
    public void saveVenue_throwsBadRequestException_whenNameStreetAndCityTripleAlreadyExists() {
        Long sectorID = 2L;
        Sector sector = new Sector(sectorID, "Sever", 2, 2, 4, 1, 1);
        Set<Sector> sectors = new HashSet<>();
        sectors.add(sector);
        Venue newVenue = new Venue(null, "SPENS", true, "Puskinova 12", "Novi Sad", 15.0, 15.0, sectors, new HashSet<>());
        venueService.save(newVenue);
    }

    // Try to save venue, but method throws BadRequestException because
    // new venue does not have any sector included
    @Test(expected = BadRequestException.class)
    public void saveVenue_throwsBadRequestException_whenNewVenueDoesNotHaveAnySectorIncluded() {
        Venue newVenue = new Venue(null, "ARENA", true, "Gogoljeva 15", "Novi Sad", 15.0, 15.0, new HashSet<>(), new HashSet<>());
        venueService.save(newVenue);
    }

    // Try to save venue, but method throws BadRequestException because
    // new venue has sectors which overlap
    @Test(expected = BadRequestException.class)
    public void saveVenue_throwsBadRequestException_whenNewVenueHasSectorsWhichOverlap() {
        Sector sector1 = new Sector(2L, "Sever", 2, 2, 4, 1, 1);
        Sector sector2 = new Sector(3L, "Jug", 2, 2, 4, 2, 2);
        Set<Sector> sectors = new HashSet<>();
        sectors.add(sector1);
        sectors.add(sector2);

        Venue newVenue = new Venue(null, "ARENA", true, "Gogoljeva 15", "Novi Sad", 15.0, 15.0, sectors, new HashSet<>());
        venueService.save(newVenue);
    }

    // Try to save venue, but method throws BadRequestException because
    // new venue has sectors with the same name
    @Test(expected = BadRequestException.class)
    public void saveVenue_throwsBadRequestException_whenNewVenueHasSectorsWithTheSameName() {
        Sector sector1 = new Sector(2L, "Sever", 2, 2, 4, 1, 1);
        Sector sector2 = new Sector(3L, "Sever", 2, 2, 4, 3, 3);
        Set<Sector> sectors = new HashSet<>();
        sectors.add(sector1);
        sectors.add(sector2);

        Venue newVenue = new Venue(null, "ARENA", true, "Gogoljeva 15", "Novi Sad", 15.0, 15.0, sectors, new HashSet<>());
        venueService.save(newVenue);
    }

    // Successfully save new venue
    @Test
    public void saveVenue_successful() {
        Sector sector1 = new Sector(2L, "Sever", 2, 2, 4, 1, 1);
        Sector sector2 = new Sector(3L, "Jug", 2, 2, 4, 3, 3);
        Set<Sector> sectors = new HashSet<>();
        sectors.add(sector1);
        sectors.add(sector2);

        Venue newVenue = new Venue(null, "ARENA", true, "Gogoljeva 15", "Novi Sad", 15.0, 15.0, sectors, new HashSet<>());
        Venue savedVenueMock = new Venue(2L, "ARENA", true, "Gogoljeva 15", "Novi Sad", 15.0, 15.0, sectors, new HashSet<>());

        when(venueRepositoryMock.save(newVenue))
                .thenReturn(savedVenueMock);

        Venue savedVenue = venueService.save(newVenue);
        assertSame("Venue's id equals to wanted id", 2L, savedVenue.getId());
        assertEquals("Venue's name is correct", "ARENA", savedVenue.getName());
        assertEquals("Venue's active status is correct", true, savedVenue.getIsActive());
        assertEquals("Venue's street is correct", "Gogoljeva 15", savedVenue.getStreet());
        assertEquals("Venue's city is correct", "Novi Sad", savedVenue.getCity());
        assertEquals("Venue's latitude is correct", new Double("15.0"), savedVenue.getLatitude());
        assertEquals("Venue's longitude is correct", new Double("15.0"), savedVenue.getLongitude());
        assertSame("Venue's sector size is correct", 2, savedVenue.getSectors().size());
    }

    @Test(expected = NotFoundException.class)
    public void checkIsActive_throwsNotFoundException_whenVenueWithWantedIdDoesNotExist() {
        Long venueID = 2L;
        venueService.checkIsActive(venueID);
    }

    @Test
    public void checkIsActive_successfulWhenTrue() {
        Long venueID = 1L;
        boolean checkIsActive = venueService.checkIsActive(venueID);
        assertTrue("Status is true", checkIsActive);
    }

    @Test
    public void checkIsActive_successfulWhenFalse() {
        Long venueID = 1L;
        Venue checkVenue = new Venue(venueID, "SPENS", false, "Puskinova 12", "Novi Sad", 15.0, 15.0, sectors, new HashSet<>());

        when(venueRepositoryMock.findById(venueID))
                .thenReturn(Optional.of(checkVenue));

        boolean checkIsActive = venueService.checkIsActive(venueID);
        assertFalse("Status is false", checkIsActive);
    }

    @Test(expected = NotFoundException.class)
    public void changeActiveStatus_throwsNotFoundException_whenVenueWithWantedIdDoesNotExist() {
        Long venueID = 2L;
        venueService.changeActiveStatus(venueID);
    }

    @Test
    public void changeActiveStatus_successful() {
        Long venueID = 1L;

        assertTrue("venue status is true", venue.getIsActive());
        venueService.changeActiveStatus(venueID);
        assertFalse("venue status is false", venue.getIsActive());
        venueService.changeActiveStatus(venueID);
        assertTrue("venue status is again true", venue.getIsActive());
    }

    @Test(expected = NotFoundException.class)
    public void updateVenue_throwsNotFoundException_whenVenueWithWantedIdDoesNotExist() {
        Long venueID = 2L;
        VenueBasicDTO basicVenue = new VenueBasicDTO("SPENS", "Puskinova 12", "Novi Sad", 12.0, 15.0);

        venueService.updateVenue(basicVenue, venueID);
    }

    @Test(expected = BadRequestException.class)
    public void updateVenue_throwsBadRequestException__whenNameStreetAndCityTripleAlreadyExists() {
        Long venueID = 1L;
        VenueBasicDTO basicVenue = new VenueBasicDTO("SPENS", "Puskinova 12", "Novi Sad", 12.0, 15.0);

        venueService.updateVenue(basicVenue, venueID);
    }

    @Test
    public void updateVenue_Successful() {
        Long venueID = 1L;
        String newVenueName = "ARENA";
        String newVenueStreet = "Tolstojeva 12";
        String newVenueCity = "Novi Sad";
        Double newLat = 12.0;
        Double newLon = 16.0;

        VenueBasicDTO basicVenue = new VenueBasicDTO(newVenueName, newVenueStreet, newVenueCity, newLat, newLon);

        assertEquals("Venue's name is correct", "SPENS", venue.getName());
        assertEquals("Venue's street is correct", "Puskinova 12", venue.getStreet());
        assertEquals("Venue's city is correct", "Novi Sad", venue.getCity());
        assertEquals("Venue's latitude is correct", new Double("15.0"), venue.getLatitude());
        assertEquals("Venue's longitude is correct", new Double("15.0"), venue.getLongitude());

        venueService.updateVenue(basicVenue, venueID);

        assertEquals("Venue's name is correct", newVenueName, venue.getName());
        assertEquals("Venue's street is correct", newVenueStreet, venue.getStreet());
        assertEquals("Venue's city is correct", newVenueCity, venue.getCity());
        assertEquals("Venue's latitude is correct", new Double(newLat.toString()), venue.getLatitude());
        assertEquals("Venue's longitude is correct", new Double(newLon.toString()), venue.getLongitude());
    }

    @Test(expected = NotFoundException.class)
    public void addSectorToVenue_throwsNotFoundException_whenVenueWithWantedIdDoesNotExist() {
        Long venueID = 2L;
        Sector sector = new Sector(2L, "Jug", 2, 2, 4, 3, 3);

        venueService.addSectorToVenue(sector, venueID);
    }

    @Test(expected = BadRequestException.class)
    public void addSectorToVenue_throwsBadRequestException_whenAddSectorWithNameThatAlreadyExistsInVenue() {
        Long venueID = 1L;
        Sector sector = new Sector(2L, "Sever", 2, 2, 4, 3, 3);

        venueService.addSectorToVenue(sector, venueID);
    }

    @Test(expected = BadRequestException.class)
    public void addSectorToVenue_throwsBadRequestException_whenNewSectorOverlapWithOtherSectors() {
        Long venueID = 1L;
        Sector sector = new Sector(2L, "Jug", 2, 2, 4, 1, 1);

        venueService.addSectorToVenue(sector, venueID);
    }

    @Test
    public void addSectorToVenue_successfullyAddSectorToVenue() {
        Long venueID = 1L;
        Sector sector = new Sector(1L, "Sever", 2, 2, 4, 1, 1);
        Sector newSector = new Sector(2L, "Jug", 2, 2, 4, 3, 3);

        assertSame("Venue has 1 sector", venue.getSectors().size(), 1);

        for(Sector sector1 : venue.getSectors()) {
            assertSame("First sector has id equals to 1", 1L, sector1.getId());
            assertEquals("First sector has name Sever", "Sever", sector1.getName());
            assertSame("First sector has rows count equals to 2", 2, sector1.getRowsCount());
            assertSame("First sector has columns count equals to 2", 2, sector1.getColumnsCount());
            assertSame("First sector has max capacity equals to 4", 4, sector1.getMaxCapacity());
            assertSame("First sector has start row equals to 1", 1, sector1.getStartRow());
            assertSame("First sector has start column equals to 3", 1, sector1.getStartColumn());
        }

        venueService.addSectorToVenue(newSector, venueID);

        assertSame("Venue has 2 sectors", venue.getSectors().size(), 2);

        int index = 0;
        for(Sector sector1 : venue.getSectors()) {
            if(index == 0) {
                assertSame("First sector has id equals to 1", 1L, sector1.getId());
                assertEquals("First sector has name Sever", "Sever", sector1.getName());
                assertSame("First sector has rows count equals to 2", 2, sector1.getRowsCount());
                assertSame("First sector has columns count equals to 2", 2, sector1.getColumnsCount());
                assertSame("First sector has max capacity equals to 4", 4, sector1.getMaxCapacity());
                assertSame("First sector has start row equals to 1", 1, sector1.getStartRow());
                assertSame("First sector has start column equals to 1", 1, sector1.getStartColumn());
            } else {
                assertSame("Second sector has id equals to 2", 2L, sector1.getId());
                assertEquals("Second sector has name Sever", "Jug", sector1.getName());
                assertSame("Second sector has rows count equals to 2", 2, sector1.getRowsCount());
                assertSame("Second sector has columns count equals to 2", 2, sector1.getColumnsCount());
                assertSame("Second sector has max capacity equals to 4", 4, sector1.getMaxCapacity());
                assertSame("Second sector has start row equals to 3", 3, sector1.getStartRow());
                assertSame("Second sector has start column equals to 3", 3, sector1.getStartColumn());
            }
            index++;
        }
    }


}
