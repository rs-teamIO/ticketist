package com.siit.ticketist.controller;

import com.siit.ticketist.dto.RegisterUserDto;
import com.siit.ticketist.dto.SuccessResponse;
import com.siit.ticketist.dto.UpdateUserDto;
import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.model.RegisteredUser;
import com.siit.ticketist.service.RegisteredUserService;
import com.siit.ticketist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.Objects;

/**
 * Registered User management REST controller.
 */
@RestController
@RequestMapping("/api/users")
public class RegisteredUserController {

    private final RegisteredUserService registeredUserService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegisteredUserController(RegisteredUserService registeredUserService, UserService userService, PasswordEncoder passwordEncoder) {
        this.registeredUserService = registeredUserService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * POST /api/users
     * Registers a new {@link RegisteredUser}.
     *
     * @param RegisterUserDto DTO containing User info
     * @return {@link ResponseEntity} containing the ID of created User
     */
    @PostMapping
    public ResponseEntity handleCreate(@Valid @RequestBody RegisterUserDto dto) throws MessagingException {
        userService.checkIfUsernameTaken(dto.getUsername());
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        final RegisteredUser registeredUser = registeredUserService.create(dto.convertToEntity());
        return new ResponseEntity<>(registeredUser.getId(), HttpStatus.CREATED);
    }

    /**
     * GET /api/users/verify/{verificationCode}
     * Endpoint used for {@link RegisteredUser} account verification.
     *
     * @param verificationCode Verification code string
     * @return {@link ResponseEntity} containing HttpStatus and message of the operation result
     */
    @GetMapping(value="verify/{verificationCode}")
    public ResponseEntity handleVerify(@PathVariable("verificationCode") String verificationCode) {
        registeredUserService.verify(verificationCode);
        return new ResponseEntity(new SuccessResponse("User verified successfully."), HttpStatus.OK);
    }

    @GetMapping(value = "/{username}")
    public ResponseEntity findUserByUsername(@PathVariable("username") String username){
        RegisteredUser user = (RegisteredUser) userService.findByUsername(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping
    //@PreAuthorize("hasAuthority('REGISTERED_USER')")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UpdateUserDto dto) {

        if(!dto.getNewPassword().equals(dto.getNewPasswordRepeat()))
            throw new BadRequestException("New passwords do not match.");

        RegisteredUser updatedRegisteredUser = dto.convertToEntity();
        updatedRegisteredUser = this.userService.update(updatedRegisteredUser, dto.getNewPassword());

        // TODO: Convert user to DTO and return
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
