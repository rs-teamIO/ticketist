package com.siit.ticketist.service;

import com.siit.ticketist.dto.VenueBasicDTO;
import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.exceptions.NotFoundException;
import com.siit.ticketist.model.Sector;
import com.siit.ticketist.model.Venue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/venues.sql")
public class VenueServiceIntegrationTest {

    @Autowired
    private VenueService venueService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void findOne_throwsNotFoundException_whenWantedVenueDoesNotExist() {
        exceptionRule.expect(NotFoundException.class);
        exceptionRule.expectMessage("Venue not found.");
        Long wantedID = 3L;
        venueService.findOne(wantedID);
    }

    @Test
    @Transactional
    @Rollback
    public void findOne_successfullyGetVenue() {
        Long wantedID = 1L;
        Venue venue = venueService.findOne(wantedID);

        assertEquals("Venue's id equals to wanted id", wantedID, venue.getId());
        assertEquals("Venue's name is correct", "Spens", venue.getName());
        assertEquals("Venue's active status is correct", true, venue.getIsActive());
        assertEquals("Venue's street is correct", "Sutjeska 2", venue.getStreet());
        assertEquals("Venue's city is correct", "Novi Sad", venue.getCity());
        assertEquals("Venue's latitude is correct", new Double("45.123"), venue.getLatitude());
        assertEquals("Venue's longitude is correct", new Double("25.123"), venue.getLongitude());
        Set<Sector> sectors = venue.getSectors();
        assertSame("Venue's sector size is correct", 2, sectors.size());

        for(Sector sector1 : sectors) {
            if(sector1.getId() == 1L) {
                assertSame("First sector has id equals to 1", 1L, sector1.getId());
                assertEquals("First sector has name Sever", "Sever", sector1.getName());
                assertSame("First sector has rows count equals to 2", 2, sector1.getRowsCount());
                assertSame("First sector has columns count equals to 2", 2, sector1.getColumnsCount());
                assertSame("First sector has max capacity equals to 4", 4, sector1.getMaxCapacity());
                assertSame("First sector has start row equals to 1", 1, sector1.getStartRow());
                assertSame("First sector has start column equals to 1", 1, sector1.getStartColumn());
            } else if(sector1.getId() == 2L){
                assertSame("Second sector has id equals to 2", 2L, sector1.getId());
                assertEquals("Second sector has name Zapad", "Zapad", sector1.getName());
                assertSame("Second sector has rows count equals to 2", 2, sector1.getRowsCount());
                assertSame("Second sector has columns count equals to 3", 3, sector1.getColumnsCount());
                assertSame("Second sector has max capacity equals to 6", 6, sector1.getMaxCapacity());
                assertSame("Second sector has start row equals to 4", 4, sector1.getStartRow());
                assertSame("Second sector has start column equals to 4", 4, sector1.getStartColumn());
            } else {
                assertEquals("Exception in case that there is unwanted sector", "NOT_EXIST", "NONE");
            }
        }
    }

    @Test
    public void saveVenue_throwsBadRequestException_whenNameStreetAndCityTripleAlreadyExists() {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Venue name, street and city combination must be unique!");
        Sector sector = new Sector(null, "Istok", 2, 2, 4, 1, 1);
        Set<Sector> sectors = new HashSet<>();
        sectors.add(sector);
        Venue newVenue = new Venue(null, "Spens", true, "Sutjeska 2", "Novi Sad", 15.0, 15.0, sectors, new HashSet<>());
        venueService.save(newVenue);
    }

    @Test
    public void saveVenue_throwsBadRequestException_whenNewVenueDoesNotHaveAnySectorIncluded() {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Venue must contain at least 1 sector");
        Venue newVenue = new Venue(null, "Emirates Stadium", true, "Gogoljeva 15", "Novi Sad", 15.0, 15.0, new HashSet<>(), new HashSet<>());
        venueService.save(newVenue);
    }

