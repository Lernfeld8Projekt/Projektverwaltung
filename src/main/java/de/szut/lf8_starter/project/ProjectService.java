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

    public ProjectEntity patchProject(Long id, ProjectEntity patchedEntity) {
        ProjectEntity entityToPatch = this.projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Das Projekt mit der ID " + id + " konnte nicht gefunden werden."));

        if (patchedEntity.getTitle() != null) {
            entityToPatch.setTitle(patchedEntity.getTitle());
        }

        if (patchedEntity.getResponsibleEmployeeId() != 0) {
            if (!employeeService.checkIfEmployeeExists(patchedEntity.getResponsibleEmployeeId())) {
                throw new ResourceNotFoundException("Employee not found on id: " + patchedEntity.getResponsibleEmployeeId());
            }
            entityToPatch.setResponsibleEmployeeId(patchedEntity.getResponsibleEmployeeId());
        }

        if (patchedEntity.getCustomerId() != 0){
            if (!customerService.checkIfCustomerExists(patchedEntity.getCustomerId())) {
                throw new ResourceNotFoundException("Customer not found on id: " + patchedEntity.getCustomerId());
            }
            entityToPatch.setCustomerId(patchedEntity.getCustomerId());
        }

        if (patchedEntity.getCustomerRepresentativeName() != null) {
            entityToPatch.setCustomerRepresentativeName(patchedEntity.getCustomerRepresentativeName());
        }

        if (patchedEntity.getGoal() != null) {
            entityToPatch.setGoal(patchedEntity.getGoal());
        }

        if (patchedEntity.getStartDate() != null) {
            if (patchedEntity.getStartDate().isAfter(entityToPatch.getPlannedEndDate())){
                throw new DateNotValidException("Start date cannot be after planned end date!");
            }
            if (entityToPatch.getActualEndDate() != null && patchedEntity.getStartDate().isAfter(entityToPatch.getActualEndDate())) {
                throw new DateNotValidException("Start date cannot be after the actual end date!");
            }
            entityToPatch.setStartDate(patchedEntity.getStartDate());
        }

        if (patchedEntity.getPlannedEndDate() != null) {
            if (patchedEntity.getPlannedEndDate().isBefore(entityToPatch.getStartDate())) {
                throw new DateNotValidException("Planned end date cannot be before the start date!");
            }
            entityToPatch.setPlannedEndDate(patchedEntity.getPlannedEndDate());
        }

        if (patchedEntity.getActualEndDate() != null) {
            if (entityToPatch.getActualEndDate() != null && patchedEntity.getActualEndDate().isBefore(entityToPatch.getActualEndDate())){
                throw new DateNotValidException("Actual end date cannot be before the start date!");
            }
        }
        return entityToPatch;
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
}
