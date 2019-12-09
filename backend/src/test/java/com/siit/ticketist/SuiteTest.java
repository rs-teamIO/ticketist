package com.siit.ticketist;

import com.siit.ticketist.repository.EventRepositoryTest;
import com.siit.ticketist.repository.SectorRepositoryTest;
import com.siit.ticketist.repository.TicketRepositoryTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TicketRepositoryTest.class,
        SectorRepositoryTest.class,
        EventRepositoryTest.class
})
//@ActiveProfiles("test")
//@TestPropertySource(locations = "classpath:application.properties")
public class SuiteTest {
}
