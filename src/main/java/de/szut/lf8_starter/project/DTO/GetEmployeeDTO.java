package de.szut.lf8_starter.project.DTO;

import lombok.Data;

@Data
public class GetEmployeeDTO {
    private Long id;
    private String lastName;
    private String firstName;
    private Long qualification;
}
