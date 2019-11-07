package com.siit.ticketist.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Basic protected REST Controller. Used to test security/authorities.
 */
@RestController
@RequestMapping("/api/protected")
public class ProtectedController {

    /**
     * GET /api/protected/user
     * Endpoint used for testing access for registered users.
     *
     * @return ResponseEntity containing HttpStatus and message of the operation result
     */
    @GetMapping(value = "/user")
    @PreAuthorize("hasAuthority('REGISTERED_USER')")
    public ResponseEntity<?> registeredUserTest() {
        return new ResponseEntity<>("REGISTERED_USER Test successful.", HttpStatus.OK);
    }

    /**
     * GET /api/protected/admin
     * Endpoint used for testing access for administrators.
     *
     * @return ResponseEntity containing HttpStatus and message of the operation result
     */
    @GetMapping(value = "/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> adminTest() {
        return new ResponseEntity<>("ADMIN Test successful.", HttpStatus.OK);
    }
}
