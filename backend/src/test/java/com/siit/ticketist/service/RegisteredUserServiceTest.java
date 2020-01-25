package com.siit.ticketist.service;

import com.siit.ticketist.exceptions.AuthorizationException;
import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.exceptions.NotFoundException;
import com.siit.ticketist.model.RegisteredUser;
import com.siit.ticketist.model.User;
import com.siit.ticketist.security.UserDetailsServiceImpl;
import com.sun.deploy.security.ruleset.ExceptionRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/data.sql")
public class RegisteredUserServiceTest {

    @Autowired
    private RegisteredUserService registeredUserService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserService userService;

    @Before
    public void setUp(){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("user2020","123456"));

        final UserDetails userDetails = userDetailsService.loadUserByUsername("user2020");
        final User user = userService.findByUsername(userDetails.getUsername());
        System.out.println(user);
    }

    @Test
    public void updateUser_ShouldThrowAuthorizationException_whenUserDoesntExist(){
        exceptionRule.expect(AuthorizationException.class);
        RegisteredUser regUser = new RegisteredUser();
        regUser.setUsername("Primer");

        this.registeredUserService.update(regUser,"");

    }

    @Test
    public void updateUser_ShouldThrowBadRequestException_whenEmailIsTaken(){
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("User with e-mail 'rocky+1@gmail.com' already registered");
        RegisteredUser regUser = new RegisteredUser();
        regUser.setUsername("user2020");
        regUser.setEmail("rocky+1@gmail.com");

        this.registeredUserService.update(regUser,"");

    }

    @Test
    public void updateUser_ShouldPass_whenUserUpdateIsValid(){
        RegisteredUser regUser = new RegisteredUser();
        regUser.setUsername("user2020");
        regUser.setEmail("kaca@gmail.com");
        regUser.setFirstName("First");
        regUser.setLastName("Last");
        regUser.setPhone("051221");

        RegisteredUser test = this.registeredUserService.update(regUser,"newPassword");
        assertEquals(test.getUsername(),regUser.getUsername());
        assertEquals(test.getPassword(),"newPassword");
        assertEquals(test.getFirstName(),regUser.getFirstName());
        assertEquals(test.getLastName(),regUser.getLastName());
        assertEquals(test.getPhone(),regUser.getPhone());
        assertEquals(test.getEmail(),regUser.getEmail());
    }



}
