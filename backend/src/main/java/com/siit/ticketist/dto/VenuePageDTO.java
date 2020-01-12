package com.siit.ticketist.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VenuePageDTO {

    private List<VenueDTO> venues;
    private Long totalSize;
}
