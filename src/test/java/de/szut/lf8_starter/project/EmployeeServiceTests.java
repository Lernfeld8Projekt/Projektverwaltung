package de.szut.lf8_starter.project;

import de.szut.lf8_starter.employee.EmployeeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EmployeeServiceTests {

    @Test
    public void checkIfEmployeeHaveQualification_shouldReturnTrue(){
        EmployeeService employeeService = new EmployeeService();

        Long employeeId = 1L;
        Long qualificationId = 2L;
        boolean hasQualification = employeeService.checkIfEmployeeHaveQualification(employeeId, qualificationId);

        Assertions.assertTrue(hasQualification);
    }
}