package com.siit.ticketist.service;

import com.siit.ticketist.exceptions.AuthorizationException;
import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.exceptions.NotFoundException;
import com.siit.ticketist.model.RegisteredUser;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

    @Test(expected = AuthorizationException.class)
    public void updateUser_ShouldThrowAuthorizationException_whenUserDoesntExist(){
        RegisteredUser regUser = new RegisteredUser();
        regUser.setUsername("Primer");

        this.registeredUserService.update(regUser,"");

    }

    @Test(expected = BadRequestException.class)
    public void updateUser_ShouldThrowBadRequestException_whenEmailIsTaken(){
        RegisteredUser regUser = new RegisteredUser();
        regUser.setUsername("kaca");
        regUser.setEmail("kacjica+1@gmail.com");

        this.registeredUserService.update(regUser,"");

    }

    @Test
    public void updateUser_ShouldPass_whenUserUpdateIsValid(){
        RegisteredUser regUser = new RegisteredUser();
        regUser.setUsername("kaca");
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
