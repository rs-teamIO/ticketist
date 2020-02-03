package com.siit.ticketist.suite;

import com.siit.ticketist.unit.service.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        EventServiceTest.class,
        QrCodeServiceTest.class,
        RegisteredUserServiceTest.class,
        ReportServiceTest.class,
        TicketServiceTest.class,
        UserServiceTest.class,
        VenueServiceTest.class
})
public class UnitTestSuite {
}
