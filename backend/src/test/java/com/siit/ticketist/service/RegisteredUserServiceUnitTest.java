package com.siit.ticketist.service;
import com.siit.ticketist.exceptions.AuthorizationException;
import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.exceptions.NotFoundException;
import com.siit.ticketist.model.RegisteredUser;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/data.sql")
public class RegisteredUserServiceUnitTest {

    @Autowired
    private RegisteredUserService registeredUserService;

    @MockBean
    private UserService userService;

    @Test(expected = AuthorizationException.class)
    public void updateUser_ShouldThrowAuthorizationException_whenUserDoesntExistUnit(){
        RegisteredUser regUser = new RegisteredUser();
        regUser.setUsername("Primer");

        Mockito.when(userService.findRegisteredUserByUsername(regUser.getUsername())).thenThrow(AuthorizationException.class);
        this.registeredUserService.update(regUser,"");

    }

    @Test(expected = BadRequestException.class)
    public void updateUser_ShouldThrowBadRequestException_whenEmailIsTakenUnit(){
        RegisteredUser regUser = new RegisteredUser();
        regUser.setUsername("kaca");
        regUser.setEmail("kacjica+1@gmail.com");


        Mockito.when(userService.findRegisteredUserByUsername(regUser.getUsername())).thenReturn(regUser);
        Mockito.doThrow(BadRequestException.class).when(userService).checkIfEmailTaken(regUser.getEmail());
        this.registeredUserService.update(regUser,"");

    }

    @Test
    public void updateUser_ShouldPass_whenUserUpdateIsValidUnit(){
        RegisteredUser regUser = new RegisteredUser();
        regUser.setUsername("kaca");
        regUser.setEmail("kaca@gmail.com");
        regUser.setFirstName("First");
        regUser.setLastName("Last");
        regUser.setPhone("051221");


        Mockito.when(userService.findRegisteredUserByUsername(regUser.getUsername())).thenReturn(regUser);
        Mockito.when(userService.save(regUser)).thenReturn(regUser);
        RegisteredUser test = this.registeredUserService.update(regUser,"newPassword");
        assertEquals(test.getUsername(),regUser.getUsername());
        assertEquals(test.getPassword(),regUser.getPassword());
        assertEquals(test.getFirstName(),regUser.getFirstName());
        assertEquals(test.getLastName(),regUser.getLastName());
        assertEquals(test.getPhone(),regUser.getPhone());
        assertEquals(test.getEmail(),regUser.getEmail());
    }
}
