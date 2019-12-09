package com.siit.ticketist.exceptions;

import lombok.NoArgsConstructor;

/**
 * This exception is thrown when an optimistic locking conflict occurs.
 */
@NoArgsConstructor
public class OptimisticLockException extends RuntimeException {

    public OptimisticLockException(String message) { super(message); }
}
