package de.szut.lf8_starter.project;

import de.szut.lf8_starter.customer.CustomerService;
import de.szut.lf8_starter.employee.EmployeeService;
import de.szut.lf8_starter.exceptionHandling.DateNotValidException;
import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.project.DTO.AddProjectDTO;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {
    private final CustomerService customerService;
    private final EmployeeService employeeService;

    public ProjectService(CustomerService customerService, EmployeeService employeeService) {
        this.customerService = customerService;
        this.employeeService = employeeService;
    }

    boolean validateAddProjectDTO(AddProjectDTO addProjectDTO){
        if(addProjectDTO.getStartDate().isAfter(addProjectDTO.getPlannedEndDate())){
            throw new DateNotValidException("Start date cannot be after planned end date!");
        }
        if(!customerService.checkIfCustomerExists(addProjectDTO.getCustomerId())){
            throw new ResourceNotFoundException("Customer not found on id: " + addProjectDTO.getCustomerId());
        }
        if(!employeeService.checkIfEmployeeExists(addProjectDTO.getResponsibleEmployeeId())){
            throw new ResourceNotFoundException("Employee not found on id: " + addProjectDTO.getResponsibleEmployeeId());
        }
        return true;
    };
}
