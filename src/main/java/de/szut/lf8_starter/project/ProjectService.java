package de.szut.lf8_starter.project;

import de.szut.lf8_starter.customer.CustomerService;
import de.szut.lf8_starter.employee.EmployeeService;
import de.szut.lf8_starter.exceptionHandling.DateNotValidException;
import de.szut.lf8_starter.exceptionHandling.EmployeeAlreadyInThisProject;
import de.szut.lf8_starter.exceptionHandling.EmployeeNotAvailableException;
import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

import java.util.List;
import java.util.Objects;
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

    public ProjectEntity patchProject(Long id, Map<String, Object> fields) {
        ProjectEntity entityToPatch = this.getProjectById(id);

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
        ProjectEntity projectEntity = this.getProjectById(id);
        this.projectRepository.delete(projectEntity);
    }

    public void addEmployeeToProject(final Long projectId, ProjectAssignment newProjectAssignment) {
        ProjectEntity project = this.getProjectById(projectId);

        Set<ProjectAssignment> oldProjectAssignments = project.getAssignments();
        Long employeeId = newProjectAssignment.getEmployeeId();

        if (isEmployeeInProject(oldProjectAssignments, employeeId)) {
            throw new EmployeeAlreadyInThisProject("The Employee with ID " + employeeId + " is already a part of the project!");
        }

        this.employeeService.checkIfEmployeeExists(employeeId);
        this.employeeService.checkIfEmployeeHaveQualification(employeeId, newProjectAssignment.getQualificationId());

        if (project.getActualEndDate() == null) {
            this.isEmployeeAvailable(employeeId, project.getStartDate(), project.getPlannedEndDate());
        }

        oldProjectAssignments.add(newProjectAssignment);
        project.setAssignments(oldProjectAssignments);

        this.projectRepository.save(project);
    }

    public boolean isEmployeeInProject(Set<ProjectAssignment> oldProjectAssignments, Long employeeId) {
        for (ProjectAssignment oldProjectAssignment : oldProjectAssignments) {
            if (Objects.equals(oldProjectAssignment.getEmployeeId(), employeeId)) {
                return true;
            }
        }
        return false;
    }

    public void isEmployeeAvailable(Long employeeId, LocalDate startDate, LocalDate plannedEndDate) {
        List<ProjectEntity> projectEntities = this.getAllProjects();

        for (ProjectEntity project : projectEntities) {
            for (ProjectAssignment projectAssignment : project.getAssignments()) {
                if (projectAssignment.getEmployeeId().equals(employeeId)) {
                    LocalDate loadedStartDate = project.getStartDate();
                    LocalDate loadedPlannedEndDate = project.getPlannedEndDate();

                    boolean overlaps = ((startDate.isBefore(loadedPlannedEndDate) || startDate.isEqual(loadedPlannedEndDate))
                            && (plannedEndDate.isAfter(loadedStartDate) || plannedEndDate.isEqual(loadedStartDate)));

                    if (overlaps) {
                        throw new EmployeeNotAvailableException("Employee is already in a project in the period from " + loadedStartDate + " to " + loadedPlannedEndDate);
                    }
                }
            }
        }
    }

    public void removeEmployeeFromProject(Long projectId, Long employeeId) {
        ProjectEntity project = this.getProjectById(projectId);
        Set<ProjectAssignment> assignments = project.getAssignments();

        if (!employeeService.checkIfEmployeeExists(employeeId)) {
            throw new ResourceNotFoundException("Employee not found on id: " + employeeId);
        }
        if (!isEmployeeInProject(assignments, employeeId)) {
            throw new ResourceNotFoundException("The Employee with ID " + employeeId + " is not a part of the project!");
        }

        project.getAssignments().removeIf(assignment -> Objects.equals(assignment.getEmployeeId(), employeeId));
        projectRepository.save(project);
    }

    public List<ProjectEntity> getProjectsByEmployeeId(Long employeeId) {
        if (!employeeService.checkIfEmployeeExists(employeeId)) {
            throw new ResourceNotFoundException("Employee not found on id: " + employeeId);
        }

        List<ProjectEntity> allProjects = this.getAllProjects();
        List<ProjectEntity> employeeProjects = new ArrayList<>();

        for (ProjectEntity project : allProjects) {
            Set<ProjectAssignment> assignments = project.getAssignments();
            for (ProjectAssignment assignment : assignments) {
                if (assignment.getEmployeeId().equals(employeeId)) {
                    employeeProjects.add(project);
                    break;
                }
            }
        }
        return employeeProjects;
    }
}