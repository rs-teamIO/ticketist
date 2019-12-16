package com.siit.ticketist.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@Sql("/reports.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VenueRepositoryTest {

    @Autowired
    private VenueRepository venueRepository;

    @Test
    public void getAllVenueRevenuesReturnsListOfVenuesWithTheirRevenues(){
        List<Object[]> venueRevenues = venueRepository.getAllVenueRevenues();
        assertThat(venueRevenues, hasSize(4));
        venueRevenues.stream().forEach(obj -> {
            if(((String)obj[0]).equals("Spens"))
                assertThat(new BigDecimal(130),  comparesEqualTo((BigDecimal)obj[1]));
            else
                assertThat(new BigDecimal(0),  comparesEqualTo((BigDecimal)obj[1]));
        });
    }
}
