package com.siit.ticketist.dto;

import com.siit.ticketist.model.Venue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter @NoArgsConstructor
public class VenueBasicDTO {

    @NotBlank(message = "Venue name cannot be empty")
    private String name;

    @NotBlank(message = "Street cannot be empty")
    private String street;

    @NotBlank(message = "City cannot be empty")
    private String city;

    @NotNull(message = "Latitude cannot be null")
    private Double latitude;

    @NotNull(message = "Longitude cannot be null")
    private Double longitude;

    public VenueBasicDTO(Venue venue) {
        this.name = venue.getName();
        this.street = venue.getStreet();
        this.city = venue.getCity();
        this.latitude = venue.getLatitude();
        this.longitude = venue.getLongitude();
    }
}
