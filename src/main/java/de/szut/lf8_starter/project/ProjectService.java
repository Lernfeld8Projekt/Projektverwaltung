package de.szut.lf8_starter.project;

import de.szut.lf8_starter.customer.CustomerService;
import de.szut.lf8_starter.employee.EmployeeService;
import de.szut.lf8_starter.exceptionHandling.DateNotValidException;
import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {
    private final CustomerService customerService;
    private final EmployeeService employeeService;
    private final ProjectRepository projectRepository;

    public ProjectService(CustomerService customerService, EmployeeService employeeService, ProjectRepository projectRepository) {
        this.customerService = customerService;
        this.employeeService = employeeService;
        this.projectRepository = projectRepository;
    }

    public ProjectEntity createProject(ProjectEntity projectEntity) {
        validateAddProjectDTO(projectEntity);
        return this.projectRepository.save(projectEntity);
    }

    void validateAddProjectDTO(ProjectEntity projectEntity) {
        if (projectEntity.getStartDate().isAfter(projectEntity.getPlannedEndDate())) {
            throw new DateNotValidException("Start date cannot be after planned end date!");
        }
        if (!customerService.checkIfCustomerExists(projectEntity.getCustomerId())) {
            throw new ResourceNotFoundException("Customer not found on id: " + projectEntity.getCustomerId());
        }
        if (!employeeService.checkIfEmployeeExists(projectEntity.getResponsibleEmployeeId())) {
            throw new ResourceNotFoundException("Employee not found on id: " + projectEntity.getResponsibleEmployeeId());
        }
    }

    public void deleteProjectById(final Long id) {
        ProjectEntity projectEntity = this.projectRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Project with " + id + " not found."));
        this.projectRepository.delete(projectEntity);
    }
}
