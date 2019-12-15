package com.siit.ticketist.controller;

import com.siit.ticketist.dto.SectorDTO;
import com.siit.ticketist.model.Sector;
import com.siit.ticketist.model.Venue;
import com.siit.ticketist.service.SectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Sectors REST controller.
 */
@RestController
@RequestMapping("/api/sectors")
public class SectorController {

    private final SectorService sectorService;

    @Autowired
    public SectorController(SectorService sectorService) {
        this.sectorService = sectorService;
    }

    /**
     * GET /api/sectors/{id}
     * Returns a {@link Sector} with the specified ID
     *
     * @param id ID of the {@link Sector}
     * @return Response entity containing sector info
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getSector(@PathVariable("id") Long id) {
        Sector sector = sectorService.findOne(id);
        return new ResponseEntity<>(sector, HttpStatus.OK);
    }

    /**
     * GET /api/sectors/venue/{venueId}
     * Returns a list of sectors that are related to a {@link Venue} with given ID
     *
     * @param venueId ID of the {@link Venue}
     * @return TODO
     */
    @GetMapping(value="/venue/{venueId}")
    public ResponseEntity<List<SectorDTO>> getSectorsByVenueId(@PathVariable("venueId") Long venueId) {
        List<Sector> sectors = sectorService.findSectorsByVenueId(venueId);
        List<SectorDTO> sectorsDTO = new ArrayList<>();
        for(Sector sector : sectors) {
            sectorsDTO.add(new SectorDTO(sector));
        }
        return new ResponseEntity<>(sectorsDTO, HttpStatus.OK);
    }
}