    @Test
    public void saveVenue_throwsBadRequestException_whenNewVenueHasSectorsWhichOverlap() {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Sectors overlap!");
        Sector sector1 = new Sector(null, "Sever", 2, 2, 4, 1, 1);
        Sector sector2 = new Sector(null, "Jug", 2, 2, 4, 2, 2);
        Set<Sector> sectors = new HashSet<>();
        sectors.add(sector1);
        sectors.add(sector2);

        Venue newVenue = new Venue(null, "Emirates Stadium", true, "Gogoljeva 15", "Novi Sad", 15.0, 15.0, sectors, new HashSet<>());
        venueService.save(newVenue);
    }

    @Test
    public void saveVenue_throwsBadRequestException_whenNewVenueHasSectorsWithTheSameName() {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Sector name is not unique!");
        Sector sector1 = new Sector(null, "Sever", 2, 2, 4, 1, 1);
        Sector sector2 = new Sector(null, "Sever", 2, 2, 4, 3, 3);
        Set<Sector> sectors = new HashSet<>();
        sectors.add(sector1);
        sectors.add(sector2);

        Venue newVenue = new Venue(null, "Emirates Stadium", true, "Gogoljeva 15", "Novi Sad", 15.0, 15.0, sectors, new HashSet<>());
        venueService.save(newVenue);
    }

    @Test
    public void saveVenue_successful() {
        Sector sector1 = new Sector(null, "Sever", 2, 2, 4, 1, 1);
        Sector sector2 = new Sector(null, "Jug", 2, 2, 4, 3, 3);
        Set<Sector> sectors = new HashSet<>();
        sectors.add(sector1);
        sectors.add(sector2);

        String venueName = "Emirates Stadium";
        String venueStreet = "Gogoljeva 15";
        String venueCity = "Novi Sad";
        double longitude = 15.0;
        double latitude = 15.0;

        Venue newVenue = new Venue(null, venueName, true, venueStreet, venueCity, latitude, longitude, sectors, new HashSet<>());

        Venue savedVenue = venueService.save(newVenue);
        assertSame("Venue's id equals to wanted id", 3L, savedVenue.getId());
        assertEquals("Venue's name is correct", venueName, savedVenue.getName());
        assertEquals("Venue's active status is correct", true, savedVenue.getIsActive());
        assertEquals("Venue's street is correct", venueStreet, savedVenue.getStreet());
        assertEquals("Venue's city is correct", venueCity, savedVenue.getCity());
        assertEquals("Venue's latitude is correct", new Double(latitude), savedVenue.getLatitude());
        assertEquals("Venue's longitude is correct", new Double(longitude), savedVenue.getLongitude());
        assertSame("Venue's sector size is correct", 2, savedVenue.getSectors().size());

        for(Sector sector : savedVenue.getSectors()) {
            if(sector.getName().equals("Sever")) {
                assertEquals("First sector has name Sever", "Sever", sector.getName());
                assertSame("First sector has rows count equals to 2", 2, sector.getRowsCount());
                assertSame("First sector has columns count equals to 2", 2, sector.getColumnsCount());
                assertSame("First sector has max capacity equals to 4", 4, sector.getMaxCapacity());
                assertSame("First sector has start row equals to 1", 1, sector.getStartRow());
                assertSame("First sector has start column equals to 1", 1, sector.getStartColumn());
            } else if(sector.getName().equals("Jug")){
                assertEquals("Second sector has name Jug", "Jug", sector.getName());
                assertSame("Second sector has rows count equals to 2", 2, sector.getRowsCount());
                assertSame("Second sector has columns count equals to 2", 2, sector.getColumnsCount());
                assertSame("Second sector has max capacity equals to 4", 4, sector.getMaxCapacity());
                assertSame("Second sector has start row equals to 3", 3, sector.getStartRow());
                assertSame("Second sector has start column equals to 3", 3, sector.getStartColumn());
            } else {
                assertEquals("Exception in case that there is unwanted sector", "NOT_EXIST", "NONE");
            }
        }
    }

    @Test
    public void checkIsActive_throwsNotFoundException_whenVenueWithWantedIdDoesNotExist() {
        exceptionRule.expect(NotFoundException.class);
        exceptionRule.expectMessage("Venue not found.");
        Long venueID = 3L;
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
        Long venueID = 2L;
        boolean checkIsActive = venueService.checkIsActive(venueID);
        assertFalse("Status is false", checkIsActive);
    }

