package de.szut.lf8_starter.project.DTO.GetDTO;

import lombok.Data;

import java.util.List;

@Data
public class GetAllEmployeesFromProjectDTO {
    private Long id;
    private String title;
    private List<GetEmployeeDTO> employees;
}
