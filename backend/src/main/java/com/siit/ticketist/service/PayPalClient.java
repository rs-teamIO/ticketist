package com.siit.ticketist.service;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.siit.ticketist.controller.exceptions.PayPalException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PayPalClient {

    String clientId = "Ae4XZ3p5PVrf7rzLUo-vVJF3d9hWR0bSGLtMebAkSP0qzUPQexV4hnZfr5qnVwlXU7OrIcA7tqbtwNyg";
    String clientSecret = "EBGTUyAnZo5zNZuSRLDmvWTMLRVD7WxpPjxbv31rGrBI6GVQbdWyCQProk_dJCAygoes1yVTjpQ60-sP";

    public Map<String, Object> createPayment(String sum){
        Map<String, Object> response = new HashMap<String, Object>();

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
        redirectUrls.setCancelUrl("http://localhost:8080/cancel");
        redirectUrls.setReturnUrl("http://localhost:8080/");
        payment.setRedirectUrls(redirectUrls);

        Payment createdPayment;
        try{
            String redirectUrl = "";
            APIContext context = new APIContext(clientId, clientSecret, "sandbox");
            createdPayment = payment.create(context);

            if(createdPayment != null){
                List<Links> links = createdPayment.getLinks();
                for(Links link: links){
                    if(link.getRel().equals("approval_url")){
                        redirectUrl = link.getHref();
                        break;
                    }
                }
                response.put("status", "success");
                response.put("redirect_url", redirectUrl);
            }

        }catch(PayPalRESTException ex){
            System.out.println("Error happened during payment creation!");
        }

        return response;
    }

    public Map<String, Object> completePayment(HttpServletRequest req) throws PayPalException {
        Map<String, Object> response = new HashMap();
        Payment payment = new Payment();
        payment.setId(req.getParameter("paymentId"));

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(req.getParameter("PayerID"));
        try{
            APIContext context = new APIContext(clientId, clientSecret, "sandbox");
            Payment createdPayment = payment.execute(context, paymentExecution);
            if(createdPayment != null){
                response.put("status", "success");
                response.put("payment", createdPayment);
            }
        }catch(PayPalRESTException ex){
            throw new PayPalException(ex.getMessage());
        }
        return response;
    }
}
