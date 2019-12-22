package com.siit.ticketist.dto;

import com.siit.ticketist.model.Category;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Date;

@Getter @Setter
public class EventUpdateDTO {

    @NotBlank(message = "name cannot be blank")
    private String name;

    @NotNull(message = "category cannot be null")
    private Category category;

    @NotNull(message = "reservation deadline cannot be null")
    @Future(message = "reservation deadline must be in future")
    private Date reservationDeadline;

    @NotNull(message = "reservation limit cannot be null")
    @Positive(message = "reservation limit must be a positive number")
    private Integer reservationLimit;

    @NotBlank(message = "description cannot be blank")
    private String description;
}
