package com.siit.ticketist.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventReportDTO eventReportDTO = (EventReportDTO) o;
        if (ticketsSold.compareTo(eventReportDTO.ticketsSold) != 0) return false;
        if (totalRevenue.compareTo(eventReportDTO.totalRevenue) != 0) return false;
        return Objects.equals(name, eventReportDTO.name) && Objects.equals(venueName, eventReportDTO.name);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }
}
