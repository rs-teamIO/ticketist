package com.siit.ticketist.dto;

import com.siit.ticketist.model.Venue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class VenueDTO {

    private Long id;

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

    @NotNull(message ="sector list cannot be null")
    private Set<SectorDTO> sectors;

    private Boolean isActive;

    public VenueDTO(Venue venue) {
        this.id = venue.getId();
        this.name = venue.getName();
        this.street = venue.getStreet();
        this.city = venue.getCity();
        this.latitude = venue.getLatitude();
        this.longitude = venue.getLongitude();
        this.sectors = new HashSet<>();
        venue.getSectors().stream()
                .map(SectorDTO::new)
                .forEach(this.sectors::add);
        this.isActive = venue.getIsActive();
    }

    public Venue convertToEntity() {
        Venue venue = new Venue();
        venue.setId(this.id);
        venue.setName(this.name);
        venue.setStreet(this.street);
        venue.setCity(this.city);
        venue.setLongitude(this.longitude);
        venue.setLatitude(this.latitude);
        venue.setIsActive(this.isActive);

        this.sectors.stream()
                .map(SectorDTO::convertToEntity)
                .forEach(venue.getSectors()::add);

        return venue;
    }
}
