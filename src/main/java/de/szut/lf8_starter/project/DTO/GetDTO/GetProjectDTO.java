package de.szut.lf8_starter.project.DTO.GetDTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GetProjectDTO {
    private Long id;
    private String title;
    private Long responsibleEmployeeId;
    private Long customerId;
    private String customerRepresentativeName;
    private String goal;
    private LocalDate startDate;
    private LocalDate plannedEndDate;
    private LocalDate actualEndDate;
}
