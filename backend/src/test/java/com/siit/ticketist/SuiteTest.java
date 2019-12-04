package com.siit.ticketist;

import com.siit.ticketist.repository.TicketRepositoryTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.test.context.TestPropertySource;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TicketRepositoryTest.class
})
@TestPropertySource(locations = "classpath:application-test.properties")
public class SuiteTest {
}
