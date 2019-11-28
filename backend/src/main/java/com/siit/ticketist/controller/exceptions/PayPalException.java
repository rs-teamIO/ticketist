package com.siit.ticketist.controller.exceptions;

import com.paypal.base.rest.PayPalRESTException;

public class PayPalException extends PayPalRESTException {

    public PayPalException(String message) {
        super(message);
    }

}
