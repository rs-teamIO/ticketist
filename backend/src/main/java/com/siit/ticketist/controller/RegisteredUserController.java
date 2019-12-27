package com.siit.ticketist.controller;

import com.siit.ticketist.dto.RegisterUserDto;
import com.siit.ticketist.dto.SuccessResponse;
import com.siit.ticketist.dto.UpdateUserDto;
import com.siit.ticketist.exceptions.AuthorizationException;
import com.siit.ticketist.model.RegisteredUser;
import com.siit.ticketist.model.User;
import com.siit.ticketist.service.RegisteredUserService;
import com.siit.ticketist.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;

/**
 * Registered User management REST controller.
 */
@RestController
@RequestMapping("/api/users")
public class RegisteredUserController {

    private final RegisteredUserService registeredUserService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

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

    /**
     * GET /api/users/me
     * Endpoint used to get the currently active {@link RegisteredUser}
     *
     * @return {@link ResponseEntity} containing HttpStatus and current user data
     */
    @GetMapping(value = "me")
    @PreAuthorize("hasAuthority('REGISTERED_USER')")
    public ResponseEntity getCurrentUser() {
        final User user = this.userService.findCurrentUser();
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    /**
     * PUT /api/users
     * Updates the {@link RegisteredUser} data
     *
     * @param dto DTO containing updated {@link RegisteredUser} data
     * @return {@link ResponseEntity} containing HttpStatus and updated user data
     */
    @PutMapping
    @PreAuthorize("hasAuthority('REGISTERED_USER')")
    public ResponseEntity<RegisterUserDto> updateUser(@Valid @RequestBody UpdateUserDto dto) {
        if(dto.getNewPassword() != null && (!dto.getNewPassword().equals(dto.getNewPasswordRepeat())))
            throw new AuthorizationException("New passwords do not match.");

        final User currentUser = this.userService.findCurrentUser();
        final RegisteredUser registeredUser = this.userService.findRegisteredUserByUsername(dto.getUsername());

        if(!currentUser.getUsername().equalsIgnoreCase(registeredUser.getUsername()))
            throw new AuthorizationException("Usernames don't match.");

        Boolean oldPasswordCorrect = passwordEncoder.matches(dto.getOldPassword(), registeredUser.getPassword());
        if(!oldPasswordCorrect.booleanValue())
            throw new AuthorizationException("Incorrect password.");

        dto.setOldPassword(passwordEncoder.encode(dto.getOldPassword()));
        if(dto.getNewPassword() != null) {
            dto.setNewPassword(passwordEncoder.encode(dto.getNewPassword()));
            dto.setNewPasswordRepeat(passwordEncoder.encode(dto.getNewPasswordRepeat()));
        }

        final RegisteredUser updatedRegisteredUser = this.registeredUserService.update(dto.convertToEntity(), dto.getNewPassword());

        return new ResponseEntity<>(new RegisterUserDto(updatedRegisteredUser), HttpStatus.OK);
    }
}
