package com.siit.ticketist.service;

import com.siit.ticketist.exceptions.NotFoundException;
import com.siit.ticketist.model.Sector;
import com.siit.ticketist.repository.SectorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectorService {

    @Autowired
    private SectorRepository sectorRepository;

    public Sector findOne(Long id) throws NotFoundException {
        return sectorRepository.findById(id).orElseThrow(() -> new NotFoundException("sector not found"));
    }

    public List<Sector> findSectorsByVenueId(Long id) {
        return sectorRepository.findSectorsByVenueId(id);
    }

}
