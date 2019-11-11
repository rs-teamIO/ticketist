package com.siit.ticketist.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchDTO {

    private String eventName = "";
    private String category = "";
    private String venueName = "";
    private Long startDate = null;
    private Long endDate = null;

}
