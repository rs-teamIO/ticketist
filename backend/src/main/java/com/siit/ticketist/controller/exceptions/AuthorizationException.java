package com.siit.ticketist.controller.exceptions;

import lombok.NoArgsConstructor;

/**
 * This exception is thrown in case user authorization fails.
 */
@NoArgsConstructor
public class AuthorizationException extends RuntimeException {

    public AuthorizationException(String message) {
        super(message);
    }
}
