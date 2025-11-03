package de.szut.lf8_starter.project;

import de.szut.lf8_starter.customer.CustomerService;
import de.szut.lf8_starter.employee.EmployeeService;
import de.szut.lf8_starter.exceptionHandling.DateNotValidException;
import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import javax.sound.sampled.Port;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;

import java.util.List;
import java.util.Set;

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

    public List<ProjectEntity> getAllProjects() {
        return this.projectRepository.findAll();
    }

    public ProjectEntity getProjectById(Long id) {
        return this.projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project not found on id: " + id));
    }

    public ProjectEntity patchProject(Long id, Map<String,Object> fields) {
        ProjectEntity entityToPatch = this.projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found on id: " + id));

        fields.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(ProjectEntity.class, key);
            field.setAccessible(true);
            ReflectionUtils.setField(field, entityToPatch, value);
        });

        validateProjectEntity(entityToPatch);

        return projectRepository.save(entityToPatch);
    }
    private void validateProjectEntity(ProjectEntity projectEntity) {
        if (projectEntity.getStartDate() != null && projectEntity.getStartDate().isAfter(projectEntity.getPlannedEndDate())) {
            throw new DateNotValidException("Start date cannot be after planned end date!");
        }

        if (projectEntity.getActualEndDate() != null && projectEntity.getActualEndDate().isBefore(projectEntity.getStartDate())) {
            throw new DateNotValidException("Actual end date cannot be before start date!");
        }

        if (projectEntity.getCustomerId() != null && !customerService.checkIfCustomerExists(projectEntity.getCustomerId())) {
            throw new ResourceNotFoundException("Customer not found on id: " + projectEntity.getCustomerId());
        }

        if (projectEntity.getResponsibleEmployeeId() != null && !employeeService.checkIfEmployeeExists(projectEntity.getResponsibleEmployeeId())) {
            throw new ResourceNotFoundException("Employee not found on id: " + projectEntity.getResponsibleEmployeeId());
        }
    }

    public void deleteProjectById(final Long id) {
        ProjectEntity projectEntity = this.projectRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Project with ID " + id + " not found."));
        this.projectRepository.delete(projectEntity);
    }

    public void addEmployeeToProject(final Long projectId, ProjectAssignment projectAssignment) {
        ProjectEntity project = this.projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found on ID: " + projectId));

        Set<ProjectAssignment> projectAssignments = project.getAssignments();
        projectAssignments.add(projectAssignment);
        project.setAssignments(projectAssignments);

        this.employeeService.checkIfEmployeeExists(projectAssignment.getEmployeeId());
        this.employeeService.checkIfEmployeeHaveQualification(projectAssignment.getEmployeeId(), projectAssignment.getQualificationId());

        this.projectRepository.save(project);
    }
}
