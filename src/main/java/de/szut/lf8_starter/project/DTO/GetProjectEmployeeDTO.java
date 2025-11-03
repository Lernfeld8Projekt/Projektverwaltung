package de.szut.lf8_starter.project.DTO;

import lombok.Data;

@Data
public class GetProjectEmployeeDTO {
    private Long projectId;
//    private String title;
    private Long employeeId;
    private String employeeLastName;
    private String employeeFirstName;
    private Long qualification;
}
