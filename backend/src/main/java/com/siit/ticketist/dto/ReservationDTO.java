package com.siit.ticketist.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter @NoArgsConstructor
public class ReservationDTO {
    private Long id;

    @NotBlank(message = "event name cannot be blank")
    private String eventName;

    @NotBlank(message = "venue name cannot be blank")
    private String venueName;

    @NotNull(message = "price cannot be null")
    private Double price;

    @NotNull(message = "size cannot be null")
    private Integer ticketCount;

}
