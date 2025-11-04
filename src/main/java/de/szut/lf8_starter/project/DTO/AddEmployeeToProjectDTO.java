package de.szut.lf8_starter.project.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddEmployeeToProjectDTO {
    @NotBlank(message = "Employee ID ist mandatory")
    private Long employeeId;
    @NotBlank(message = "Qualification ID is mandatory")
    private Long qualification;
}
