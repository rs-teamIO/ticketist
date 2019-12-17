package com.siit.ticketist.exceptions;

import lombok.NoArgsConstructor;

/**
 * This exception is thrown in case of a bad request to the server.
 *
 * // TODO (fivkovic): This should be replaced with a Unprocessable Entity Exception in the future
 */
@NoArgsConstructor
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
