package com.siit.ticketist.controller.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NotFoundException extends Exception{
    public NotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
