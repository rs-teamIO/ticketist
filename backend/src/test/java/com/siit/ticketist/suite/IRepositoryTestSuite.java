package com.siit.ticketist.suite;

import com.siit.ticketist.integration.repository.EventRepositoryTest;
import com.siit.ticketist.integration.repository.SectorRepositoryTest;
import com.siit.ticketist.integration.repository.TicketRepositoryTest;
import com.siit.ticketist.integration.repository.VenueRepositoryTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        EventRepositoryTest.class,
        SectorRepositoryTest.class,
        TicketRepositoryTest.class,
        VenueRepositoryTest.class
})
public class IRepositoryTestSuite {
}
