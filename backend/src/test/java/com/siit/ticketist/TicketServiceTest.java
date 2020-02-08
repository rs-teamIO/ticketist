package com.siit.ticketist;

import com.itextpdf.text.log.SysoCounter;
import com.siit.ticketist.dto.AuthenticationRequest;
import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.exceptions.ForbiddenException;
import com.siit.ticketist.model.*;
import com.siit.ticketist.repository.ReservationRepository;
import com.siit.ticketist.security.TokenUtils;
import com.siit.ticketist.security.UserDetailsServiceImpl;
import com.siit.ticketist.service.TicketService;
import com.siit.ticketist.service.UserService;
import lombok.With;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
