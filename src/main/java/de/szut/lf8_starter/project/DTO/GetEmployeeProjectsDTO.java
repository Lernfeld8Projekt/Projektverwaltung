package de.szut.lf8_starter.project.DTO;

import lombok.Data;

import java.util.List;

@Data
public class GetEmployeeProjectsDTO {
    private Long employeeId;
    private String employeeFirstName;
    private String employeeLastName;
    private List<GetEmployeeReducedProjectDTO> projects;
}
