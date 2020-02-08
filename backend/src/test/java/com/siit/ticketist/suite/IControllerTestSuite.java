package com.siit.ticketist.suite;

import com.siit.ticketist.integration.controller.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AuthenticationControllerTest.class,
        EventControllerTest.class,
        RegisteredUserControllerTest.class,
        ReportsControllerTest.class,
        TicketControllerTest.class,
        VenueControllerTest.class
})
public class IControllerTestSuite {
}
