package com.siit.ticketist.dto;

import com.siit.ticketist.model.Sector;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SectorDTO {

    private Long id;

    @NotBlank(message = "name cannot be blank")
    private String name;

    @NotNull(message = "rows count cannot be null")
    private Integer rowsCount;

    @NotNull(message = "columns count cannot be null")
    private Integer columnsCount;

    @NotNull(message = "capacity cannot be null")
    private Integer maxCapacity;

    @NotNull(message = "start row cannot be null")
    private Integer startRow;

    @NotNull(message = "start column cannot be null")
    private Integer startColumn;

    public SectorDTO(Sector sector) {
        this.id = sector.getId();
        this.name = sector.getName();
        this.rowsCount = sector.getRowsCount();
        this.columnsCount = sector.getColumnsCount();
        this.maxCapacity = sector.getMaxCapacity();
        this.startRow = sector.getStartRow();
        this.startColumn = sector.getStartColumn();
    }

    public Sector convertToEntity() {
        Sector sector = new Sector();
        sector.setId(this.id);
        sector.setName(this.name);
        sector.setRowsCount(this.rowsCount);
        sector.setColumnsCount(this.columnsCount);
        sector.setMaxCapacity(this.maxCapacity);
        sector.setStartRow(this.startRow);
        sector.setStartColumn(this.startColumn);
        return sector;
    }

}
