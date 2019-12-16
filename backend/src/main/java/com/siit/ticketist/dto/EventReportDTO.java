package com.siit.ticketist.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

public class EventReportDTO {

    public String name;
    public String venueName;
    public BigInteger ticketsSold;
    public BigDecimal totalRevenue;

    public EventReportDTO(String name, String venueName, BigInteger ticketsSold, BigDecimal totalRevenue){
        this.name = name;
        this.venueName = venueName;
        this.ticketsSold = ticketsSold;
        this.totalRevenue = totalRevenue;
    }
}
