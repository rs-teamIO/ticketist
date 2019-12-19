package com.siit.ticketist.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class InitialReportDTO {

    private Map<String, BigDecimal> allVenuesChart;

    private List<EventReportDTO> events;

    public InitialReportDTO(Map<String, BigDecimal> allVenuesChart, List<Object[]> events) {
        this.allVenuesChart = allVenuesChart;
        this.events = events
                .stream()
                .map(obj -> new EventReportDTO((String) obj[0], (String) obj[1], ((BigInteger) obj[2]), (BigDecimal) obj[3]))
                .collect(Collectors.toList());
    }
}
