package de.szut.lf8_starter.project.DTO;

import lombok.Data;

@Data
public class GetProjectEmployeeDTO {
    private Long id;
    private String title;
    private Long employeeId;
    private String employeeName;
    private String qualification;
}
