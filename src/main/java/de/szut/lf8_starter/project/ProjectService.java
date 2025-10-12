package de.szut.lf8_starter.project;

import de.szut.lf8_starter.customer.CustomerService;
import de.szut.lf8_starter.employee.EmployeeService;
import de.szut.lf8_starter.exceptionHandling.DateNotValidException;
import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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

    private void validateAddProjectDTO(ProjectEntity projectEntity) {
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

    public ProjectEntity patchProject(Long id, ProjectEntity patchedEntity) {
        ProjectEntity entityToPatch = this.projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found on id: " + id));

        validateEmployeeAndCustomerExistence(patchedEntity);

        updateFields(entityToPatch, patchedEntity);
        updateDates(entityToPatch, patchedEntity);

        projectRepository.save(entityToPatch);
        return entityToPatch;
    }

    private void validateEmployeeAndCustomerExistence(ProjectEntity patchedEntity) {
        if (patchedEntity.getResponsibleEmployeeId() != 0 && !employeeService.checkIfEmployeeExists(patchedEntity.getResponsibleEmployeeId())) {
            throw new ResourceNotFoundException("Employee not found on id: " + patchedEntity.getResponsibleEmployeeId());
        }

        if (patchedEntity.getCustomerId() != 0 && !customerService.checkIfCustomerExists(patchedEntity.getCustomerId())) {
            throw new ResourceNotFoundException("Customer not found on id: " + patchedEntity.getCustomerId());
        }
    }

    private void updateFields(ProjectEntity entityToPatch, ProjectEntity patchedEntity) {
        if (patchedEntity.getTitle() != null) {
            entityToPatch.setTitle(patchedEntity.getTitle());
        }

        entityToPatch.setResponsibleEmployeeId(patchedEntity.getResponsibleEmployeeId());

        entityToPatch.setCustomerId(patchedEntity.getCustomerId());

        if (patchedEntity.getCustomerRepresentativeName() != null) {
            entityToPatch.setCustomerRepresentativeName(patchedEntity.getCustomerRepresentativeName());
        }

        if (patchedEntity.getGoal() != null) {
            entityToPatch.setGoal(patchedEntity.getGoal());
        }
    }

    private void updateDates(ProjectEntity entityToPatch, ProjectEntity patchedEntity) {
        LocalDate startDate = patchedEntity.getStartDate() != null
                ? patchedEntity.getStartDate()
                : entityToPatch.getStartDate();

        LocalDate plannedEndDate = patchedEntity.getPlannedEndDate() != null
                ? patchedEntity.getPlannedEndDate()
                : entityToPatch.getPlannedEndDate();

        LocalDate actualEndDate = patchedEntity.getActualEndDate() != null
                ? patchedEntity.getActualEndDate()
                : entityToPatch.getActualEndDate();

        if (actualEndDate != null && startDate != null && actualEndDate.isBefore(startDate)) {
            throw new DateNotValidException("Actual end date cannot be before start date!");
        }

        if (startDate != null && plannedEndDate != null && startDate.isAfter(plannedEndDate)) {
            throw new DateNotValidException("Start date cannot be after planned end date!");
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
}
