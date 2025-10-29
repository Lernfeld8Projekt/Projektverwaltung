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
        validateProjectEntity(projectEntity);
        return this.projectRepository.save(projectEntity);
    }

    public ProjectEntity patchProject(Long id, ProjectEntity patchedEntity) {
        ProjectEntity entityToPatch = this.projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found on id: " + id));

        validateProjectEntity(patchedEntity);

        updateFields(entityToPatch, patchedEntity);

        return projectRepository.save(entityToPatch);
    }

    private void updateFields(ProjectEntity entityToPatch, ProjectEntity patchedEntity) {
        if (patchedEntity.getTitle() != null) {
            entityToPatch.setTitle(patchedEntity.getTitle());
        }

        if (patchedEntity.getResponsibleEmployeeId() != null) {
            entityToPatch.setResponsibleEmployeeId(patchedEntity.getResponsibleEmployeeId());
        }

        if (patchedEntity.getCustomerId() != null) {
            entityToPatch.setCustomerId(patchedEntity.getCustomerId());
        }

        if (patchedEntity.getCustomerRepresentativeName() != null) {
            entityToPatch.setCustomerRepresentativeName(patchedEntity.getCustomerRepresentativeName());
        }

        if (patchedEntity.getGoal() != null) {
            entityToPatch.setGoal(patchedEntity.getGoal());
        }

        if (patchedEntity.getStartDate() != null) {
            entityToPatch.setStartDate(patchedEntity.getStartDate());
        }

        if (patchedEntity.getPlannedEndDate() != null) {
            entityToPatch.setPlannedEndDate(patchedEntity.getPlannedEndDate());
        }

        if (patchedEntity.getActualEndDate() != null) {
            entityToPatch.setActualEndDate(patchedEntity.getActualEndDate());
        }
    }

    private void validateProjectEntity(ProjectEntity projectEntity) {
        if (projectEntity.getStartDate().isAfter(projectEntity.getPlannedEndDate())) {
            throw new DateNotValidException("Start date cannot be after planned end date!");
        }

        if (projectEntity.getActualEndDate() != null && projectEntity.getActualEndDate().isBefore(projectEntity.getStartDate())) {
            throw new DateNotValidException("Actual end date cannot be before start date!");
        }

        if (!customerService.checkIfCustomerExists(projectEntity.getCustomerId())) {
            throw new ResourceNotFoundException("Customer not found on id: " + projectEntity.getCustomerId());
        }
        if (!employeeService.checkIfEmployeeExists(projectEntity.getResponsibleEmployeeId())) {
            throw new ResourceNotFoundException("Employee not found on id: " + projectEntity.getResponsibleEmployeeId());
        }
    }
}
