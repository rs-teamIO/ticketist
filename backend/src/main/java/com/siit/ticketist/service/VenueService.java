package com.siit.ticketist.service;

import com.siit.ticketist.controller.exceptions.BadRequestException;
import com.siit.ticketist.controller.exceptions.NotFoundException;
import com.siit.ticketist.model.Sector;
import com.siit.ticketist.model.Venue;
import com.siit.ticketist.repository.SectorRepository;
import com.siit.ticketist.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.lang.reflect.Array;
import java.util.ArrayList;

@Service
public class VenueService {

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private SectorRepository sectorRepository;

    public List<Venue> findAll() {
        return venueRepository.findAll();
    }

    public Venue findOne(Long id) throws NotFoundException {
        return venueRepository.findById(id).orElseThrow(() -> new NotFoundException("Venue not found"));
    }

    public Venue save(Venue venue) {
        int overlap = 0;
        for(Sector sector: venue.getSectors()){
            for(Sector sectorSave: venue.getSectors()){
                    if(!sectorCanBeDrawn(sector,sectorSave)){
                        overlap++;
                        if(overlap>venue.getSectors().size())
                            throw new BadRequestException("Cant add overlaping sectors");
                    }
            }
        }

        return venueRepository.save(venue);
    }

    private boolean sectorCanBeDrawn(Sector sector, Sector sectorSave) {
        if(sector.getStartRow()>sectorSave.getStartRow()+sectorSave.getRowsCount()) return true;
        else if(sector.getStartRow()+sector.getRowsCount()<sectorSave.getStartRow()) return true;
        else if(sector.getStartColumn()>sectorSave.getStartColumn()+sectorSave.getColumnsCount()) return true;
        else if(sector.getStartColumn()+sector.getColumnsCount()<sectorSave.getStartColumn()) return true;
        else return false;
    }
}
