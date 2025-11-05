package de.szut.lf8_starter.project.DTO.GetDTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GetProjectFromEmployeeDTO {
    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate plannedEndDate;
    private LocalDate actualEndDate;
    private Long employeeQualification;
}
