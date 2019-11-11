package com.siit.ticketist.controller;

import com.siit.ticketist.controller.exceptions.PayPalException;
import com.siit.ticketist.service.PayPalClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.Response;
import java.util.Map;

@RestController
@RequestMapping(value="/api/paypal")
public class PayPalController {

    private final PayPalClient payPalClient;

    @Autowired
    PayPalController(PayPalClient payPalClient){
        this.payPalClient = payPalClient;
    }

    @PostMapping(value = "/make/payment")
    public Map<String, Object> makePayment(@RequestParam("sum") String sum){
        return payPalClient.createPayment(sum);
    }

    @PostMapping(value = "/complete/payment")
    public ResponseEntity completePayment(HttpServletRequest request) throws PayPalException {
        Map<String, Object> temp = payPalClient.completePayment(request);
        return new ResponseEntity("Payment successful! :)", HttpStatus.OK);
    }
}
