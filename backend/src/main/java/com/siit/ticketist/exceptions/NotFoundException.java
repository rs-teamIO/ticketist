package com.siit.ticketist.exceptions;

import lombok.NoArgsConstructor;

/**
 * This exception is thrown in case the requested resource is not found on the server.
 */
@NoArgsConstructor
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) { super(message); }
}
