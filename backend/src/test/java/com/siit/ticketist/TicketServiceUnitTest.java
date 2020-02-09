package com.siit.ticketist;

import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.exceptions.ForbiddenException;
import com.siit.ticketist.model.RegisteredUser;
import com.siit.ticketist.model.Reservation;
import com.siit.ticketist.model.Ticket;
import com.siit.ticketist.model.TicketStatus;
import com.siit.ticketist.repository.ReservationRepository;
import com.siit.ticketist.repository.TicketRepository;
import com.siit.ticketist.service.TicketService;
import com.siit.ticketist.service.UserService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TicketServiceUnitTest {

    @InjectMocks
    private TicketService ticketService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Mock
    private UserService userService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


}
