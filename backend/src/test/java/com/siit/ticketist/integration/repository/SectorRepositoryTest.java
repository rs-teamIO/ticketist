package com.siit.ticketist.integration.repository;

import com.siit.ticketist.model.Sector;
import static org.junit.Assert.*;

import com.siit.ticketist.repository.SectorRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectorRepositoryTest {

    @Autowired
    private SectorRepository sectorRepository;

    @Test
    public void findSectorsByVenueId_ShouldReturnEmptyList_whenVenueIdIsWrong(){
        List<Sector> sectorList = sectorRepository.findSectorsByVenueId(10l);
        assertEquals("sector list is empty (0 elements)",0,sectorList.size());
    }

    @Test
    public void findSectorsByVenueId_ShouldReturnSectorList_whenVenueIdIsCorrect(){
        List<Sector> sectorList = sectorRepository.findSectorsByVenueId(1l);
        assertEquals("sector list is empty (2 elements)",2,sectorList.size());
    }

}
