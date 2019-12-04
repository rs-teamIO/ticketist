package com.siit.ticketist.controller.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OptimisticLockException extends RuntimeException{

    public OptimisticLockException(String message) { super(message); }

}
