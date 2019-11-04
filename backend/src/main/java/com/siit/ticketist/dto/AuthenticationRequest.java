package com.siit.ticketist.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter @NoArgsConstructor
public class AuthenticationRequest {

    @NotBlank(message = "Username may not be blank")
    private String username;
    @NotBlank(message = "Password may not be blank")
    private String password;
}
