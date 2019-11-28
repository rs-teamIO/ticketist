package com.siit.ticketist.service;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.siit.ticketist.controller.exceptions.PayPalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PayPalService {

    @Autowired
    private APIContext apiContext;

    public Map<String, Object> createPayment(String sum) throws PayPalRESTException {

        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(sum);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("http://localhost:8080/api/paypal/cancel");
        redirectUrls.setReturnUrl("http://localhost:8080/api/paypal/confirm");
        payment.setRedirectUrls(redirectUrls);

        Payment createdPayment = payment.create(apiContext);
        return createResponse(createdPayment);
    }

    private Map<String, Object> createResponse(Payment createdPayment) {
        Map<String, Object> response = new HashMap<String, Object>();
        String redirectUrl = "";

        if (createdPayment != null) {
            List<Links> links = createdPayment.getLinks();
            for (Links link : links) {
                if (link.getRel().equals("approval_url")) {
                    redirectUrl = link.getHref();
                    break;
                }
            }
            response.put("redirect_url", redirectUrl);
        }
        return response;
    }


    public Map<String, Object> completePayment(HttpServletRequest req) throws PayPalRESTException {
        Map<String, Object> response = new HashMap<String, Object>();

        Payment payment = new Payment();
        payment.setId(req.getParameter("paymentId"));

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(req.getParameter("PayerID"));

        Payment createdPayment = payment.execute(apiContext, paymentExecution);
        if (createdPayment != null)
            response.put("payment", createdPayment);

        return response;

    }


}
