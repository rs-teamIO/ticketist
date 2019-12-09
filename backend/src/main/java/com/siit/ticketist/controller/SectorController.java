package com.siit.ticketist.controller;


import com.siit.ticketist.exceptions.NotFoundException;
import com.siit.ticketist.dto.SectorDTO;
import com.siit.ticketist.model.Sector;
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

@RestController
@RequestMapping("/api/sectors")
public class SectorController {

    @Autowired
    private SectorService sectorService;

    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getSector(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(sectorService.findOne(id), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

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
