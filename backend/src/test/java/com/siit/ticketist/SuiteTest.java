package com.siit.ticketist;

import com.siit.ticketist.integration.repository.SectorRepositoryTest;
import com.siit.ticketist.repository.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TicketRepositoryTest.class,
        SectorRepositoryTest.class,
        EventRepositoryTest.class,
        VenueRepositoryTest.class
})
//@ActiveProfiles("test")
//@TestPropertySource(locations = "classpath:application.properties")
public class SuiteTest {
}
