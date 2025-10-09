package de.szut.lf8_starter.project.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AddProjectDTO {
    @NotBlank(message = "Title is mandatory")
    private String title;
    @NotBlank(message = "Responsible Employee ID is mandatory")
    private Long responsibleEmployeeId;
    @NotBlank(message = "Customer ID is mandatory")
    private Long customerId;
    @NotBlank(message = "Customer representative name is mandatory")
    private String customerRepresentativeName;
    private String goal;
    private LocalDate startDate;
    private LocalDate plannedEndDate;
}
