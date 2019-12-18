package com.siit.ticketist.controller;

import com.paypal.base.rest.PayPalRESTException;
import com.siit.ticketist.service.PayPalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * PayPal REST controller.
 */
@Controller
@RequestMapping(value="/api/paypal")
public class PayPalController {

    private final PayPalService payPalService;

    @Autowired
    PayPalController(PayPalService payPalService){
        this.payPalService = payPalService;
    }

    @PostMapping(value = "/make/payment")
    public ResponseEntity makePayment(@RequestParam("sum") String sum) throws PayPalRESTException {
        return new ResponseEntity<>(payPalService.createPayment(sum), HttpStatus.CREATED);
    }

    @PostMapping(value = "/complete/payment")
    public ResponseEntity completePayment(HttpServletRequest request) throws PayPalRESTException {
        payPalService.completePayment(request);
        return new ResponseEntity<>("Payment successful! :)", HttpStatus.OK);
    }

    /*
        Temporary endpoints
     */
    @GetMapping(value="/confirm")
    public String confirmPayment(HttpServletRequest request){
        return "confirm";
    }

    @GetMapping(value="/cancel")
    public ResponseEntity cancelPayment(){
        return new ResponseEntity<>("Payment cancelled", HttpStatus.I_AM_A_TEAPOT);
    }
}
