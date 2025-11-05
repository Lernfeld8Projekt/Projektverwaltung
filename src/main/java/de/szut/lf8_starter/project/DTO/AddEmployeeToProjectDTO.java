package de.szut.lf8_starter.project.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddEmployeeToProjectDTO {
    @NotNull(message = "Employee ID ist mandatory")
    private Long employeeId;
    @NotNull(message = "Qualification ID is mandatory")
    private Long qualification;
}
