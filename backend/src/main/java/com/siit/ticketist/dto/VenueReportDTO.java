package com.siit.ticketist.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class VenueReportDTO {

    private Map<Integer, Float> singleVenueChart;

    private List<EventReportDTO> events;

    public VenueReportDTO(Map<Integer, Float> singleVenueChart, List<Object[]> events) {
        this.singleVenueChart = singleVenueChart;
        this.events = events
                .stream()
                .map(obj -> new EventReportDTO((String) obj[0], (String) obj[1], (BigInteger) obj[2], (BigDecimal) obj[3]))
                .collect(Collectors.toList());
    }

}
