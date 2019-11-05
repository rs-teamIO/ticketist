package com.siit.ticketist.dto;

import com.siit.ticketist.model.Sector;
import com.siit.ticketist.model.Venue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter
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

    public VenueDTO() {
        sectors = new HashSet<>();
    }

    public VenueDTO(Venue venue) {
        this.id = venue.getId();
        this.name = venue.getName();
        this.street = venue.getStreet();
        this.city = venue.getCity();
        this.latitude = venue.getLatitude();
        this.longitude = venue.getLongitude();
        Set<SectorDTO> sectors = new HashSet<>();
        for(Sector sector : venue.getSectors()) {
            sectors.add(new SectorDTO(sector));
        }
        this.setSectors(sectors);
    }

    public Venue convertToEntity() {
        Venue venue = new Venue();
        venue.setId(this.id);
        venue.setName(this.name);
        venue.setStreet(this.street);
        venue.setCity(this.city);
        venue.setLongitude(this.longitude);
        venue.setLatitude(this.latitude);
        venue.setIsActive(true);
        Set<Sector> sectors = new HashSet<>();
        for(SectorDTO sector : this.sectors) {
            sectors.add(sector.convertToEntity());
        }
        venue.setSectors(sectors);
        return venue;
    }

}
