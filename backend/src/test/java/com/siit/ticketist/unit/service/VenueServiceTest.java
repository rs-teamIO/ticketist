package com.siit.ticketist.unit.service;

import com.siit.ticketist.dto.VenueBasicDTO;
import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.exceptions.NotFoundException;
import com.siit.ticketist.model.Sector;
import com.siit.ticketist.model.Venue;
import com.siit.ticketist.repository.VenueRepository;
import com.siit.ticketist.service.VenueService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class VenueServiceTest {

    @InjectMocks
    private VenueService venueService;

    @Mock
    private VenueRepository venueRepositoryMock;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    private Venue venue;

    private Set<Sector> sectors;

    @Before
    public void setupVenueRepositoryMock() {
        MockitoAnnotations.initMocks(this);

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
    @Test
    public void findOne_throwsNotFoundException_whenWantedVenueDoesNotExist() {
        exceptionRule.expect(NotFoundException.class);
        exceptionRule.expectMessage("Venue not found.");
        Long wantedID = 2L;
        when(venueRepositoryMock.findById(wantedID))
                .thenReturn(Optional.empty());
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
        for(Sector sector1 : venue.getSectors()) {
            assertSame("First sector has id equals to 1", 1L, sector1.getId());
            assertEquals("First sector has name Sever", "Sever", sector1.getName());
            assertSame("First sector has rows count equals to 2", 2, sector1.getRowsCount());
            assertSame("First sector has columns count equals to 2", 2, sector1.getColumnsCount());
            assertSame("First sector has max capacity equals to 4", 4, sector1.getMaxCapacity());
            assertSame("First sector has start row equals to 1", 1, sector1.getStartRow());
            assertSame("First sector has start column equals to 3", 1, sector1.getStartColumn());
        }
    }


    // Try to save venue, but method throws BadRequestException because
    // venue with entered name/city/street triple already exists
    @Test
    public void saveVenue_throwsBadRequestException_whenNameStreetAndCityTripleAlreadyExists() {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Venue name, street and city combination must be unique!");
        Long sectorID = 2L;
        Sector sector = new Sector(sectorID, "Sever", 2, 2, 4, 1, 1);
        Set<Sector> sectors = new HashSet<>();
        sectors.add(sector);
        Venue newVenue = new Venue(null, "SPENS", true, "Puskinova 12", "Novi Sad", 15.0, 15.0, sectors, new HashSet<>());
        venueService.save(newVenue);
    }

    // Try to save venue, but method throws BadRequestException because
    // new venue does not have any sector included
    @Test
    public void saveVenue_throwsBadRequestException_whenNewVenueDoesNotHaveAnySectorIncluded() {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Venue must contain at least 1 sector");
        Venue newVenue = new Venue(null, "ARENA", true, "Gogoljeva 15", "Novi Sad", 15.0, 15.0, new HashSet<>(), new HashSet<>());
        venueService.save(newVenue);
    }

    // Try to save venue, but method throws BadRequestException because
    // new venue has sectors which overlap
    @Test
    public void saveVenue_throwsBadRequestException_whenNewVenueHasSectorsWhichOverlap() {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Sectors overlap!");
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
    @Test
    public void saveVenue_throwsBadRequestException_whenNewVenueHasSectorsWithTheSameName() {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Sector name is not unique!");
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

        String venueName = "Emirates Stadium";
        String venueStreet = "Gogoljeva 15";
        String venueCity = "Novi Sad";
        double longitude = 15.0;
        double latitude = 15.0;

        Venue newVenue = new Venue(null, venueName, true, venueStreet, venueCity, latitude, longitude, sectors, new HashSet<>());
        Venue savedVenueMock = new Venue(2L, venueName, true, venueStreet, venueCity, latitude, longitude, sectors, new HashSet<>());

        when(venueRepositoryMock.save(newVenue))
                .thenReturn(savedVenueMock);

        Venue savedVenue = venueService.save(newVenue);
        assertSame("Venue's id equals to wanted id", 2L, savedVenue.getId());
        assertEquals("Venue's name is correct", venueName, savedVenue.getName());
        assertEquals("Venue's active status is correct", true, savedVenue.getIsActive());
        assertEquals("Venue's street is correct", venueStreet, savedVenue.getStreet());
        assertEquals("Venue's city is correct", venueCity, savedVenue.getCity());
        assertEquals("Venue's latitude is correct", new Double(latitude), savedVenue.getLatitude());
        assertEquals("Venue's longitude is correct", new Double(longitude), savedVenue.getLongitude());
        assertSame("Venue's sector size is correct", 2, savedVenue.getSectors().size());

        //add sector validation
    }

    @Test
    public void checkIsActive_throwsNotFoundException_whenVenueWithWantedIdDoesNotExist() {
        exceptionRule.expect(NotFoundException.class);
        exceptionRule.expectMessage("Venue not found.");
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

    @Test
    public void changeActiveStatus_throwsNotFoundException_whenVenueWithWantedIdDoesNotExist() {
        exceptionRule.expect(NotFoundException.class);
        exceptionRule.expectMessage("Venue not found.");
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

    @Test
    public void updateVenue_throwsNotFoundException_whenVenueWithWantedIdDoesNotExist() {
        exceptionRule.expect(NotFoundException.class);
        exceptionRule.expectMessage("Venue not found.");
        Long venueID = 2L;
        VenueBasicDTO basicVenue = new VenueBasicDTO("SPENS", "Puskinova 12", "Novi Sad", 12.0, 15.0);

        venueService.updateVenue(basicVenue, venueID);
    }

    @Test
    public void updateVenue_throwsBadRequestException__whenNameStreetAndCityTripleAlreadyExists() {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Venue name, street and city combination must be unique!");
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

    @Test
    public void addSectorToVenue_throwsNotFoundException_whenVenueWithWantedIdDoesNotExist() {
        exceptionRule.expect(NotFoundException.class);
        exceptionRule.expectMessage("Venue not found.");
        Long venueID = 2L;
        Sector sector = new Sector(2L, "Jug", 2, 2, 4, 3, 3);

        venueService.addSectorToVenue(sector, venueID);
    }

    @Test
    public void addSectorToVenue_throwsBadRequestException_whenAddSectorWithNameThatAlreadyExistsInVenue() {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Sector name already exists!");
        Long venueID = 1L;
        Sector sector = new Sector(2L, "Sever", 2, 2, 4, 3, 3);

        venueService.addSectorToVenue(sector, venueID);
    }

    @Test
    public void addSectorToVenue_throwsBadRequestException_whenNewSectorOverlapWithOtherSectors() {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Sectors overlap!");
        Long venueID = 1L;
        Sector sector = new Sector(2L, "Jug", 2, 2, 4, 1, 1);

        venueService.addSectorToVenue(sector, venueID);
    }

    @Test
    public void addSectorToVenue_successfullyAddSectorToVenue() {
        Long venueID = 1L;
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

        for(Sector sector1 : venue.getSectors()) {
            if(sector1.getId() == 1L) {
                assertSame("First sector has id equals to 1", 1L, sector1.getId());
                assertEquals("First sector has name Sever", "Sever", sector1.getName());
                assertSame("First sector has rows count equals to 2", 2, sector1.getRowsCount());
                assertSame("First sector has columns count equals to 2", 2, sector1.getColumnsCount());
                assertSame("First sector has max capacity equals to 4", 4, sector1.getMaxCapacity());
                assertSame("First sector has start row equals to 1", 1, sector1.getStartRow());
                assertSame("First sector has start column equals to 1", 1, sector1.getStartColumn());
            } else if(sector1.getId() == 2L) {
                assertSame("Second sector has id equals to 2", 2L, sector1.getId());
                assertEquals("Second sector has name Sever", "Jug", sector1.getName());
                assertSame("Second sector has rows count equals to 2", 2, sector1.getRowsCount());
                assertSame("Second sector has columns count equals to 2", 2, sector1.getColumnsCount());
                assertSame("Second sector has max capacity equals to 4", 4, sector1.getMaxCapacity());
                assertSame("Second sector has start row equals to 3", 3, sector1.getStartRow());
                assertSame("Second sector has start column equals to 3", 3, sector1.getStartColumn());
            } else {
                assertEquals("Exception in case that there is unwanted sector", "NOT_EXIST", "NONE");
            }
        }
    }


}
