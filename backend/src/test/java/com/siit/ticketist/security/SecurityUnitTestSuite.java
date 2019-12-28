package com.siit.ticketist.security;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        UserDetailsFactoryUnitTest.class,
        UserDetailsServiceImplUnitTest.class,
        AuthenticationTokenFilterUnitTest.class,
        EntryPointUnauthorizedHandlerUnitTest.class,
        SpringSecurityUserUnitTest.class,
        TokenUtilsUnitTest.class
})
public class SecurityUnitTestSuite {
}