    @Test
    public void changeActiveStatus_throwsNotFoundException_whenVenueWithWantedIdDoesNotExist() {
        exceptionRule.expect(NotFoundException.class);
        exceptionRule.expectMessage("Venue not found.");
        Long venueID = 3L;
        venueService.changeActiveStatus(venueID);
    }

    @Test
    public void changeActiveStatus_successful() {
        Long venueID = 1L;

        Venue venue = venueService.changeActiveStatus(venueID);
        assertFalse("venue status is false", venue.getIsActive());
        venue = venueService.changeActiveStatus(venueID);
        assertTrue("venue status is again true", venue.getIsActive());
    }

    @Test
    public void updateVenue_throwsNotFoundException_whenVenueWithWantedIdDoesNotExist() {
        exceptionRule.expect(NotFoundException.class);
        exceptionRule.expectMessage("Venue not found.");
        Long venueID = 3L;
        VenueBasicDTO basicVenue = new VenueBasicDTO("SPENS", "Puskinova 12", "Novi Sad", 12.0, 15.0);

        venueService.updateVenue(basicVenue, venueID);
    }

    @Test
    public void updateVenue_throwsBadRequestException__whenNameStreetAndCityTripleAlreadyExists() {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Venue name, street and city combination must be unique!");
        Long venueID = 1L;
        VenueBasicDTO basicVenue = new VenueBasicDTO("Arena", "Puskinova 2", "Beograd", 12.0, 15.0);

        venueService.updateVenue(basicVenue, venueID);
    }

    @Test
    public void updateVenue_Successful() {
        Long venueID = 1L;
        String newVenueName = "Emirates Stadium";
        String newVenueStreet = "Tolstojeva 12";
        String newVenueCity = "Novi Sad";
        Double newLat = 12.0;
        Double newLon = 16.0;

        VenueBasicDTO basicVenue = new VenueBasicDTO(newVenueName, newVenueStreet, newVenueCity, newLat, newLon);

        Venue startVenue = venueService.findOne(venueID);

        assertEquals("Venue's name is correct", "Spens", startVenue.getName());
        assertEquals("Venue's street is correct", "Sutjeska 2", startVenue.getStreet());
        assertEquals("Venue's city is correct", "Novi Sad", startVenue.getCity());
        assertEquals("Venue's latitude is correct", new Double("45.123"), startVenue.getLatitude());
        assertEquals("Venue's longitude is correct", new Double("25.123"), startVenue.getLongitude());

        Venue changedVenue = venueService.updateVenue(basicVenue, venueID);

        assertEquals("Venue's name is correct", newVenueName, changedVenue.getName());
        assertEquals("Venue's street is correct", newVenueStreet, changedVenue.getStreet());
        assertEquals("Venue's city is correct", newVenueCity, changedVenue.getCity());
        assertEquals("Venue's latitude is correct", new Double(newLat.toString()), changedVenue.getLatitude());
        assertEquals("Venue's longitude is correct", new Double(newLon.toString()), changedVenue.getLongitude());
    }

    @Test
    public void addSectorToVenue_throwsNotFoundException_whenVenueWithWantedIdDoesNotExist() {
        exceptionRule.expect(NotFoundException.class);
        exceptionRule.expectMessage("Venue not found.");
        Long venueID = 3L;
        Sector sector = new Sector(null, "Istok", 2, 2, 4, 8, 8);

        venueService.addSectorToVenue(sector, venueID);
    }

    @Test
    @Transactional
    @Rollback
    public void addSectorToVenue_throwsBadRequestException_whenAddSectorWithNameThatAlreadyExistsInVenue() {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Sector name already exists!");
        Long venueID = 1L;
        Sector sector = new Sector(null, "Sever", 2, 2, 4, 8, 8);

        venueService.addSectorToVenue(sector, venueID);
    }

    @Test
    @Transactional
    @Rollback
    public void addSectorToVenue_throwsBadRequestException_whenNewSectorOverlapWithOtherSectors() {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Sectors overlap!");
        Long venueID = 1L;
        Sector sector = new Sector(null, "Jug", 2, 2, 4, 1, 1);

        venueService.addSectorToVenue(sector, venueID);
    }

