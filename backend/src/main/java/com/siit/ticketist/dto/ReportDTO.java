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

    //naziv venue-a - ukupna zarada
    //TODO prikazivati zaradu u prethodnih 12 meseci ili za zivota (trenutno je za zivota)
    Map<String, BigDecimal> allVenuesChart;

    List<EventReportDTO> events;

    Map<Integer, Float> singleVenueChart;

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

    public ReportDTO generateInitialReport(Map<String, BigDecimal> allVenuesChart, List<Object[]> events){
        this.allVenuesChart = allVenuesChart;
        this.events = events
                .stream()
                .map(obj -> new EventReportDTO((String)obj[0], (String)obj[1], ((BigInteger)obj[2]), (BigDecimal)obj[3]))
                .collect(Collectors.toList());
        this.singleVenueChart = null;
        return this;
    }

    public ReportDTO generateSpecificReport(Map<Integer, Float> singleVenueChart, List<Object[]> events){
        this.singleVenueChart = singleVenueChart;
        this.events = events
                .stream()
                .map(obj -> new EventReportDTO((String)obj[0], (String)obj[1], (BigInteger)obj[2], (BigDecimal)obj[3]))
                .collect(Collectors.toList());
        this.allVenuesChart = null;
        return this;
    }



}
