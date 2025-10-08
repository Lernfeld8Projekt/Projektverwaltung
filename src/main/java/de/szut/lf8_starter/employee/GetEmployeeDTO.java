package de.szut.lf8_starter.employee;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetEmployeeDTO {

    private Long eid;
    private String lastName;
    private String firstName;
    private String street;
    private String postcode;
    private String city;
    private String phone;

    private String skill;
}
