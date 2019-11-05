package com.siit.ticketist.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class ReportDTO {

    //naziv venue-a - ukupna zarada (u prethodnih 12 meseci?)
    Map<String, BigDecimal> allVenuesChart;

    List<EventReportDTO> events;

    Map<BigDecimal, BigDecimal> singleVenueChart;

    public class EventReportDTO {

        public String name;
        public String venueName;
        public Integer ticketsSold;
        public BigDecimal totalRevenue;

        public EventReportDTO(String name, String venueName, Integer ticketsSold, BigDecimal totalRevenue){
            this.name = name;
            this.venueName = venueName;
            this.ticketsSold = ticketsSold;
            this.totalRevenue = totalRevenue;
        }
    }

    public ReportDTO(Map<String, BigDecimal> mapa, List<Object[]> lista){
        this.allVenuesChart = mapa;
        this.events = lista
                .stream()
                .map(obj -> new EventReportDTO((String)obj[0], (String)obj[1], ((BigInteger)obj[2]).intValue(), (BigDecimal)obj[3]))
                .collect(Collectors.toList());
        this.singleVenueChart = null;
    }

}