    @Test
    @Transactional
    @Rollback
    public void addSectorToVenue_successfullyAddSectorToVenue() {
        Long venueID = 1L;
        Sector newSector = new Sector(null, "Istok", 2, 2, 4, 8, 8);

        Venue startVenue = venueService.findOne(venueID);

        Set<Sector> sectors = startVenue.getSectors();
        assertSame("Venue has 2 sectors", sectors.size(), 2);

        for(Sector sector1 : sectors) {
            if(sector1.getId() == 1L) {
                assertSame("First sector has id equals to 1", 1L, sector1.getId());
                assertEquals("First sector has name Sever", "Sever", sector1.getName());
                assertSame("First sector has rows count equals to 2", 2, sector1.getRowsCount());
                assertSame("First sector has columns count equals to 2", 2, sector1.getColumnsCount());
                assertSame("First sector has max capacity equals to 4", 4, sector1.getMaxCapacity());
                assertSame("First sector has start row equals to 1", 1, sector1.getStartRow());
                assertSame("First sector has start column equals to 1", 1, sector1.getStartColumn());
            } else if(sector1.getId() == 2L){
                assertSame("Second sector has id equals to 2", 2L, sector1.getId());
                assertEquals("Second sector has name Zapad", "Zapad", sector1.getName());
                assertSame("Second sector has rows count equals to 2", 2, sector1.getRowsCount());
                assertSame("Second sector has columns count equals to 3", 3, sector1.getColumnsCount());
                assertSame("Second sector has max capacity equals to 6", 6, sector1.getMaxCapacity());
                assertSame("Second sector has start row equals to 4", 4, sector1.getStartRow());
                assertSame("Second sector has start column equals to 4", 4, sector1.getStartColumn());
            } else {
                assertEquals("Exception in case that there is unwanted sector", "NOT_EXIST", "NONE");
            }
        }

        Venue changedVenue = venueService.addSectorToVenue(newSector, venueID);

        assertSame("Venue has 3 sectors", changedVenue.getSectors().size(), 3);

        for(Sector sector1 : changedVenue.getSectors()) {
            if(sector1.getId() == 1L) {
                assertSame("First sector has id equals to 1", 1L, sector1.getId());
                assertEquals("First sector has name Sever", "Sever", sector1.getName());
                assertSame("First sector has rows count equals to 2", 2, sector1.getRowsCount());
                assertSame("First sector has columns count equals to 2", 2, sector1.getColumnsCount());
                assertSame("First sector has max capacity equals to 4", 4, sector1.getMaxCapacity());
                assertSame("First sector has start row equals to 1", 1, sector1.getStartRow());
                assertSame("First sector has start column equals to 1", 1, sector1.getStartColumn());
            } else if(sector1.getId() == 2L){
                assertSame("Second sector has id equals to 2", 2L, sector1.getId());
                assertEquals("Second sector has name Zapad", "Zapad", sector1.getName());
                assertSame("Second sector has rows count equals to 2", 2, sector1.getRowsCount());
                assertSame("Second sector has columns count equals to 3", 3, sector1.getColumnsCount());
                assertSame("Second sector has max capacity equals to 6", 6, sector1.getMaxCapacity());
                assertSame("Second sector has start row equals to 4", 4, sector1.getStartRow());
                assertSame("Second sector has start column equals to 4", 4, sector1.getStartColumn());
            } else if(sector1.getId() == 4L) {
                assertSame("Second sector has id equals to 4", 4L, sector1.getId());
                assertEquals("Second sector has name Istok", "Istok", sector1.getName());
                assertSame("Second sector has rows count equals to 2", 2, sector1.getRowsCount());
                assertSame("Second sector has columns count equals to 2", 2, sector1.getColumnsCount());
                assertSame("Second sector has max capacity equals to 4", 4, sector1.getMaxCapacity());
                assertSame("Second sector has start row equals to 8", 8, sector1.getStartRow());
                assertSame("Second sector has start column equals to 8", 8, sector1.getStartColumn());
            } else {
                assertEquals("Exception in case that there is unwanted sector", "NOT_EXIST", "NONE");
            }
        }
    }

}
