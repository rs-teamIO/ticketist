package com.siit.ticketist.security;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * This class contains unit test methods for {@link EntryPointUnauthorizedHandler}.
 */
public class EntryPointUnauthorizedHandlerUnitTest {

    @InjectMocks
    private EntryPointUnauthorizedHandler entryPointUnauthorizedHandler;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Tests commence from {@link EntryPointUnauthorizedHandler}.
     *
     * @throws IOException unhandled
     * @throws ServletException unhandled
     */
    @Test
    public void commence_sendsErrorAsResponse_whenInvoked() throws IOException, ServletException {

        // Arrange
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        this.entryPointUnauthorizedHandler.commence(request, response, null);

        // Assert
        assertEquals("HTTP status is UNAUTHORIZED", HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }
}
