package com.siit.ticketist;

import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.model.Reservation;
import com.siit.ticketist.model.Ticket;
import com.siit.ticketist.model.TicketStatus;
import com.siit.ticketist.repository.ReservationRepository;
import com.siit.ticketist.security.TokenUtils;
import com.siit.ticketist.security.UserDetailsServiceImpl;
import com.siit.ticketist.service.TicketService;
import com.siit.ticketist.service.UserService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/data.sql")
public class TicketServiceTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Before
    public void before(){
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken("user2020", "123456"));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }



}
