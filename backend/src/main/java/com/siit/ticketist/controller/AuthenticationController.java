package com.siit.ticketist.controller;

import com.siit.ticketist.dto.AuthenticationRequest;
import com.siit.ticketist.dto.AuthenticationResponse;
import com.siit.ticketist.model.User;
import com.siit.ticketist.security.TokenUtils;
import com.siit.ticketist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Authentication REST Controller
 */
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final TokenUtils tokenUtils;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
                                    UserService userService, TokenUtils tokenUtils) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.tokenUtils = tokenUtils;
    }

    /**
     * POST /api/auth
     * Endpoint used for user authentication.
     *
     * @param authenticationRequest DTO containing login credentials
     * @return ResponseEntity containing the generated JSON Web Token
     */
    @PostMapping
    public ResponseEntity authenticate(@Valid @RequestBody AuthenticationRequest authenticationRequest) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()
                    )
            );

            // SecurityContextHolder.getContext().setAuthentication(authentication);

            final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
            final User user = userService.findByUsername(userDetails.getUsername());
            final String token = tokenUtils.generateToken(userDetails);
            return new ResponseEntity<>(new AuthenticationResponse(token), HttpStatus.OK);

        } catch (Exception ex) {
            return new ResponseEntity<>("User credentials invalid.", HttpStatus.UNAUTHORIZED);
        }
    }
}
