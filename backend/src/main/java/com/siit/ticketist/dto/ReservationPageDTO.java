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
public class ReservationPageDTO {
    private List<ReservationDTO> reservations;
    private Long totalSize;
}
