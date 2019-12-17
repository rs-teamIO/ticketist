package com.siit.ticketist.dto;

import lombok.Getter;

@Getter
public class SuccessResponse {

    private String message;

    public SuccessResponse(String message) {
        this.message = message;
    }
}
