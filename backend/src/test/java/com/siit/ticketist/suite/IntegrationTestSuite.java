package com.siit.ticketist.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        IControllerTestSuite.class,
        IServiceTestSuite.class,
        IRepositoryTestSuite.class
})
public class IntegrationTestSuite {

}
