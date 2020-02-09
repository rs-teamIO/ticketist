package com.siit.ticketist.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AuthenticationResponse {

    private static String message = "Authentication successful.";
    private String token;
}
