package com.siit.ticketist.integration.controller;

import com.siit.ticketist.dto.SectorDTO;
import com.siit.ticketist.dto.VenueBasicDTO;
import com.siit.ticketist.dto.VenueDTO;
import com.siit.ticketist.dto.VenuePageDTO;
import com.siit.ticketist.model.Sector;
import com.siit.ticketist.model.Venue;
import com.siit.ticketist.security.TokenUtils;
import com.siit.ticketist.security.UserDetailsServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/controller.sql")
public class VenueControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    private HttpHeaders headers;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Before
    public void setUp() {
        final UserDetails userDetails = userDetailsService.loadUserByUsername("admin");
        final String token = tokenUtils.generateToken(userDetails);

        headers = new HttpHeaders();
        headers.set("Authorization", token);
    }

    @Test
    public void getVenue_shouldThrowNotFound_whenVenueDoesNotExist() {
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<VenueDTO> response = testRestTemplate.exchange("/api/venues/5", HttpMethod.GET, request, VenueDTO.class);
        assertEquals("Expected status NOT_FOUND", HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull("empty body", response.getBody().getId());
    }

    @Test
    public void getVenue_shouldPass_whenVenueExists() {
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<VenueDTO> response = testRestTemplate.exchange("/api/venues/2", HttpMethod.GET, request, VenueDTO.class);
        assertEquals("Expected status OK", HttpStatus.OK, response.getStatusCode());
        assertSame("Id equals to 2", response.getBody().getId(), 2L);
        assertEquals("Name equals to Novi Sad Fair", response.getBody().getName(), "Novi Sad Fair");
        assertSame("Sector number is 1", response.getBody().getSectors().size(), 1);
    }

    @Test
    public void getVenues_shouldPass() {
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<VenueDTO[]> response = testRestTemplate.exchange("/api/venues", HttpMethod.GET, request, VenueDTO[].class);
        assertEquals("Expected status OK", HttpStatus.OK, response.getStatusCode());
        assertSame("Venues size is 3", response.getBody().length, 3);
    }

    @Test
    public void getVenuesPaged_shouldPass() {
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<VenuePageDTO> response = testRestTemplate.exchange("/api/venues/paged", HttpMethod.GET, request, VenuePageDTO.class);
        assertEquals("Expected status OK", HttpStatus.OK, response.getStatusCode());
        assertSame("Venues size is 3", response.getBody().getVenues().size(), 3);
        assertEquals("Total size is 3", response.getBody().getTotalSize().toString(), "3");
    }

    @Test
    public void getActiveVenues_shouldPass() {
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<VenueDTO[]> response = testRestTemplate.exchange("/api/venues/active", HttpMethod.GET, request, VenueDTO[].class);
        assertEquals("Expected status OK", HttpStatus.OK, response.getStatusCode());
        assertSame("Venues size is 2", response.getBody().length, 2);
    }

    @Test
    public void saveVenue_throwsUnauthorized_whenUserIsNotLogged() {
        Sector sector = new Sector(null, "Istok", 2, 2, 4, 1, 1);
        Set<Sector> sectors = new HashSet<>();
        sectors.add(sector);
        Venue newVenue = new Venue(null, "Spens", true, "Sutjeska 2", "Novi Sad", 15.0, 15.0, sectors, new HashSet<>());
        VenueDTO venueDTO = new VenueDTO(newVenue);

        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<VenueDTO> request = new HttpEntity<>(venueDTO, httpHeaders);
        ResponseEntity<VenueDTO> response = testRestTemplate.exchange("/api/venues", HttpMethod.POST, request, VenueDTO.class);
        assertEquals("Expected status Unauthorized", HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void saveVenue_throwsForbidden_whenUserIsNotAdmin() {
        Sector sector = new Sector(null, "Istok", 2, 2, 4, 1, 1);
        Set<Sector> sectors = new HashSet<>();
        sectors.add(sector);
        Venue newVenue = new Venue(null, "Spens", true, "Sutjeska 2", "Novi Sad", 15.0, 15.0, sectors, new HashSet<>());
        VenueDTO venueDTO = new VenueDTO(newVenue);

        UserDetails userDetails = userDetailsService.loadUserByUsername("user2020");
        String token = tokenUtils.generateToken(userDetails);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", token);
        HttpEntity<VenueDTO> request = new HttpEntity<>(venueDTO, httpHeaders);
        ResponseEntity<VenueDTO> response = testRestTemplate.exchange("/api/venues", HttpMethod.POST, request, VenueDTO.class);
        assertEquals("Expected status Forbidden", HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void saveVenue_throwsBadRequest_whenNameStreetAndCityTripleAlreadyExists() {
        Sector sector = new Sector(null, "Istok", 2, 2, 4, 1, 1);
        Set<Sector> sectors = new HashSet<>();
        sectors.add(sector);
        Venue newVenue = new Venue(null, "Spens", true, "Sutjeska 2", "Novi Sad", 15.0, 15.0, sectors, new HashSet<>());
        VenueDTO venueDTO = new VenueDTO(newVenue);

        HttpEntity<VenueDTO> request = new HttpEntity<>(venueDTO, headers);
        ResponseEntity<VenueDTO> response = testRestTemplate.exchange("/api/venues", HttpMethod.POST, request, VenueDTO.class);
        assertEquals("Expected status BAD_REQUEST", HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody().getId());
    }

    @Test
    public void saveVenue_throwsBadRequest_whenNewVenueDoesNotHaveAnySectorIncluded() {
        Venue newVenue = new Venue(null, "Emirates Stadium", true, "Gogoljeva 15", "Novi Sad", 15.0, 15.0, new HashSet<>(), new HashSet<>());
        VenueDTO venueDTO = new VenueDTO(newVenue);

        HttpEntity<VenueDTO> request = new HttpEntity<>(venueDTO, headers);
        ResponseEntity<VenueDTO> response = testRestTemplate.exchange("/api/venues", HttpMethod.POST, request, VenueDTO.class);
        assertEquals("Expected status BAD_REQUEST", HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody().getId());
    }

    @Test
    public void saveVenue_throwsBadRequest_whenNewVenueHasSectorsWhichOverlap() {
        Sector sector1 = new Sector(null, "Sever", 2, 2, 4, 1, 1);
        Sector sector2 = new Sector(null, "Jug", 2, 2, 4, 2, 2);
        Set<Sector> sectors = new HashSet<>();
        sectors.add(sector1);
        sectors.add(sector2);
        Venue newVenue = new Venue(null, "Emirates Stadium", true, "Gogoljeva 15", "Novi Sad", 15.0, 15.0, sectors, new HashSet<>());
        VenueDTO venueDTO = new VenueDTO(newVenue);

        HttpEntity<VenueDTO> request = new HttpEntity<>(venueDTO, headers);
        ResponseEntity<VenueDTO> response = testRestTemplate.exchange("/api/venues", HttpMethod.POST, request, VenueDTO.class);
        assertEquals("Expected status BAD_REQUEST", HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody().getId());
    }

    @Test
    public void saveVenue_throwsBadRequest_whenNewVenueHasSectorsWithTheSameName() {
        Sector sector1 = new Sector(null, "Sever", 2, 2, 4, 1, 1);
        Sector sector2 = new Sector(null, "Sever", 2, 2, 4, 3, 3);
        Set<Sector> sectors = new HashSet<>();
        sectors.add(sector1);
        sectors.add(sector2);
        Venue newVenue = new Venue(null, "Emirates Stadium", true, "Gogoljeva 15", "Novi Sad", 15.0, 15.0, sectors, new HashSet<>());
        VenueDTO venueDTO = new VenueDTO(newVenue);

        HttpEntity<VenueDTO> request = new HttpEntity<>(venueDTO, headers);
        ResponseEntity<VenueDTO> response = testRestTemplate.exchange("/api/venues", HttpMethod.POST, request, VenueDTO.class);
        assertEquals("Expected status BAD_REQUEST", HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody().getId());
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

        VenueDTO venueDTO = new VenueDTO(newVenue);

        HttpEntity<VenueDTO> request = new HttpEntity<>(venueDTO, headers);
        ResponseEntity<VenueDTO> response = testRestTemplate.exchange("/api/venues", HttpMethod.POST, request, VenueDTO.class);
        assertEquals("Expected status CREATED", HttpStatus.CREATED, response.getStatusCode());

        assertSame("Venue's id equals to wanted id", 4L, response.getBody().getId());
        assertEquals("Venue's name is correct", venueName, response.getBody().getName());
        assertEquals("Venue's active status is correct", true, response.getBody().getIsActive());
        assertEquals("Venue's street is correct", venueStreet, response.getBody().getStreet());
        assertEquals("Venue's city is correct", venueCity, response.getBody().getCity());
        assertEquals("Venue's latitude is correct", new Double(latitude), response.getBody().getLatitude());
        assertEquals("Venue's longitude is correct", new Double(longitude), response.getBody().getLongitude());
        assertSame("Venue's sector size is correct", 2, response.getBody().getSectors().size());
    }

    @Test
    public void changeActiveStatus_successful() {
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<Boolean> response = testRestTemplate.exchange("/api/venues/change-status/1", HttpMethod.PUT, request, Boolean.class);
        assertEquals("Expected status OK", HttpStatus.OK, response.getStatusCode());
        assertFalse("venue status is changed to false", response.getBody().booleanValue());
    }

    @Test
    public void updateVenue_throwsBadRequest__whenNameStreetAndCityTripleAlreadyExists() {
        VenueBasicDTO basicVenue = new VenueBasicDTO("Novi Sad Fair", "Hajduk Veljkova 11", "Novi Sad", 45.267136, 19.833549);
        HttpEntity<VenueBasicDTO> request = new HttpEntity<>(basicVenue, headers);
        ResponseEntity<VenueDTO> response = testRestTemplate.exchange("/api/venues/1", HttpMethod.PUT, request, VenueDTO.class);
        assertEquals("Expected status BAD_REQUEST", HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody().getId());
    }

    @Test
    public void updateVenue_Successful() {
        String newVenueName = "Emirates Stadium";
        String newVenueStreet = "Tolstojeva 12";
        String newVenueCity = "Novi Sad";
        Double newLat = 12.0;
        Double newLon = 16.0;
        VenueBasicDTO basicVenue = new VenueBasicDTO(newVenueName, newVenueStreet, newVenueCity, newLat, newLon);

        HttpEntity<VenueBasicDTO> request = new HttpEntity<>(basicVenue, headers);
        ResponseEntity<VenueDTO> response = testRestTemplate.exchange("/api/venues/1", HttpMethod.PUT, request, VenueDTO.class);
        assertEquals("Expected status OK", HttpStatus.OK, response.getStatusCode());

        assertEquals("Venue's name is correct", newVenueName, response.getBody().getName());
        assertEquals("Venue's street is correct", newVenueStreet, response.getBody().getStreet());
        assertEquals("Venue's city is correct", newVenueCity, response.getBody().getCity());
        assertEquals("Venue's latitude is correct", new Double(newLat.toString()), response.getBody().getLatitude());
        assertEquals("Venue's longitude is correct", new Double(newLon.toString()), response.getBody().getLongitude());

    }

    @Test
    public void addSectorToVenue_throwsBadRequest_whenAddSectorWithNameThatAlreadyExistsInVenue() {
        Sector sector = new Sector(null, "Sever", 2, 2, 4, 8, 8);
        SectorDTO sectorDTO = new SectorDTO(sector);
        HttpEntity<SectorDTO> request = new HttpEntity<>(sectorDTO, headers);
        ResponseEntity<VenueDTO> response = testRestTemplate.exchange("/api/venues/sector/1", HttpMethod.POST, request, VenueDTO.class);
        assertEquals("Expected status BAD_REQUEST", HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody().getId());
    }

    @Test
    public void addSectorToVenue_throwsBadRequestException_whenNewSectorOverlapWithOtherSectors() {
        Sector sector = new Sector(null, "Central", 2, 2, 4, 1, 1);
        SectorDTO sectorDTO = new SectorDTO(sector);
        HttpEntity<SectorDTO> request = new HttpEntity<>(sectorDTO, headers);
        ResponseEntity<VenueDTO> response = testRestTemplate.exchange("/api/venues/sector/1", HttpMethod.POST, request, VenueDTO.class);
        assertEquals("Expected status BAD_REQUEST", HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody().getId());
    }

    @Test
    public void addSectorToVenue_successfullyAddSectorToVenue() {
        Sector sector = new Sector(null, "Central", 2, 2, 4, 8, 8);
        SectorDTO sectorDTO = new SectorDTO(sector);
        HttpEntity<SectorDTO> request = new HttpEntity<>(sectorDTO, headers);
        ResponseEntity<VenueDTO> response = testRestTemplate.exchange("/api/venues/sector/1", HttpMethod.POST, request, VenueDTO.class);
        assertEquals("Expected status OK", HttpStatus.OK, response.getStatusCode());
        assertSame("Venue has 5 sectors", response.getBody().getSectors().size(), 5);
    }

}
