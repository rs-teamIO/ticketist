package com.siit.ticketist.suite;

import com.siit.ticketist.integration.service.*;
import com.siit.ticketist.security.UserDetailsServiceImplIntegrationTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        EventServiceTest.class,
        RegisteredUserServiceTest.class,
        ReportServiceTest.class,
        TicketServiceTest.class,
        VenueServiceTest.class,
        UserDetailsServiceImplIntegrationTest.class
})
public class IServiceTestSuite {
}
