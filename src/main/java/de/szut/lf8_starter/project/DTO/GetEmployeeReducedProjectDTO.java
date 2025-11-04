package de.szut.lf8_starter.project.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GetEmployeeReducedProjectDTO {
    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate plannedEndDate;
    private LocalDate actualEndDate;
    private Long employeeQualification;
}
