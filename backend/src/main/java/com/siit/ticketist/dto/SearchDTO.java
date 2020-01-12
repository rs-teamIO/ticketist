package com.siit.ticketist.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class SearchDTO {

    private String eventName = "";
    private String category = "";
    private String venueName = "";
    private Long startDate = null;
    private Long endDate = null;

}
