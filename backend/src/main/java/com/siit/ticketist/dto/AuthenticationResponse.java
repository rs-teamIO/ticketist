package com.siit.ticketist.dto;

import lombok.Getter;

@Getter
public class AuthenticationResponse {

    private static String message = "Authentication successful.";
    private String token;

    public AuthenticationResponse(String token) {
        this.token = token;
    }
}
