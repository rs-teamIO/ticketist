package com.siit.ticketist.controller.exceptions;

import com.siit.ticketist.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.mail.MessagingException;

/**
 * Provides global exception handling.
 */
@ControllerAdvice
public class ExceptionResolver {

    // TODO: Add exception handlers here

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity handleAuthorizationException(AuthorizationException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity handleBadRequestException(BadRequestException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity handleForbiddenException(ForbiddenException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity handleNotFoundException(NotFoundException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity handleMessagingException(MessagingException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
