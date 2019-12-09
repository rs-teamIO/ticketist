package com.siit.ticketist.exceptions;

import com.paypal.base.rest.PayPalRESTException;

/**
 * This exception is thrown when an error on PayPal server occurs.
 */
public class PayPalException extends PayPalRESTException {

    public PayPalException(String message) {
        super(message);
    }
}
