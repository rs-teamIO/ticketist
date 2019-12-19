package com.siit.ticketist.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter @Setter @NoArgsConstructor
public class EventReportDTO {

    private String name;
    private String venueName;
    private BigInteger ticketsSold;
    private BigDecimal totalRevenue;

    public EventReportDTO(String name, String venueName, BigInteger ticketsSold, BigDecimal totalRevenue) {
        this.name = name;
        this.venueName = venueName;
        this.ticketsSold = ticketsSold;
        this.totalRevenue = totalRevenue;
    }
}
