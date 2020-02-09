package com.siit.ticketist.suite;

import com.siit.ticketist.unit.service.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        EmailServiceTest.class,
        EventSectorServiceTest.class,
        EventServiceTest.class,
        PdfServiceTest.class,
        QrCodeServiceTest.class,
        RegisteredUserServiceTest.class,
        ReportServiceTest.class,
        TicketServiceTest.class,
        UserServiceTest.class,
        VenueServiceTest.class,
        SecurityUnitTestSuite.class
})
public class UnitTestSuite {
}
