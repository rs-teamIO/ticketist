package com.siit.ticketist.service;

import com.siit.ticketist.controller.exceptions.NotFoundException;
import com.siit.ticketist.dto.SectorDTO;
import com.siit.ticketist.model.Sector;
import com.siit.ticketist.repository.SectorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SectorService {

    @Autowired
    private SectorRepository sectorRepository;

    public List<SectorDTO> findSectorsByVenueId(Long id) {
        List<Sector> sectors = sectorRepository.findSectorsByVenueId(id);
        List<SectorDTO> sectorsDTO = new ArrayList<>();
        for(Sector sector : sectors) {
            sectorsDTO.add(new SectorDTO(sector));
        }
        return sectorsDTO;
    }

    public SectorDTO findOne(Long id) throws NotFoundException {
        return new SectorDTO(sectorRepository.findById(id).orElseThrow(() -> new NotFoundException("sector not found")));
    }

}
