package com.siit.ticketist.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InitialReportDTO {

    Map<String, BigDecimal> allVenuesChart;

    List<EventReportDTO> events;

    public InitialReportDTO(Map<String, BigDecimal> allVenuesChart, List<Object[]> events){
        this.allVenuesChart = allVenuesChart;
        this.events = events
                .stream()
                .map(obj -> new EventReportDTO((String)obj[0], (String)obj[1], ((BigInteger)obj[2]), (BigDecimal)obj[3]))
                .collect(Collectors.toList());
    }
}
