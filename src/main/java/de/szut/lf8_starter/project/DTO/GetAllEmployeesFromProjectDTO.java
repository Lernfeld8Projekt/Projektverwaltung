package de.szut.lf8_starter.project.DTO;

import de.szut.lf8_starter.project.ProjectAssignment;
import lombok.Data;

import java.util.List;

@Data
public class GetAllEmployeesFromProjectDTO {
    private Long id;
    private String title;
    private List<GetEmployeeDTO> employees;
}
